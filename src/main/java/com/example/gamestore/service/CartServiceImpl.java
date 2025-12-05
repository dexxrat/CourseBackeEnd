package com.example.gamestore.service;

import com.example.gamestore.dto.CartDTO;
import com.example.gamestore.dto.CartItemDTO;
import com.example.gamestore.model.Cart;
import com.example.gamestore.model.CartItem;
import com.example.gamestore.model.Game;
import com.example.gamestore.model.User;
import com.example.gamestore.repository.CartRepository;
import com.example.gamestore.repository.GameRepository;
import com.example.gamestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(Long userId) {
        log.info("Getting cart for user: {}", userId);
        validateUserId(userId);

        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> createNewCart(userId));

        CartDTO cartDTO = convertToDTO(cart);
        log.info("Successfully retrieved cart with {} items for user: {}",
                cartDTO.getItems().size(), userId);

        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(Long userId, Long gameId, Integer quantity) {
        log.info("Adding item to cart - user: {}, game: {}, quantity: {}", userId, gameId, quantity);
        validateAddToCartParameters(userId, gameId, quantity);

        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> createNewCart(userId));

        Game game = getGameById(gameId);
        validateGameAvailability(game);

        Optional<CartItem> existingItemOpt = cartItemService.findByCartIdAndGameId(cart.getId(), gameId);

        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            cartItem.setQuantity(newQuantity);
            log.info("Updated existing cart item: {} to quantity: {}", game.getTitle(), newQuantity);
        } else {
            cartItem = createNewCartItem(cart, game, quantity);
            cart.getItems().add(cartItem);
            log.info("Added new cart item: {} with quantity: {}", game.getTitle(), quantity);
        }

        cartItemService.save(cartItem);
        recalculateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);

        CartDTO cartDTO = convertToDTO(savedCart);
        log.info("Cart successfully updated. Total: ${} for user: {}",
                savedCart.getTotalPrice(), userId);

        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long userId, Long itemId, Integer quantity) {
        log.info("Updating cart item - user: {}, item: {}, quantity: {}", userId, itemId, quantity);
        validateUpdateCartItemParameters(userId, itemId);

        CartItem cartItem = cartItemService.findByIdWithAssociations(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + itemId));

        validateCartItemOwnership(cartItem, userId);

        Cart cart = cartItem.getCart();

        if (quantity == null || quantity <= 0) {
            removeCartItem(cart, cartItem, itemId);
            log.info("Removed cart item: {} due to zero quantity", itemId);
        } else {
            updateCartItemQuantity(cartItem, quantity, itemId);
            log.info("Updated cart item: {} to quantity: {}", itemId, quantity);
        }

        recalculateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);

        return convertToDTO(savedCart);
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long itemId) {
        log.info("Removing item from cart - user: {}, item: {}", userId, itemId);
        validateRemoveItemParameters(userId, itemId);

        CartItem cartItem = cartItemService.findByIdWithAssociations(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + itemId));

        validateCartItemOwnership(cartItem, userId);

        Cart cart = cartItem.getCart();
        removeCartItem(cart, cartItem, itemId);

        log.info("Successfully removed cart item: {} for user: {}", itemId, userId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        log.info("Clearing cart for user: {}", userId);
        validateUserId(userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        cartItemService.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);

        log.info("Successfully cleared cart for user: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isGameInUserCart(Long userId, Long gameId) {
        validateUserId(userId);
        if (gameId == null) {
            throw new RuntimeException("Game ID cannot be null");
        }

        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isEmpty()) {
            return false;
        }

        boolean exists = cartItemService.existsByCartIdAndGameId(cartOpt.get().getId(), gameId);
        log.debug("Game {} in user {} cart: {}", gameId, userId, exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        validateUserId(userId);

        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isEmpty()) {
            return 0;
        }

        int count = (int) cartItemService.countByCartId(cartOpt.get().getId());
        log.debug("Cart item count for user {}: {}", userId, count);
        return count;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCartTotalPrice(Long userId) {
        validateUserId(userId);

        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        if (cartOpt.isEmpty()) {
            return BigDecimal.ZERO;
        }

        Cart cart = cartOpt.get();
        return cart.getTotalPrice() != null ? cart.getTotalPrice() : BigDecimal.ZERO;
    }



    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new RuntimeException("User ID cannot be null");
        }
    }

    private void validateAddToCartParameters(Long userId, Long gameId, Integer quantity) {
        if (userId == null) {
            throw new RuntimeException("User ID cannot be null");
        }
        if (gameId == null) {
            throw new RuntimeException("Game ID cannot be null");
        }
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }
        if (quantity > 100) {
            throw new RuntimeException("Quantity cannot exceed 100");
        }
    }

    private void validateUpdateCartItemParameters(Long userId, Long itemId) {
        if (userId == null || itemId == null) {
            throw new RuntimeException("User ID and Item ID cannot be null");
        }
    }

    private void validateRemoveItemParameters(Long userId, Long itemId) {
        if (userId == null || itemId == null) {
            throw new RuntimeException("User ID and Item ID cannot be null");
        }
    }

    private void validateCartItemOwnership(CartItem cartItem, Long userId) {
        if (!cartItem.getCart().getUser().getId().equals(userId)) {
            throw new RuntimeException("Cart item does not belong to user");
        }
    }

    private Game getGameById(Long gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found with id: " + gameId));
    }

    private void validateGameAvailability(Game game) {
        if (Boolean.FALSE.equals(game.getActive())) {
            throw new RuntimeException("Game is not available: " + game.getTitle());
        }
    }



    private CartItem createNewCartItem(Cart cart, Game game, Integer quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setGame(game);
        cartItem.setQuantity(quantity);

        BigDecimal itemPrice = (game.getDiscountPrice() != null && game.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0)
                ? game.getDiscountPrice()
                : game.getPrice();
        cartItem.setPrice(itemPrice);

        return cartItem;
    }

    private void removeCartItem(Cart cart, CartItem cartItem, Long itemId) {
        cart.getItems().remove(cartItem);
        cartItemService.delete(cartItem);
    }

    private void updateCartItemQuantity(CartItem cartItem, Integer quantity, Long itemId) {
        cartItem.setQuantity(quantity);
        cartItemService.save(cartItem);
    }

    private void recalculateCartTotal(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems()) {
                if (item.getPrice() != null && item.getQuantity() != null) {
                    BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                    total = total.add(itemTotal);
                }
            }
        }
        cart.setTotalPrice(total);
        cart.setUpdatedAt(LocalDateTime.now());
    }

    private Cart createNewCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());
        Cart savedCart = cartRepository.save(cart);

        log.info("Created new cart for user: {}", userId);
        return savedCart;
    }



    private CartDTO convertToDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setTotalPrice(cart.getTotalPrice() != null ? cart.getTotalPrice() : BigDecimal.ZERO);
        dto.setItems(convertItemsToDTO(cart.getItems()));
        return dto;
    }

    private List<CartItemDTO> convertItemsToDTO(List<CartItem> items) {
        List<CartItemDTO> itemDTOs = new ArrayList<>();
        if (items != null && !items.isEmpty()) {
            for (CartItem item : items) {
                try {
                    CartItemDTO itemDTO = convertItemToDTO(item);
                    if (itemDTO != null) {
                        itemDTOs.add(itemDTO);
                    }
                } catch (Exception e) {
                    log.warn("Failed to convert cart item to DTO: {}", e.getMessage());
                }
            }
        }
        return itemDTOs;
    }

    private CartItemDTO convertItemToDTO(CartItem item) {
        if (item == null || item.getGame() == null) {
            return null;
        }

        CartItemDTO dto = new CartItemDTO();
        dto.setId(item.getId());
        dto.setGameId(item.getGame().getId());
        dto.setGameTitle(item.getGame().getTitle());
        dto.setImageUrl(item.getGame().getImageUrl());
        dto.setQuantity(item.getQuantity() != null ? item.getQuantity() : 0);
        dto.setPrice(item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO);

        BigDecimal subtotal = BigDecimal.ZERO;
        if (item.getPrice() != null && item.getQuantity() != null) {
            subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        }
        dto.setSubtotal(subtotal);

        dto.setPlatform(item.getGame().getPlatform());
        dto.setDeveloper(item.getGame().getDeveloper());

        return dto;
    }



    @Override
    @Transactional
    public void cleanupInactiveGameItems() {
        List<Cart> allCarts = cartRepository.findAll();
        int removedCount = 0;

        for (Cart cart : allCarts) {
            if (cart.getItems() != null) {
                List<CartItem> itemsToRemove = cart.getItems().stream()
                        .filter(item -> item.getGame() != null && Boolean.FALSE.equals(item.getGame().getActive()))
                        .toList();

                if (!itemsToRemove.isEmpty()) {
                    for (CartItem item : itemsToRemove) {
                        cartItemService.delete(item);
                        cart.getItems().remove(item);
                        removedCount++;
                    }
                    recalculateCartTotal(cart);
                    cartRepository.save(cart);
                }
            }
        }

        log.info("Cleaned up {} inactive game items from carts", removedCount);
    }

    @Override
    @Transactional
    public void recalculateAllCartsTotals() {
        List<Cart> allCarts = cartRepository.findAll();
        for (Cart cart : allCarts) {
            recalculateCartTotal(cart);
        }
        cartRepository.saveAll(allCarts);
        log.info("Recalculated totals for {} carts", allCarts.size());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getUsersWithNonEmptyCarts() {
        return cartRepository.findAll().stream()
                .filter(cart -> cart.getItems() != null && !cart.getItems().isEmpty())
                .map(cart -> cart.getUser().getId())
                .distinct()
                .toList();
    }
}
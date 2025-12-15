package com.example.gamestore.service;

import com.example.gamestore.dto.CartDTO;
import com.example.gamestore.dto.CartItemDTO;
import com.example.gamestore.model.*;
import com.example.gamestore.repository.CartRepository;
import com.example.gamestore.repository.GameRepository;
import com.example.gamestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    @Override
    @Transactional(readOnly = true)
    public CartDTO getCartByUserId(Long userId) {
        validateUserId(userId);

        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> createNewCart(userId));

        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public CartDTO addItemToCart(Long userId, Long gameId, Integer quantity) {
        validateUserId(userId);
        validateGameId(gameId);
        validateQuantity(quantity);

        Cart cart = cartRepository.findByUserIdWithItems(userId)
                .orElseGet(() -> createNewCart(userId));

        Game game = getGameById(gameId);
        validateGameAvailability(game);

        Optional<CartItem> existingItemOpt = cartItemService.findByCartIdAndGameId(cart.getId(), gameId);

        CartItem cartItem;
        if (existingItemOpt.isPresent()) {
            cartItem = existingItemOpt.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = createNewCartItem(cart, game, quantity);
            cart.getItems().add(cartItem);
        }

        cartItemService.save(cartItem);
        recalculateCartTotal(cart);
        Cart savedCart = cartRepository.save(cart);

        return convertToDTO(savedCart);
    }

    @Override
    @Transactional
    public CartDTO updateCartItem(Long userId, Long itemId, Integer quantity) {
        validateUserId(userId);

        CartItem cartItem = cartItemService.findByIdWithAssociations(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + itemId));

        validateCartItemOwnership(cartItem, userId);

        if (quantity == null || quantity <= 0) {
            removeCartItem(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemService.save(cartItem);
        }

        Cart cart = cartItem.getCart();
        recalculateCartTotal(cart);
        cartRepository.save(cart);

        return convertToDTO(cart);
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long itemId) {
        validateUserId(userId);

        CartItem cartItem = cartItemService.findByIdWithAssociations(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + itemId));

        validateCartItemOwnership(cartItem, userId);
        removeCartItem(cartItem);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        validateUserId(userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        cartItemService.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cart.setUpdatedAt(LocalDateTime.now());
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isGameInUserCart(Long userId, Long gameId) {
        validateUserId(userId);
        validateGameId(gameId);

        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        return cartOpt.isPresent() &&
                cartItemService.existsByCartIdAndGameId(cartOpt.get().getId(), gameId);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemCount(Long userId) {
        validateUserId(userId);

        Optional<Cart> cartOpt = cartRepository.findByUserId(userId);
        return cartOpt.map(cart -> (int) cartItemService.countByCartId(cart.getId()))
                .orElse(0);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCartTotalPrice(Long userId) {
        validateUserId(userId);

        return cartRepository.findByUserId(userId)
                .map(cart -> cart.getTotalPrice() != null ? cart.getTotalPrice() : BigDecimal.ZERO)
                .orElse(BigDecimal.ZERO);
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new RuntimeException("User ID cannot be null");
        }
    }

    private void validateGameId(Long gameId) {
        if (gameId == null) {
            throw new RuntimeException("Game ID cannot be null");
        }
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be positive");
        }
        if (quantity > 100) {
            throw new RuntimeException("Quantity cannot exceed 100");
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
        cartItem.setPrice(game.getFinalPrice());
        return cartItem;
    }

    private void removeCartItem(CartItem cartItem) {
        Cart cart = cartItem.getCart();
        cart.getItems().remove(cartItem);
        cartItemService.delete(cartItem);
        recalculateCartTotal(cart);
        cartRepository.save(cart);
    }

    private void recalculateCartTotal(Cart cart) {
        BigDecimal total = BigDecimal.ZERO;
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            for (CartItem item : cart.getItems()) {
                if (item.getPrice() != null && item.getQuantity() != null) {
                    total = total.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
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
        return cartRepository.save(cart);
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
                CartItemDTO itemDTO = convertItemToDTO(item);
                if (itemDTO != null) {
                    itemDTOs.add(itemDTO);
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
}
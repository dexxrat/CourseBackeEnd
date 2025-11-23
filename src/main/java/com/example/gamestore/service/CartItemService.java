package com.example.gamestore.service;

import com.example.gamestore.model.CartItem;
import com.example.gamestore.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    @Transactional(readOnly = true)
    public Optional<CartItem> findById(Long id) {
        return cartItemRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<CartItem> findByIdWithAssociations(Long id) {
        return cartItemRepository.findByIdWithAssociations(id);
    }

    @Transactional(readOnly = true)
    public List<CartItem> findByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    @Transactional(readOnly = true)
    public List<CartItem> findByCartIdWithGame(Long cartId) {
        return cartItemRepository.findByCartIdWithGame(cartId);
    }

    @Transactional(readOnly = true)
    public Optional<CartItem> findByCartIdAndGameId(Long cartId, Long gameId) {
        return cartItemRepository.findByCartIdAndGameId(cartId, gameId);
    }

    @Transactional(readOnly = true)
    public boolean existsByCartIdAndGameId(Long cartId, Long gameId) {
        return cartItemRepository.existsByCartIdAndGameId(cartId, gameId);
    }

    @Transactional(readOnly = true)
    public long countByCartId(Long cartId) {
        return cartItemRepository.countByCartId(cartId);
    }

    @Transactional(readOnly = true)
    public Double getCartTotalPrice(Long cartId) {
        return cartItemRepository.getCartTotalPrice(cartId);
    }

    @Transactional
    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void delete(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }

    @Transactional
    public void deleteByCartId(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }

    @Transactional
    public void deleteByCartIdAndGameId(Long cartId, Long gameId) {
        cartItemRepository.deleteByCartIdAndGameId(cartId, gameId);
    }

    @Transactional
    public int updateQuantity(Long cartId, Long itemId, Integer quantity) {
        return cartItemRepository.updateQuantity(cartId, itemId, quantity);
    }

    @Transactional(readOnly = true)
    public List<CartItem> findByUserId(Long userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<CartItem> findByUserIdWithGames(Long userId) {
        return cartItemRepository.findByUserIdWithGames(userId);
    }

    /**
     * Очищает корзину от неактивных игр
     */
    @Transactional
    public int cleanupInactiveGameItems() {
        int deletedCount = cartItemRepository.deleteInactiveGameItems();
        log.info("Cleaned up {} inactive game items from carts", deletedCount);
        return deletedCount;
    }

    /**
     * Удаляет дубликаты элементов в корзине
     */
    @Transactional
    public int removeDuplicateItems(Long cartId) {
        List<CartItem> duplicates = cartItemRepository.findDuplicateItems(cartId);
        if (!duplicates.isEmpty()) {
            cartItemRepository.deleteAll(duplicates);
            log.info("Removed {} duplicate items from cart {}", duplicates.size(), cartId);
            return duplicates.size();
        }
        return 0;
    }

    /**
     * Проверяет, принадлежит ли элемент корзины пользователю
     */
    @Transactional(readOnly = true)
    public boolean isCartItemOwnedByUser(Long cartItemId, Long userId) {
        return cartItemRepository.findByUserId(userId).stream()
                .anyMatch(item -> item.getId().equals(cartItemId));
    }
}
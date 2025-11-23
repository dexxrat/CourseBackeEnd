package com.example.gamestore.service;

import com.example.gamestore.dto.CartDTO;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    CartDTO addItemToCart(Long userId, Long gameId, Integer quantity);
    CartDTO updateCartItem(Long userId, Long itemId, Integer quantity);
    void removeItemFromCart(Long userId, Long itemId);
    void clearCart(Long userId);

    // Методы проверки состояния корзины
    boolean isGameInUserCart(Long userId, Long gameId);
    int getCartItemCount(Long userId);
    BigDecimal getCartTotalPrice(Long userId);

    // Методы для администрирования
    void cleanupInactiveGameItems();
    void recalculateAllCartsTotals();
    List<Long> getUsersWithNonEmptyCarts();
}
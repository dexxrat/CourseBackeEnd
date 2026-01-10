package com.example.gamestore.service;

import com.example.gamestore.dto.CartDTO;
import java.math.BigDecimal;

public interface CartService {
    CartDTO getCartByUserId(Long userId);
    CartDTO addItemToCart(Long userId, Long gameId, Integer quantity);
    CartDTO updateCartItem(Long userId, Long itemId, Integer quantity);
    void removeItemFromCart(Long userId, Long itemId);
    void clearCart(Long userId);

    boolean isGameInUserCart(Long userId, Long gameId);
    int getCartItemCount(Long userId);
    BigDecimal getCartTotalPrice(Long userId);
}
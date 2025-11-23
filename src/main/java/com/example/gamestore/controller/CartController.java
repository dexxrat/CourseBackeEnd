package com.example.gamestore.controller;

import com.example.gamestore.dto.CartDTO;
import com.example.gamestore.service.CartService;
import com.example.gamestore.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<CartDTO> getCart() {
        try {
            Long userId = securityUtils.getCurrentUserId();
            CartDTO cart = cartService.getCartByUserId(userId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addToCart(@RequestBody AddToCartRequest request) {
        try {
            Long userId = securityUtils.getCurrentUserId();
            CartDTO cart = cartService.addItemToCart(userId, request.getGameId(), request.getQuantity());
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request) {
        try {
            Long userId = securityUtils.getCurrentUserId();
            CartDTO cart = cartService.updateCartItem(userId, itemId, request.getQuantity());
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long itemId) {
        try {
            Long userId = securityUtils.getCurrentUserId();
            cartService.removeItemFromCart(userId, itemId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        try {
            Long userId = securityUtils.getCurrentUserId();
            cartService.clearCart(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public static class AddToCartRequest {
        private Long gameId;
        private Integer quantity = 1;

        public Long getGameId() { return gameId; }
        public void setGameId(Long gameId) { this.gameId = gameId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class UpdateCartItemRequest {
        private Integer quantity;

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
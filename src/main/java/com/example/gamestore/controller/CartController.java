package com.example.gamestore.controller;

import com.example.gamestore.dto.CartDTO;
import com.example.gamestore.service.CartService;
import com.example.gamestore.util.SecurityUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<CartDTO> getCart() {
        Long userId = securityUtils.getCurrentUserId();
        CartDTO cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartDTO> addToCart(@RequestBody AddToCartRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        CartDTO cart = cartService.addItemToCart(userId, request.getGameId(), request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDTO> updateCartItem(
            @PathVariable Long itemId,
            @RequestBody UpdateCartItemRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        CartDTO cart = cartService.updateCartItem(userId, itemId, request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeCartItem(@PathVariable Long itemId) {
        Long userId = securityUtils.getCurrentUserId();
        cartService.removeItemFromCart(userId, itemId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        Long userId = securityUtils.getCurrentUserId();
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }

    // TODO move these classes in dto folder
    @Getter
    @Setter
    public static class AddToCartRequest {
        private Long gameId;
        private Integer quantity = 1;
    }

    @Getter
    @Setter
    public static class UpdateCartItemRequest {
        private Integer quantity;
    }
}
package com.example.gamestore.controller;

import com.example.gamestore.dto.OrderDTO;
import com.example.gamestore.service.OrderService;
import com.example.gamestore.util.SecurityUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder() {
        Long userId = securityUtils.getCurrentUserId();
        OrderDTO order = orderService.createOrder(userId);
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getUserOrders() {
        Long userId = securityUtils.getCurrentUserId();
        List<OrderDTO> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        Long userId = securityUtils.getCurrentUserId();
        OrderDTO order = orderService.getOrder(userId, orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateStatusRequest request) {
        OrderDTO order = orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Data
    static class UpdateStatusRequest {
        private String status;
    }
}
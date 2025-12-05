package com.example.gamestore.controller;

import com.example.gamestore.dto.OrderDTO;
import com.example.gamestore.service.OrderService;
import com.example.gamestore.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<OrderDTO> createOrder() {
        log.info("Creating new order");
        Long userId = securityUtils.getCurrentUserId();
        log.info("User ID for order creation: {}", userId);

        OrderDTO order = orderService.createOrder(userId);
        log.info("Order created successfully with ID: {}", order.getId());

        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getUserOrders() {
        log.info("Getting user orders");
        Long userId = securityUtils.getCurrentUserId();
        List<OrderDTO> orders = orderService.getUserOrders(userId);
        log.info("Found {} orders for user {}", orders.size(), userId);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        log.info("Getting order {}", orderId);
        Long userId = securityUtils.getCurrentUserId();
        OrderDTO order = orderService.getOrder(userId, orderId);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateStatusRequest request) {
        log.info("Updating order {} status to: {}", orderId, request.getStatus());
        OrderDTO order = orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        log.info("Getting all orders (admin)");
        List<OrderDTO> orders = orderService.getAllOrders();
        log.info("Found {} total orders", orders.size());
        return ResponseEntity.ok(orders);
    }

    @PutMapping("/admin/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatusAdmin(
            @PathVariable Long orderId,
            @RequestBody UpdateStatusRequest request) {
        log.info("Admin updating order {} status to: {}", orderId, request.getStatus());
        OrderDTO order = orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.ok(order);
    }


    public static class UpdateStatusRequest {
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
package com.example.gamestore.controller;

import com.example.gamestore.dto.OrderDTO;
import com.example.gamestore.service.OrderService;
import com.example.gamestore.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> createOrder() {
        try {
            log.info("Creating new order");
            Long userId = securityUtils.getCurrentUserId();
            log.info("User ID for order creation: {}", userId);

            OrderDTO order = orderService.createOrder(userId);
            log.info("Order created successfully with ID: {}", order.getId());

            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            log.error("Error creating order: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error creating order: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Ошибка при создании заказа"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserOrders() {
        try {
            log.info("Getting user orders");
            Long userId = securityUtils.getCurrentUserId();
            List<OrderDTO> orders = orderService.getUserOrders(userId);
            log.info("Found {} orders for user {}", orders.size(), userId);
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            log.error("Error getting user orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error getting user orders: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Ошибка при загрузке заказов"));
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        try {
            log.info("Getting order {}", orderId);
            Long userId = securityUtils.getCurrentUserId();
            OrderDTO order = orderService.getOrder(userId, orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            log.warn("Order not found or access denied: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting order {}: {}", orderId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Ошибка при загрузке заказа"));
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateStatusRequest request) {
        try {
            log.info("Updating order {} status to: {}", orderId, request.getStatus());
            OrderDTO order = orderService.updateOrderStatus(orderId, request.getStatus());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            log.error("Error updating order status: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error updating order status: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Ошибка при обновлении статуса заказа"));
        }
    }

    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllOrders() {
        try {
            log.info("Getting all orders (admin)");
            List<OrderDTO> orders = orderService.getAllOrders();
            log.info("Found {} total orders", orders.size());
            return ResponseEntity.ok(orders);
        } catch (RuntimeException e) {
            log.error("Error getting all orders: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error getting all orders: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(new ErrorResponse("Ошибка при загрузке всех заказов"));
        }
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

    public static class ErrorResponse {
        private String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
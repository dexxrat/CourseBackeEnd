package com.example.gamestore.controller;

import com.example.gamestore.dto.OrderDTO;
import com.example.gamestore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request.getStatus()));
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
package com.example.gamestore.service;

import com.example.gamestore.dto.OrderDTO;
import java.util.List;

public interface OrderService {
    OrderDTO createOrder(Long userId);
    List<OrderDTO> getUserOrders(Long userId);
    OrderDTO getOrder(Long userId, Long orderId);
    OrderDTO updateOrderStatus(Long orderId, String status);
    List<OrderDTO> getAllOrders();
}
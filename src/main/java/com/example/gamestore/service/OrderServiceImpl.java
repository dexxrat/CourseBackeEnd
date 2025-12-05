package com.example.gamestore.service;

import com.example.gamestore.dto.OrderDTO;
import com.example.gamestore.dto.OrderItemDTO;
import com.example.gamestore.model.*;
import com.example.gamestore.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final GameRepository gameRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId) {
        log.info("Creating order for user: {}", userId);

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for user: " + userId));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Order order = new Order();
        order.setUser(user);
        order.setItems(new ArrayList<>());

        for (CartItem cartItem : cart.getItems()) {
            Game game = gameRepository.findById(cartItem.getGame().getId())
                    .orElseThrow(() -> new RuntimeException("Game not found: " + cartItem.getGame().getId()));

            if (game.getActive() != null && !game.getActive()) {
                throw new RuntimeException("Game is not available: " + game.getTitle());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setGame(game);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPrice());

            order.getItems().add(orderItem);
            log.info("Added order item: {} x {} at ${}", game.getTitle(), cartItem.getQuantity(), cartItem.getPrice());
        }

        order.recalculateTotal();
        log.info("Order total: ${}", order.getTotalAmount());

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        cartItemRepository.deleteByCartId(cart.getId());
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
        log.info("Cart cleared for user: {}", userId);

        return convertToDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrders(Long userId) {
        log.info("Getting orders for user: {}", userId);
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);

        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.info("Found {} orders for user: {}", orderDTOs.size(), userId);
        return orderDTOs;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrder(Long userId, Long orderId) {
        log.info("Getting order {} for user: {}", orderId, userId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Order does not belong to user");
        }

        return convertToDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, String status) {
        log.info("Updating order {} status to: {}", orderId, status);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);
            log.info("Order {} status updated to: {}", orderId, newStatus);
            return convertToDTO(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        log.info("Getting all orders");
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();

        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        log.info("Found {} total orders", orderDTOs.size());
        return orderDTOs;
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus().name());
        dto.setTotalAmount(order.getTotalAmount());

        if (order.getUser() != null) {
            dto.setUserId(order.getUser().getId());
            dto.setUserName(order.getUser().getUsername());
            dto.setUserEmail(order.getUser().getEmail());
        }

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            List<OrderItemDTO> itemDTOs = order.getItems().stream()
                    .map(this::convertItemToDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemDTOs);
        } else {
            dto.setItems(new ArrayList<>());
        }

        return dto;
    }

    private OrderItemDTO convertItemToDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());

        if (item.getGame() != null) {
            dto.setGameId(item.getGame().getId());
            dto.setGameTitle(item.getGame().getTitle());
            dto.setImageUrl(item.getGame().getImageUrl());
            dto.setPlatform(item.getGame().getPlatform());
            dto.setDeveloper(item.getGame().getDeveloper());
        }

        dto.setQuantity(item.getQuantity());
        dto.setPriceAtPurchase(item.getPriceAtPurchase());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}
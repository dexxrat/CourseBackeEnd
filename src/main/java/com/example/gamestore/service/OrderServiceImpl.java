package com.example.gamestore.service;

import com.example.gamestore.dto.OrderDTO;
import com.example.gamestore.dto.OrderItemDTO;
import com.example.gamestore.model.*;
import com.example.gamestore.repository.CartRepository;
import com.example.gamestore.repository.OrderRepository;
import com.example.gamestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    @Override
    @Transactional
    public OrderDTO createOrder(Long userId) {
        Cart cart = cartRepository.findByUserIdWithItems(userId)
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
            Game game = cartItem.getGame();

            if (game.getActive() != null && !game.getActive()) {
                throw new RuntimeException("Game is not available: " + game.getTitle());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setGame(game);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(cartItem.getPrice());

            order.getItems().add(orderItem);
        }

        order.recalculateTotal();
        Order savedOrder = orderRepository.save(order);

        cartItemService.deleteByCartId(cart.getId());
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);

        return convertToDTO(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getUserOrders(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByOrderDateDesc(userId);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrder(Long userId, Long orderId) {
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
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);
            return convertToDTO(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid order status: " + status);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByOrderDateDesc();
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO(
                order.getId(),
                order.getOrderDate(),
                order.getStatus().name(),
                order.getTotalAmount()
        );

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
package com.example.gamestore.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items = new ArrayList<>();
    private Long userId;
    private String userName;
    private String userEmail;

    public OrderDTO(Long id, LocalDateTime orderDate, String status, BigDecimal totalAmount) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public void addItem(OrderItemDTO item) {
        this.items.add(item);
    }

    public BigDecimal calculateTotalAmount() {
        if (items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(OrderItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return items.size();
    }

    public boolean hasItems() {
        return !items.isEmpty();
    }
}
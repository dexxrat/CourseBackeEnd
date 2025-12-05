package com.example.gamestore.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;
    private List<OrderItemDTO> items = new ArrayList<>();
    private Long userId;
    private String userName;
    private String userEmail;


    public OrderDTO() {
    }

    public OrderDTO(Long id, LocalDateTime orderDate, String status, BigDecimal totalAmount) {
        this.id = id;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = totalAmount;
    }


    public void addItem(OrderItemDTO item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
    }

    public void removeItem(OrderItemDTO item) {
        if (this.items != null) {
            this.items.remove(item);
        }
    }

    public BigDecimal calculateTotalAmount() {
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return items.stream()
                .map(OrderItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return items != null ? items.size() : 0;
    }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", totalAmount=" + totalAmount +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
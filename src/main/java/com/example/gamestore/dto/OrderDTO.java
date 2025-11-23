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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items != null ? items : new ArrayList<>();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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
package com.example.gamestore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Data
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    @PreUpdate
    public void prePersist() {
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (totalPrice == null) {
            totalPrice = BigDecimal.ZERO;
        }
        if (items == null) {
            items = new ArrayList<>();
        }
    }

    public void recalculateTotal() {
        if (items != null) {
            this.totalPrice = items.stream()
                    .map(item -> {
                        if (item.getPrice() != null && item.getQuantity() != null) {
                            return item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                        }
                        return BigDecimal.ZERO;
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
        this.updatedAt = LocalDateTime.now();
    }
}
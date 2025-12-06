package com.example.gamestore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "games")
@Data
@NoArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String developer;

    @Column(length = 100)
    private String publisher;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Column(length = 50)
    private String platform;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "game_genres", joinColumns = @JoinColumn(name = "game_id"))
    @Column(name = "genres")
    private Set<String> genres = new HashSet<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "discount_price", precision = 10, scale = 2)
    private BigDecimal discountPrice;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public BigDecimal getFinalPrice() {
        return discountPrice != null ? discountPrice : price;
    }

    public boolean hasDiscount() {
        return discountPrice != null && discountPrice.compareTo(price) < 0;
    }

    public BigDecimal getDiscountPercentage() {
        if (!hasDiscount()) {
            return BigDecimal.ZERO;
        }
        BigDecimal discountAmount = price.subtract(discountPrice);
        return discountAmount.divide(price, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}
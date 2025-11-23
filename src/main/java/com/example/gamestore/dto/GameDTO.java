package com.example.gamestore.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class GameDTO {
    private Long id;
    private String title;
    private String description;
    private String developer;
    private String publisher;
    private LocalDate releaseDate;
    private String platform;
    private Set<String> genres;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String imageUrl;
    private BigDecimal finalPrice;
    private boolean hasDiscount;
    private BigDecimal discountPercentage;
}
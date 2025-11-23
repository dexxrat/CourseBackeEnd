package com.example.gamestore.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemDTO {
    private Long id;
    private Long gameId;
    private String gameTitle;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private String platform;
    private String developer;
}
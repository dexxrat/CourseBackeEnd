package com.example.gamestore.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private List<CartItemDTO> items;
}
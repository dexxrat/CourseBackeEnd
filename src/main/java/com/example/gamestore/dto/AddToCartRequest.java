package com.example.gamestore.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
    private Long gameId;
    private Integer quantity = 1;
}
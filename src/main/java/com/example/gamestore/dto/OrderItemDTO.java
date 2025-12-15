package com.example.gamestore.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;
    private Long gameId;
    private String gameTitle;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal priceAtPurchase;
    private BigDecimal subtotal;
    private String platform;
    private String developer;

    public void calculateSubtotal() {
        if (priceAtPurchase != null && quantity != null) {
            this.subtotal = priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public boolean isValid() {
        return gameId != null &&
                gameTitle != null &&
                !gameTitle.trim().isEmpty() &&
                quantity != null && quantity > 0 &&
                priceAtPurchase != null && priceAtPurchase.compareTo(BigDecimal.ZERO) >= 0;
    }
}
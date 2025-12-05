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


    public OrderItemDTO() {
    }

    public OrderItemDTO(Long id, Long gameId, String gameTitle, Integer quantity,
                        BigDecimal priceAtPurchase, BigDecimal subtotal) {
        this.id = id;
        this.gameId = gameId;
        this.gameTitle = gameTitle;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
        this.subtotal = subtotal;
    }


    public void calculateSubtotal() {
        if (priceAtPurchase != null && quantity != null) {
            this.subtotal = priceAtPurchase.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public void incrementQuantity() {
        if (quantity != null) {
            this.quantity = quantity + 1;
            calculateSubtotal();
        }
    }

    public void decrementQuantity() {
        if (quantity != null && quantity > 1) {
            this.quantity = quantity - 1;
            calculateSubtotal();
        }
    }

    public boolean isValid() {
        return gameId != null &&
                gameTitle != null &&
                !gameTitle.trim().isEmpty() &&
                quantity != null && quantity > 0 &&
                priceAtPurchase != null && priceAtPurchase.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static OrderItemDTO of(Long gameId, String gameTitle, Integer quantity, BigDecimal priceAtPurchase) {
        OrderItemDTO item = new OrderItemDTO();
        item.setGameId(gameId);
        item.setGameTitle(gameTitle);
        item.setQuantity(quantity);
        item.setPriceAtPurchase(priceAtPurchase);
        item.calculateSubtotal();
        return item;
    }

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "id=" + id +
                ", gameId=" + gameId +
                ", gameTitle='" + gameTitle + '\'' +
                ", quantity=" + quantity +
                ", priceAtPurchase=" + priceAtPurchase +
                ", subtotal=" + subtotal +
                '}';
    }
}
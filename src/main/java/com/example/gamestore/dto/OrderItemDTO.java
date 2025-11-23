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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getQuantity() {
        return quantity != null ? quantity : 0;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity != null ? quantity : 0;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase != null ? priceAtPurchase : BigDecimal.ZERO;
    }

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase != null ? priceAtPurchase : BigDecimal.ZERO;
    }

    public BigDecimal getSubtotal() {
        if (subtotal != null) {
            return subtotal;
        }
        return getPriceAtPurchase().multiply(BigDecimal.valueOf(getQuantity()));
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public void calculateSubtotal() {
        this.subtotal = getPriceAtPurchase().multiply(BigDecimal.valueOf(getQuantity()));
    }

    public void incrementQuantity() {
        this.quantity = getQuantity() + 1;
        calculateSubtotal();
    }

    public void decrementQuantity() {
        if (getQuantity() > 1) {
            this.quantity = getQuantity() - 1;
            calculateSubtotal();
        }
    }

    public boolean isValid() {
        return gameId != null &&
                gameTitle != null &&
                !gameTitle.trim().isEmpty() &&
                getQuantity() > 0 &&
                getPriceAtPurchase().compareTo(BigDecimal.ZERO) >= 0;
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

    public static OrderItemDTO of(Long gameId, String gameTitle, Integer quantity, BigDecimal priceAtPurchase) {
        OrderItemDTO item = new OrderItemDTO();
        item.setGameId(gameId);
        item.setGameTitle(gameTitle);
        item.setQuantity(quantity);
        item.setPriceAtPurchase(priceAtPurchase);
        item.calculateSubtotal();
        return item;
    }
}
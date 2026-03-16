package com.fuegolento.backend.dto;

import java.math.BigDecimal;

public class OrderItemDTO {

    private Long id;
    private Long dishId;
    private String dishName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String kitchenNote;
    private String meatPoint;

    public OrderItemDTO() {
    }

    public OrderItemDTO(Long id, Long dishId, String dishName, int quantity,
                        BigDecimal unitPrice, BigDecimal totalPrice,
                        String kitchenNote, String meatPoint) {
        this.id = id;
        this.dishId = dishId;
        this.dishName = dishName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.kitchenNote = kitchenNote;
        this.meatPoint = meatPoint;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getKitchenNote() {
        return kitchenNote;
    }

    public void setKitchenNote(String kitchenNote) {
        this.kitchenNote = kitchenNote;
    }

    public String getMeatPoint() {
        return meatPoint;
    }

    public void setMeatPoint(String meatPoint) {
        this.meatPoint = meatPoint;
    }
}
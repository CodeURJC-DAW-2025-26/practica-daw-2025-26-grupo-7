package com.fuegolento.backend.dto;

public class CartItemRequestDTO {

    private Long dishId;
    private int quantity;

    public CartItemRequestDTO() {
    }

    public CartItemRequestDTO(Long dishId, int quantity) {
        this.dishId = dishId;
        this.quantity = quantity;
    }

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
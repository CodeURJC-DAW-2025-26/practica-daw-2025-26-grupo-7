package com.fuegolento.backend.dto;

import com.fuegolento.backend.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    private Long id;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private Long userId;
    private String username;
    private List<OrderItemDTO> items;
    private BigDecimal totalPrice;
    private Integer tableNumber;
    private String customerNote;

    public OrderDTO() {
    }

    public OrderDTO(Long id, LocalDateTime createdAt, OrderStatus status,
                    Long userId, String username, List<OrderItemDTO> items,
                    BigDecimal totalPrice, Integer tableNumber, String customerNote) {
        this.id = id;
        this.createdAt = createdAt;
        this.status = status;
        this.userId = userId;
        this.username = username;
        this.items = items;
        this.totalPrice = totalPrice;
        this.tableNumber = tableNumber;
        this.customerNote = customerNote;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }
}
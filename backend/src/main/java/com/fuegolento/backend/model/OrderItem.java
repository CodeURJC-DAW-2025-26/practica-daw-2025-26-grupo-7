package com.fuegolento.backend.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a single line of an order.
 * It links a dish with a quantity and stores the unit price at the time of purchase.
 */
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Dish associated with this order item
    @ManyToOne(optional = false)
    @JoinColumn(name = "dish_id")
    private Dish dish;

    // Order that owns this item
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    // Quantity of the dish in this order
    private int quantity;

    /**
     * Unit price of the dish at the moment of ordering.
     * This value is stored to preserve price history.
     */
    private BigDecimal unitPrice;

    public OrderItem() {
        // Required by JPA
    }

    public OrderItem(Dish dish, int quantity, BigDecimal unitPrice) {
        this.dish = dish;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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

    /**
     * Returns the total price for this order item (unitPrice * quantity).
     */
    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

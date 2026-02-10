package com.fuegolento.backend.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fuegolento.backend.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;



/**
 * Represents a user order.
 * The total price is stored as a snapshot ONLY when the order is completed (DELIVERED),
 * to simplify reporting (e.g. daily revenue).
 */
@Entity(name = "OrderTable")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Order creation date/time
    private LocalDateTime createdAt = LocalDateTime.now();

    // Current order status (PENDING, IN_PROGRESS, READY, DELIVERED, CANCELLED)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    // User who placed the order
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // Items of the order (dish + quantity + unit price)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Total order price stored as a snapshot.
     * It is calculated and persisted ONLY when the order becomes DELIVERED.
     */
    private BigDecimal totalPrice;

    public Order() {
        // Required by JPA
    }

    public Order(User user) {
        this.user = user;
    }

    // Helper methods (no recalculation here)
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    /**
     * Calculates the total price from the current items.
     * This method does NOT persist the value automatically.
     * It should be used by the service layer when closing the order (DELIVERED).
     */
    public BigDecimal calculateTotalFromItems() {
        return items.stream()
            .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Stores the total price snapshot (used when the order is completed).
     */
    public void setTotalPriceSnapshot(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    // Getters and setters
    public Long getId() {
        return id;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        for (OrderItem item : this.items) {
            item.setOrder(this);
        }
    }

    /**
     * Returns the stored snapshot. It may be null if the order is not DELIVERED yet.
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}

package com.fuegolento.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fuegolento.backend.enums.OrderStatus;
import com.fuegolento.backend.model.Order;
import com.fuegolento.backend.model.User;

/**
 * Repository for Order entity.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders of a user
    List<Order> findByUser(User user);

    // Get all orders with a specific status
    List<Order> findByStatus(OrderStatus status);

    // Get all orders of a user with a specific status
    List<Order> findByUserAndStatus(User user, OrderStatus status);

    // Get orders created between two dates (for statistics)
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // Get completed orders (DELIVERED) between dates
    List<Order> findByStatusAndCreatedAtBetween(
        OrderStatus status,
        LocalDateTime start,
        LocalDateTime end
    );
}

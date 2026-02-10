package com.fuegolento.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fuegolento.backend.model.Order;
import com.fuegolento.backend.model.OrderItem;

/**
 * Repository for OrderItem entity.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Get all items of a given order
    List<OrderItem> findByOrder(Order order);
}

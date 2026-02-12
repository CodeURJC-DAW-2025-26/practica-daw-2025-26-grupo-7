package com.fuegolento.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fuegolento.backend.model.Order;
import com.fuegolento.backend.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderAndDishId(Order order, Long dishId);
}

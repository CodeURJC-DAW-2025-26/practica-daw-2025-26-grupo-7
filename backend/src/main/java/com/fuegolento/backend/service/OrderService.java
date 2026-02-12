package com.fuegolento.backend.service;

import com.fuegolento.backend.enums.OrderStatus;
import com.fuegolento.backend.model.Dish;
import com.fuegolento.backend.model.Order;
import com.fuegolento.backend.model.OrderItem;
import com.fuegolento.backend.model.User;
import com.fuegolento.backend.repository.OrderItemRepository;
import com.fuegolento.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final DishService dishService;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        DishService dishService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.dishService = dishService;
    }

    /* =========================
       READ
       ========================= */

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    public List<Order> findByUserAndStatus(User user, OrderStatus status) {
        return orderRepository.findByUserAndStatus(user, status);
    }

    public List<Order> findDeliveredBetween(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByStatusAndCreatedAtBetween(OrderStatus.DELIVERED, start, end);
    }

    /* =========================
       CREATE / CART-LIKE FLOW
       ========================= */

    /**
     * Creates a new empty order in PENDING status.
     * You can call this when user starts an order.
     */
    public Order createOrder(User user) {
        if (user == null) throw new IllegalArgumentException("User is required");

        Order order = new Order(user);
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    /**
     * Adds a dish to an existing order.
     * - Only allowed if order is PENDING
     * - Dish must be available
     * - If dish already exists in the order, increases quantity
     */
    public Order addDish(Long orderId, Long dishId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");

        Order order = findById(orderId);
        ensureEditable(order);

        Dish dish = dishService.findById(dishId);
        if (!dish.isAvailable()) {
            throw new IllegalStateException("Dish is not available");
        }

        OrderItem item = orderItemRepository.findByOrderAndDishId(order, dishId)
                .orElse(null);

        if (item == null) {
            OrderItem newItem = new OrderItem(dish, quantity, dish.getPrice());
            order.addItem(newItem);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }

        return orderRepository.save(order);
    }

    /**
     * Sets a specific quantity for a dish in the order.
     * If quantity <= 0, removes the item.
     */
    public Order setDishQuantity(Long orderId, Long dishId, int quantity) {
        Order order = findById(orderId);
        ensureEditable(order);

        OrderItem item = orderItemRepository.findByOrderAndDishId(order, dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found in order"));

        if (quantity <= 0) {
            order.removeItem(item);
        } else {
            item.setQuantity(quantity);
        }

        return orderRepository.save(order);
    }

    public Order removeDish(Long orderId, Long dishId) {
        Order order = findById(orderId);
        ensureEditable(order);

        OrderItem item = orderItemRepository.findByOrderAndDishId(order, dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found in order"));

        order.removeItem(item);
        return orderRepository.save(order);
    }

    /* =========================
       STATUS CHANGES
       ========================= */

    /**
     * Moves order from PENDING to IN_PROGRESS (kitchen starts).
     */
    public Order startPreparing(Long orderId) {
        Order order = findById(orderId);

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be started");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot start an empty order");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        return orderRepository.save(order);
    }

    /**
     * Moves order from IN_PROGRESS to READY.
     */
    public Order markReady(Long orderId) {
        Order order = findById(orderId);

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS orders can be marked as READY");
        }

        order.setStatus(OrderStatus.READY);
        return orderRepository.save(order);
    }

    /**
     * Moves order from READY to DELIVERED and stores total snapshot.
     */
    public Order deliver(Long orderId) {
        Order order = findById(orderId);

        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Only READY orders can be delivered");
        }

        BigDecimal total = order.calculateTotalFromItems();
        order.setTotalPriceSnapshot(total);
        order.setStatus(OrderStatus.DELIVERED);

        return orderRepository.save(order);
    }

    /**
     * Cancels an order if it is not already DELIVERED.
     */
    public Order cancel(Long orderId) {
        Order order = findById(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Delivered orders cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    /* =========================
       DELETE (optional admin)
       ========================= */

    public void deleteById(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found with id: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    /* =========================
       PRIVATE
       ========================= */

    private void ensureEditable(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be modified");
        }
    }
}

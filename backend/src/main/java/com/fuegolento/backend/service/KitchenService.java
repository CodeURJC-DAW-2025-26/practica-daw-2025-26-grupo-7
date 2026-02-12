package com.fuegolento.backend.service;

import com.fuegolento.backend.enums.OrderStatus;
import com.fuegolento.backend.model.Order;
import com.fuegolento.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class KitchenService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public KitchenService(OrderRepository orderRepository,
                          OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    /* =========================
       KDS COLUMNS
       ========================= */

    public List<Order> getReceivedOrders() {
        return getOrdersSortedByCreatedAtAsc(OrderStatus.PENDING);
    }

    public List<Order> getInProgressOrders() {
        return getOrdersSortedByCreatedAtAsc(OrderStatus.IN_PROGRESS);
    }

    public List<Order> getReadyOrders() {
        return getOrdersSortedByCreatedAtAsc(OrderStatus.READY);
    }

    /**
     * Convenience method if you want one call to build the whole board:
     * received / inProgress / ready.
     */
    public KitchenBoard getBoard() {
        KitchenBoard board = new KitchenBoard();
        board.received = getReceivedOrders();
        board.inProgress = getInProgressOrders();
        board.ready = getReadyOrders();
        return board;
    }

    /* =========================
       ACTIONS (status transitions)
       ========================= */

    // "Pasar a En marcha"
    public Order moveToInProgress(Long orderId) {
        return orderService.startPreparing(orderId);
    }

    // "Marcar Preparada"
    public Order moveToReady(Long orderId) {
        return orderService.markReady(orderId);
    }

    // "Volver a Recibida"
    public Order backToReceived(Long orderId) {
        Order order = orderService.findById(orderId);
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS orders can go back to PENDING");
        }
        order.setStatus(OrderStatus.PENDING);
        return orderRepository.save(order);
    }

    // "Volver a En marcha"
    public Order backToInProgress(Long orderId) {
        Order order = orderService.findById(orderId);
        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Only READY orders can go back to IN_PROGRESS");
        }
        order.setStatus(OrderStatus.IN_PROGRESS);
        return orderRepository.save(order);
    }

    /* =========================
       PRIVATE
       ========================= */

    private List<Order> getOrdersSortedByCreatedAtAsc(OrderStatus status) {
        List<Order> orders = orderRepository.findByStatus(status);
        orders.sort(Comparator.comparing(Order::getCreatedAt)); // oldest first (like a kitchen queue)
        return orders;
    }

    /**
     * Simple DTO-like structure for the kitchen board.
     * You can replace this later with a proper DTO if needed.
     */
    public static class KitchenBoard {
        public List<Order> received;
        public List<Order> inProgress;
        public List<Order> ready;
    }
}

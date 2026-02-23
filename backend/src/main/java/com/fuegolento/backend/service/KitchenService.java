package com.fuegolento.backend.service;

import com.fuegolento.backend.enums.OrderStatus;
import com.fuegolento.backend.model.Order;
import com.fuegolento.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
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

    /**
     * Orders that have been submitted by the user
     * and are waiting to be prepared.
     */
    public List<Order> getReceivedOrders() {
        return orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.SENT_TO_KITCHEN);
    }

    /**
     * Orders currently being prepared.
     */
    public List<Order> getInProgressOrders() {
        return orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.IN_PROGRESS);
    }

    /**
     * Orders ready to be delivered.
     */
    public List<Order> getReadyOrders() {
        return orderRepository.findByStatusOrderByCreatedAtAsc(OrderStatus.READY);
    }

    /**
     * Convenience method to build the full kitchen board.
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

    /**
     * SENT_TO_KITCHEN -> IN_PROGRESS
     */
    public Order moveToInProgress(Long orderId) {
        return orderService.startPreparing(orderId);
    }

    /**
     * IN_PROGRESS -> READY
     */
    public Order moveToReady(Long orderId) {
        return orderService.markReady(orderId);
    }

    /**
     * IN_PROGRESS -> SENT_TO_KITCHEN
     */
    public Order backToReceived(Long orderId) {
        Order order = orderService.findById(orderId);

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only IN_PROGRESS orders can go back to SENT_TO_KITCHEN"
            );
        }

        order.setStatus(OrderStatus.SENT_TO_KITCHEN);
        return orderRepository.save(order);
    }

    /**
     * READY -> IN_PROGRESS
     */
    public Order backToInProgress(Long orderId) {
        Order order = orderService.findById(orderId);

        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException(
                    "Only READY orders can go back to IN_PROGRESS"
            );
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        return orderRepository.save(order);
    }

    /**
     * Simple structure used by the kitchen board.
     */
    public static class KitchenBoard {
        public List<Order> received;
        public List<Order> inProgress;
        public List<Order> ready;
    }
}
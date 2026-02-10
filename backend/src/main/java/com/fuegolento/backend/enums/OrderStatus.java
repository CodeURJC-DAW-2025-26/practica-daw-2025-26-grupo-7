package com.fuegolento.backend.enums;

/**
 * Represents the different states an order can have
 * during its lifecycle.
 */
public enum OrderStatus {

    /**
     * Order has been created but not processed yet.
     */
    PENDING,

    /**
     * Order is being prepared in the kitchen.
     */
    IN_PROGRESS,

    /**
     * Order is ready to be delivered or picked up.
     */
    READY,

    /**
     * Order has been delivered.
     * At this point the order is considered closed and the total price is fixed.
     */
    DELIVERED,

    /**
     * Order was cancelled before being completed.
     */
    CANCELLED
}

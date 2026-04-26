package com.fuegolento.backend.service;

import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.enums.OrderStatus;
import com.fuegolento.backend.exception.custom.ForbiddenException;
import com.fuegolento.backend.exception.custom.ResourceNotFoundException;
import com.fuegolento.backend.model.Dish;
import com.fuegolento.backend.model.Order;
import com.fuegolento.backend.model.OrderItem;
import com.fuegolento.backend.model.User;
import com.fuegolento.backend.repository.OrderItemRepository;
import com.fuegolento.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
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
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
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
       CART (PENDING order in DB)
       ========================= */

    public Order getOrCreateCart(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        return orderRepository
                .findFirstByUserAndStatusOrderByCreatedAtDesc(user, OrderStatus.PENDING)
                .orElseGet(() -> {
                    Order order = new Order(user);
                    order.setStatus(OrderStatus.PENDING);
                    return orderRepository.save(order);
                });
    }

    public Order getCartIfExists(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        return orderRepository
                .findFirstByUserAndStatusOrderByCreatedAtDesc(user, OrderStatus.PENDING)
                .orElse(null);
    }

    public Order addToCart(User user, Long dishId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }

        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        Dish dish = dishService.findById(dishId);
        if (!dish.isAvailable()) {
            throw new IllegalStateException("Dish is not available");
        }

        OrderItem item = orderItemRepository.findByOrderAndDish_Id(cart, dishId).orElse(null);

        if (item == null) {
            OrderItem newItem = new OrderItem(dish, quantity, dish.getPrice());
            cart.addItem(newItem);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }

        return orderRepository.save(cart);
    }

    public Order setCartDishQuantity(User user, Long dishId, int quantity) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        OrderItem item = orderItemRepository.findByOrderAndDish_Id(cart, dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found in cart"));

        if (quantity <= 0) {
            cart.removeItem(item);
        } else {
            item.setQuantity(quantity);
        }

        return orderRepository.save(cart);
    }

    public Order removeFromCart(User user, Long dishId) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        OrderItem item = orderItemRepository.findByOrderAndDish_Id(cart, dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found in cart"));

        cart.removeItem(item);
        return orderRepository.save(cart);
    }

    public Order clearCart(User user) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        if (cart.getItems() != null) {
            cart.getItems().clear();
        }

        return orderRepository.save(cart);
    }

    public int getCartItemCount(User user) {
        Order cart = getCartIfExists(user);
        if (cart == null) {
            return 0;
        }

        return orderItemRepository.sumQuantitiesByOrder(cart);
    }

    public BigDecimal calculateCartTotal(Order cart) {
        if (cart == null || cart.getItems() == null) {
            return BigDecimal.ZERO;
        }
        return cart.calculateTotalFromItems();
    }

    /* =========================
       SUBMIT / STATUS CHANGES
       ========================= */

    public Order submitCart(User user) {
        Order cart = getCartIfExists(user);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot submit an empty cart");
        }
        ensureEditable(cart);

        cart.setStatus(OrderStatus.SENT_TO_KITCHEN);
        return orderRepository.save(cart);
    }

    public Order updateCartStatus(User user, OrderStatus targetStatus) {
        if (targetStatus != OrderStatus.SENT_TO_KITCHEN) {
            throw new IllegalArgumentException("Cart can only be moved to SENT_TO_KITCHEN");
        }
        return submitCart(user);
    }

    public Order startPreparing(Long orderId) {
        Order order = findById(orderId);

        if (order.getStatus() != OrderStatus.SENT_TO_KITCHEN) {
            throw new IllegalStateException("Only SENT_TO_KITCHEN orders can be started");
        }
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot start an empty order");
        }

        order.setStatus(OrderStatus.IN_PROGRESS);
        return orderRepository.save(order);
    }

    public Order markReady(Long orderId) {
        Order order = findById(orderId);

        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS orders can be marked as READY");
        }

        order.setStatus(OrderStatus.READY);
        return orderRepository.save(order);
    }

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

    public Order cancel(Long orderId) {
        Order order = findById(orderId);

        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new IllegalStateException("Delivered orders cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public Order setCartItemKitchenNote(User user, Long dishId, String kitchenNote) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        OrderItem item = orderItemRepository.findByOrderAndDish_Id(cart, dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found in cart"));

        if (kitchenNote != null && kitchenNote.trim().isEmpty()) {
            kitchenNote = null;
        }

        item.setKitchenNote(kitchenNote);
        return orderRepository.save(cart);
    }

    public Order setCartItemMeatPoint(User user, Long dishId, String meatPoint) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        Dish dish = dishService.findById(dishId);
        if (dish.getCategory() != DishCategory.MEAT) {
            throw new IllegalStateException("Meat point can only be set for MEAT dishes");
        }

        if (meatPoint != null) {
            meatPoint = meatPoint.trim().toUpperCase();
        }

        if (meatPoint == null || meatPoint.isEmpty()) {
            meatPoint = null;
        } else {
            switch (meatPoint) {
                case "MUY_HECHO":
                case "HECHO":
                case "AL_PUNTO":
                case "POCO_HECHO":
                    break;
                default:
                    throw new IllegalArgumentException("Invalid meat point");
            }
        }

        OrderItem item = orderItemRepository.findByOrderAndDish_Id(cart, dishId)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found in cart"));

        item.setMeatPoint(meatPoint);
        return orderRepository.save(cart);
    }

    public Order setCartTableNumber(User user, Integer tableNumber) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        if (tableNumber == null || tableNumber < 1 || tableNumber > 20) {
            throw new IllegalArgumentException("Table number must be between 1 and 20");
        }

        cart.setTableNumber(tableNumber);
        return orderRepository.save(cart);
    }

    public Order setCartCustomerNote(User user, String customerNote) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        if (customerNote != null && customerNote.trim().isEmpty()) {
            customerNote = null;
        }

        cart.setCustomerNote(customerNote);
        return orderRepository.save(cart);
    }

    /* =========================
       DELETE
       ========================= */

    public void deleteById(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order not found with id: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }

    public List<Order> getOrderHistory(User user) {
        return orderRepository.findByUserAndStatusNotOrderByCreatedAtDesc(user, OrderStatus.PENDING);
    }

    /* =========================
       PRIVATE
       ========================= */

    private void ensureEditable(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be modified");
        }
    }

    public Order adminMoveStatus(Long orderId, OrderStatus target) {

        Order order = findById(orderId);
        OrderStatus current = order.getStatus();

        if (target == current) {
            return order;
        }

        switch (target) {
            case SENT_TO_KITCHEN -> {
                if (current != OrderStatus.PENDING) {
                    throw new IllegalStateException("Only PENDING orders can be sent to kitchen");
                }
                order.setStatus(OrderStatus.SENT_TO_KITCHEN);
                return orderRepository.save(order);
            }

            case IN_PROGRESS -> {
                if (current == OrderStatus.SENT_TO_KITCHEN) {
                    return startPreparing(orderId);
                }
                order.setStatus(OrderStatus.IN_PROGRESS);
                return orderRepository.save(order);
            }

            case READY -> {
                if (current == OrderStatus.IN_PROGRESS) {
                    return markReady(orderId);
                }
                if (current == OrderStatus.SENT_TO_KITCHEN) {
                    startPreparing(orderId);
                    return markReady(orderId);
                }
                order.setStatus(OrderStatus.READY);
                return orderRepository.save(order);
            }

            case DELIVERED -> {
                if (current == OrderStatus.SENT_TO_KITCHEN) {
                    startPreparing(orderId);
                    markReady(orderId);
                    return deliver(orderId);
                }
                if (current == OrderStatus.IN_PROGRESS) {
                    markReady(orderId);
                    return deliver(orderId);
                }
                if (current == OrderStatus.READY) {
                    return deliver(orderId);
                }
                throw new IllegalStateException("Cannot deliver an order from status: " + current);
            }

            case CANCELLED -> {
                return cancel(orderId);
            }

            case PENDING -> {
                throw new IllegalStateException("Cannot move an order back to PENDING (cart)");
            }
        }

        throw new IllegalStateException("Unsupported status change");
    }

    public Order duplicateOrder(User user, Long orderId) {
        Order orderToDuplicate = findById(orderId);

        // Ownership check
        if (orderToDuplicate.getUser() == null || !orderToDuplicate.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("You are not allowed to duplicate this order");
        }

        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        if (orderToDuplicate.getItems() != null) {
            for (OrderItem item : orderToDuplicate.getItems()) {
                OrderItem newItem = new OrderItem(
                        item.getDish(),
                        item.getQuantity(),
                        item.getDish().getPrice()
                );

                if (item.getMeatPoint() != null) {
                    newItem.setMeatPoint(item.getMeatPoint());
                }
                if (item.getKitchenNote() != null) {
                    newItem.setKitchenNote(item.getKitchenNote());
                }

                cart.addItem(newItem);
            }
        }

        return orderRepository.save(cart);
    }
}
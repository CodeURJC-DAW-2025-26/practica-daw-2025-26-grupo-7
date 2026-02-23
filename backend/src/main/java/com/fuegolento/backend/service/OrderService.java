package com.fuegolento.backend.service;

import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.enums.OrderStatus;
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
       CART (PENDING order in DB)
       ========================= */

    /**
     * Returns the current cart of the user (latest PENDING order).
     * If not exists, creates a new PENDING order.
     */
    public Order getOrCreateCart(User user) {
        if (user == null) throw new IllegalArgumentException("User is required");

        return orderRepository
                .findFirstByUserAndStatusOrderByCreatedAtDesc(user, OrderStatus.PENDING)
                .orElseGet(() -> {
                    Order order = new Order(user);
                    order.setStatus(OrderStatus.PENDING);
                    return orderRepository.save(order);
                });
    }

    /**
     * Returns the current cart if exists, otherwise null.
     * Useful for pages like /order to show "empty cart" without creating a new row.
     */
    public Order getCartIfExists(User user) {
        if (user == null) throw new IllegalArgumentException("User is required");

        return orderRepository
                .findFirstByUserAndStatusOrderByCreatedAtDesc(user, OrderStatus.PENDING)
                .orElse(null);
    }

    /**
     * Adds quantity units of a dish to the user's cart.
     */
    public Order addToCart(User user, Long dishId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");

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

    /**
     * Sets quantity for a dish inside the user's cart.
     * If quantity <= 0, removes the item.
     */
    public Order setCartDishQuantity(User user, Long dishId, int quantity) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        OrderItem item = orderItemRepository.findByOrderAndDish_Id(cart, dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found in cart"));

        if (quantity <= 0) {
            cart.removeItem(item);
        } else {
            item.setQuantity(quantity);
        }

        return orderRepository.save(cart);
    }

    /**
     * Removes a dish from the user's cart.
     */
    public Order removeFromCart(User user, Long dishId) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        OrderItem item = orderItemRepository.findByOrderAndDish_Id(cart, dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found in cart"));

        cart.removeItem(item);
        return orderRepository.save(cart);
    }

    /**
     * Empties the user's cart (removes all items).
     */
    public Order clearCart(User user) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        if (cart.getItems() != null) {
            cart.getItems().clear();
        }

        return orderRepository.save(cart);
    }

    /**
     * Cart badge count:
     * total quantities (e.g., 2+1 = 3)
     */
    public int getCartItemCount(User user) {
        Order cart = getCartIfExists(user);
        if (cart == null) return 0;

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

    /**
     * Submits the current cart to the kitchen.
     * PENDING -> SENT_TO_KITCHEN
     */
    public Order submitCart(User user) {
        Order cart = getCartIfExists(user);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot submit an empty cart");
        }
        ensureEditable(cart);

        cart.setStatus(OrderStatus.SENT_TO_KITCHEN);
        return orderRepository.save(cart);
    }

    /**
     * SENT_TO_KITCHEN -> IN_PROGRESS
     */
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

    /**
     * IN_PROGRESS -> READY
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
     * READY -> DELIVERED and stores total snapshot.
     */
    public Order deliver(Long orderId) {
        Order order = findById(orderId);

        if (order.getStatus() != OrderStatus.READY) {
            throw new IllegalStateException("Only READY orders can be delivered");
        }

        BigDecimal total = order.calculateTotalFromItems();

        // Snapshot for auditing/history
        order.setTotalPriceSnapshot(total);

        // Optional: if your entity also has totalPrice used by views
        // (Remove this line if your Order entity does not have setTotalPrice)
        // order.setTotalPrice(total);

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

    public Order setCartItemKitchenNote(User user, Long dishId, String kitchenNote) {
        Order cart = getOrCreateCart(user);
        ensureEditable(cart);

        OrderItem item = orderItemRepository.findByOrderAndDish_Id(cart, dishId)
                .orElseThrow(() -> new RuntimeException("Dish not found in cart"));

        if (kitchenNote != null && kitchenNote.trim().isEmpty()) kitchenNote = null;

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
                .orElseThrow(() -> new RuntimeException("Dish not found in cart"));

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

        if (customerNote != null && customerNote.trim().isEmpty()) customerNote = null;

        cart.setCustomerNote(customerNote);
        return orderRepository.save(cart);
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

    /**
     * User "orders history": excludes the cart (PENDING).
     */
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
}
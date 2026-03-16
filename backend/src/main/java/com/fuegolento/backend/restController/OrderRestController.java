package com.fuegolento.backend.restController;

import com.fuegolento.backend.dto.CartItemRequestDTO;
import com.fuegolento.backend.dto.OrderDTO;
import com.fuegolento.backend.dto.TableNumberRequestDTO;
import com.fuegolento.backend.dto.TextRequestDTO;
import com.fuegolento.backend.enums.OrderStatus;
import com.fuegolento.backend.mapper.OrderMapper;
import com.fuegolento.backend.model.Order;
import com.fuegolento.backend.model.User;
import com.fuegolento.backend.service.InvoicePdfService;
import com.fuegolento.backend.service.OrderService;
import com.fuegolento.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderRestController {

    private final OrderService orderService;
    private final UserService userService;
    private final OrderMapper orderMapper;
    private final InvoicePdfService invoicePdfService;

    public OrderRestController(OrderService orderService,
                               UserService userService,
                               OrderMapper orderMapper,
                               InvoicePdfService invoicePdfService) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderMapper = orderMapper;
        this.invoicePdfService = invoicePdfService;
    }

    /* =========================
       READ
       ========================= */

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrders(
            @RequestParam(required = false) OrderStatus status
    ) {
        List<Order> orders = (status == null)
                ? orderService.findAll()
                : orderService.findByStatus(status);

        List<OrderDTO> response = orders.stream()
                .map(orderMapper::toDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        Order order = orderService.findById(id);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderDTO>> getMyOrders() {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        List<OrderDTO> orders = orderService.getOrderHistory(user).stream()
                .map(orderMapper::toDTO)
                .toList();

        return ResponseEntity.ok(orders);
    }

    @GetMapping("/my-cart")
    public ResponseEntity<OrderDTO> getMyCart() {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order cart = orderService.getCartIfExists(user);

        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(orderMapper.toDTO(cart));
    }

    /* =========================
       CART
       ========================= */

    @PostMapping("/cart/items")
    public ResponseEntity<OrderDTO> addToCart(@RequestBody CartItemRequestDTO request) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order cart = orderService.addToCart(user, request.getDishId(), request.getQuantity());
        return ResponseEntity.ok(orderMapper.toDTO(cart));
    }

    @PutMapping("/cart/items/{dishId}")
    public ResponseEntity<OrderDTO> updateCartItemQuantity(@PathVariable Long dishId,
                                                           @RequestBody CartItemRequestDTO request) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order cart = orderService.setCartDishQuantity(user, dishId, request.getQuantity());
        return ResponseEntity.ok(orderMapper.toDTO(cart));
    }

    @DeleteMapping("/cart/items/{dishId}")
    public ResponseEntity<OrderDTO> removeCartItem(@PathVariable Long dishId) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order cart = orderService.removeFromCart(user, dishId);
        return ResponseEntity.ok(orderMapper.toDTO(cart));
    }

    @DeleteMapping("/cart")
    public ResponseEntity<OrderDTO> clearCart() {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order cart = orderService.clearCart(user);
        return ResponseEntity.ok(orderMapper.toDTO(cart));
    }

    @PutMapping("/cart/items/{dishId}/comment")
    public ResponseEntity<OrderDTO> setItemComment(@PathVariable Long dishId,
                                                   @RequestBody TextRequestDTO request) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order cart = orderService.setCartItemKitchenNote(user, dishId, request.getValue());
        return ResponseEntity.ok(orderMapper.toDTO(cart));
    }

    @PutMapping("/cart/items/{dishId}/meat-point")
    public ResponseEntity<OrderDTO> setMeatPoint(@PathVariable Long dishId,
                                                 @RequestBody TextRequestDTO request) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order cart = orderService.setCartItemMeatPoint(user, dishId, request.getValue());
        return ResponseEntity.ok(orderMapper.toDTO(cart));
    }

    @PutMapping("/cart/table")
    public ResponseEntity<OrderDTO> setTableNumber(@RequestBody TableNumberRequestDTO request) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order cart = orderService.setCartTableNumber(user, request.getTableNumber());
        return ResponseEntity.ok(orderMapper.toDTO(cart));
    }

    @PutMapping("/cart/customer-note")
    public ResponseEntity<OrderDTO> setCustomerNote(@RequestBody TextRequestDTO request) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order cart = orderService.setCartCustomerNote(user, request.getValue());
        return ResponseEntity.ok(orderMapper.toDTO(cart));
    }

    @PutMapping("/cart/status")
    public ResponseEntity<OrderDTO> updateCartStatus(@RequestParam("value") OrderStatus value) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order order = orderService.updateCartStatus(user, value);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    /* =========================
       STATUS CHANGES
       ========================= */

    @PutMapping("/{id}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id,
                                                      @RequestParam("value") OrderStatus value) {
        Order order = orderService.adminMoveStatus(id, value);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    /* =========================
       INVOICE
       ========================= */

    @GetMapping("/{id}/invoice")
    public void downloadInvoice(@PathVariable Long id, HttpServletResponse response) throws IOException {

        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order order = orderService.findById(id);

        if (order.getUser() == null || !order.getUser().getId().equals(user.getId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to view this invoice");
            return;
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            response.sendError(HttpServletResponse.SC_CONFLICT, "Invoice available only for DELIVERED orders");
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=invoice_" + order.getId() + ".pdf");

        invoicePdfService.writeInvoicePdf(order, response.getOutputStream());
    }

    /* =========================
       DELETE
       ========================= */

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /* =========================
       COPIES
       ========================= */

    @PostMapping("/{id}/copies")
    public ResponseEntity<OrderDTO> duplicateOrder(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser()
                .orElseThrow(() -> new RuntimeException("User not authenticated"));

        Order cart = orderService.duplicateOrder(user, id);
        return ResponseEntity.ok(orderMapper.toDTO(cart));
    }
}
package com.fuegolento.backend.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fuegolento.backend.model.User;
import com.fuegolento.backend.service.OrderService;
import com.fuegolento.backend.service.UserService;

@ControllerAdvice
public class GlobalModelAttributes {

    private final UserService userService;
    private final OrderService orderService;

    public GlobalModelAttributes(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @ModelAttribute("isLogged")
    public boolean isLogged(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * Cart badge count for the navbar.
     * Uses DB cart strategy: current cart = latest Order with status PENDING.
     */
    @ModelAttribute("cartCount")
    public int cartCount(Authentication authentication) {

        boolean logged = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        if (!logged) return 0;

        User user = userService.getAuthenticatedUser().orElse(null);
        if (user == null) return 0;

        return orderService.getCartItemCount(user);
    }
}
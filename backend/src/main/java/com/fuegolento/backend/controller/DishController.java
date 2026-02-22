package com.fuegolento.backend.controller;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.model.Dish;
import com.fuegolento.backend.model.User;
import com.fuegolento.backend.service.DishService;
import com.fuegolento.backend.service.OrderService;
import com.fuegolento.backend.service.UserService;

@Controller
public class DishController {

    private final DishService dishService;
    private final OrderService orderService;
    private final UserService userService;

    public DishController(DishService dishService,
                          OrderService orderService,
                          UserService userService) {
        this.dishService = dishService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/menu")
    public String showMenu(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) DishCategory category,
            Authentication authentication,
            Model model
    ) {
        boolean isLogged = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        String searchQuery = (q == null) ? "" : q.trim();

        var dishes = !searchQuery.isBlank()
                ? dishService.searchAvailableByName(searchQuery)
                : (category != null ? dishService.findAvailableByCategory(category) : dishService.findAllAvailable());

        model.addAttribute("isLogged", isLogged);
        model.addAttribute("dishes", dishes);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchQuery", searchQuery);

        model.addAttribute("isStarter", category == DishCategory.STARTER);
        model.addAttribute("isMeat", category == DishCategory.MEAT);
        model.addAttribute("isDessert", category == DishCategory.DESSERT);
        model.addAttribute("isDrink", category == DishCategory.DRINK);

        // Cart badge (only if logged)
        if (isLogged) {
            User user = userService.getAuthenticatedUser().orElse(null);
            int cartCount = (user == null) ? 0 : orderService.getCartItemCount(user);
            model.addAttribute("cartCount", cartCount);
        } else {
            model.addAttribute("cartCount", 0);
        }

        model.addAttribute("menuActive", true);
        return "menu";
    }

    @GetMapping("/menu/{id}")
    public String showDishDetail(
            @PathVariable Long id,
            Authentication authentication,
            Model model
    ) {
        boolean isLogged = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        Dish dish = dishService.findById(id); // throws if not found

        // Extra safety: do not allow viewing unavailable dish (optional but recommended)
        if (!dish.isAvailable()) {
            return "redirect:/menu";
        }

        model.addAttribute("isLogged", isLogged);
        model.addAttribute("dish", dish);
        model.addAttribute("menuActive", true);

        // Avoid Mustache null issues
        model.addAttribute("allergens", dish.getAllergens() == null ? List.of() : dish.getAllergens());

        // Cart badge (only if logged)
        if (isLogged) {
            User user = userService.getAuthenticatedUser().orElse(null);
            int cartCount = (user == null) ? 0 : orderService.getCartItemCount(user);
            model.addAttribute("cartCount", cartCount);
        } else {
            model.addAttribute("cartCount", 0);
        }

        return "dish-detail";
    }
}
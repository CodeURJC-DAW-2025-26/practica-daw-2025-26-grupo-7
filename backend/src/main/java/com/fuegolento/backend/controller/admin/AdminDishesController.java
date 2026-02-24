package com.fuegolento.backend.controller.admin;

import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.model.Dish;
import com.fuegolento.backend.service.DishService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/products")
public class AdminDishesController {

    private final DishService dishService;

    public AdminDishesController(DishService dishService) {
        this.dishService = dishService;
    }

    /* =========================
       LIST (Search + Category filter)
       ========================= */

    @GetMapping
    public String list(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "category", required = false) DishCategory category,
            Model model
    ) {
        // Base list (admin -> all dishes)
        List<Dish> dishes = dishService.findAll();

        // Filter by category
        if (category != null) {
            dishes = dishes.stream()
                    .filter(d -> d.getCategory() == category)
                    .collect(Collectors.toList());
        }

        // Search by name (case-insensitive)
        if (q != null && !q.isBlank()) {
            String query = q.trim().toLowerCase();
            dishes = dishes.stream()
                    .filter(d -> d.getName() != null && d.getName().toLowerCase().contains(query))
                    .collect(Collectors.toList());
        }

        // Sort: by id desc (newer first) - you can change to name asc if you prefer
        dishes.sort(Comparator.comparing(Dish::getId, Comparator.nullsLast(Comparator.naturalOrder())).reversed());

        List<Map<String, Object>> vm = buildDishesVm(dishes);

        model.addAttribute("dishes", vm);
        model.addAttribute("hasDishes", !vm.isEmpty());

        model.addAttribute("searchQuery", q == null ? "" : q);
        model.addAttribute("selectedCategory", category == null ? "" : category.name());

        // Flags for category select
        model.addAttribute("isStarter", category == DishCategory.STARTER);
        model.addAttribute("isMeat", category == DishCategory.MEAT);
        model.addAttribute("isDessert", category == DishCategory.DESSERT);
        model.addAttribute("isDrink", category == DishCategory.DRINK);

        // For header-admin active item
        model.addAttribute("activeProducts", true);

        return "admin-products"; // templates/admin-products.html
    }

    /* =========================
       PRIVATE VM
       ========================= */

    private List<Map<String, Object>> buildDishesVm(List<Dish> dishes) {
        if (dishes == null || dishes.isEmpty()) return Collections.emptyList();

        List<Map<String, Object>> res = new ArrayList<>();

        for (Dish d : dishes) {
            Map<String, Object> m = new HashMap<>();

            m.put("id", d.getId());
            m.put("name", safe(d.getName()));
            m.put("description", safe(d.getDescription()));

            // Category label (Spanish)
            m.put("categoryLabel", categoryLabel(d.getCategory()));

            // Price formatted (comma + €)
            m.put("price", formatMoney(d.getPrice()));

            // Availability badge
            boolean available = d.isAvailable();
            m.put("availableLabel", available ? "Sí" : "No");
            // Reuse your existing classes (status-active / status-banned)
            m.put("availableBadgeClass", available ? "status-active" : "status-banned");

            // Image URL (from DB image if exists)
            String imageUrl = "/img/menu/default-dish.png";
            if (d.getImage() != null && d.getImage().getId() != null) {
                imageUrl = "/images/" + d.getImage().getId();
            }
            m.put("imageUrl", imageUrl);

            res.add(m);
        }

        return res;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String categoryLabel(DishCategory c) {
        if (c == null) return "-";
        return switch (c) {
            case STARTER -> "Entrantes";
            case MEAT -> "Carnes";
            case DESSERT -> "Postres";
            case DRINK -> "Bebidas";
        };
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0,00€";
        String s = value.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",");
        return s + "€";
    }
}
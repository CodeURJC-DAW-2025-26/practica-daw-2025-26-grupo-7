package com.fuegolento.backend.controller.admin;

import com.fuegolento.backend.model.Order;
import com.fuegolento.backend.model.OrderItem;
import com.fuegolento.backend.service.KitchenService;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/admin/kitchen")
public class KitchenController {

    private final KitchenService kitchenService;

    public KitchenController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @GetMapping
    public String kitchenBoard(Model model, CsrfToken csrfToken) {

        // Security is handled by WebSecurityConfig:
        // /admin/** -> hasRole("ADMIN")
        KitchenService.KitchenBoard board = kitchenService.getBoard();

        List<Map<String, Object>> receivedVm = buildOrdersVm(board.received);
        List<Map<String, Object>> inProgressVm = buildOrdersVm(board.inProgress);
        List<Map<String, Object>> readyVm = buildOrdersVm(board.ready);

        model.addAttribute("receivedOrders", receivedVm);
        model.addAttribute("inProgressOrders", inProgressVm);
        model.addAttribute("readyOrders", readyVm);

        model.addAttribute("countReceived", receivedVm.size());
        model.addAttribute("countProgress", inProgressVm.size());
        model.addAttribute("countDone", readyVm.size());

        model.addAttribute("hasReceived", !receivedVm.isEmpty());
        model.addAttribute("hasProgress", !inProgressVm.isEmpty());
        model.addAttribute("hasDone", !readyVm.isEmpty());

        // Use explicit names to avoid accidentally appending tokens to URLs
        if (csrfToken != null) {
            model.addAttribute("csrfToken", csrfToken.getToken());
            model.addAttribute("csrfParameterName", csrfToken.getParameterName()); // usually "_csrf"
        }

        model.addAttribute("kitchenActive", true);

        return "admin-kitchen";
    }

    /* =========================
       ACTIONS
       ========================= */

    @PostMapping("/to-in-progress")
    public String moveToInProgress(@RequestParam("orderId") Long orderId) {
        kitchenService.moveToInProgress(orderId);
        return "redirect:/admin/kitchen";
    }

    @PostMapping("/to-ready")
    public String moveToReady(@RequestParam("orderId") Long orderId) {
        kitchenService.moveToReady(orderId);
        return "redirect:/admin/kitchen";
    }

    @PostMapping("/back-to-received")
    public String backToReceived(@RequestParam("orderId") Long orderId) {
        kitchenService.backToReceived(orderId);
        return "redirect:/admin/kitchen";
    }

    @PostMapping("/back-to-in-progress")
    public String backToInProgress(@RequestParam("orderId") Long orderId) {
        kitchenService.backToInProgress(orderId);
        return "redirect:/admin/kitchen";
    }

    /* =========================
       PRIVATE (ViewModel builders)
       ========================= */

    private List<Map<String, Object>> buildOrdersVm(List<Order> orders) {
        if (orders == null || orders.isEmpty()) return Collections.emptyList();

        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        for (Order o : orders) {
            Map<String, Object> vm = new HashMap<>();

            vm.put("id", o.getId());
            vm.put("tableNumber", o.getTableNumber() != null ? o.getTableNumber() : "-");
            vm.put("time", o.getCreatedAt() != null ? o.getCreatedAt().format(timeFmt) : "--:--");
            vm.put("title", "Comanda #" + o.getId());

            vm.put("customerNote", o.getCustomerNote() != null ? o.getCustomerNote() : "");
            vm.put("hasCustomerNote", o.getCustomerNote() != null && !o.getCustomerNote().isBlank());

            List<Map<String, Object>> itemsVm = new ArrayList<>();
            if (o.getItems() != null) {
                for (OrderItem it : o.getItems()) {
                    Map<String, Object> itemVm = new HashMap<>();
                    itemVm.put("qty", it.getQuantity());
                    itemVm.put("name", it.getDish() != null ? it.getDish().getName() : "Dish");

                    String note = buildItemNote(it);
                    itemVm.put("note", note);
                    itemVm.put("hasNote", note != null && !note.isBlank());

                    itemsVm.add(itemVm);
                }
            }

            vm.put("items", itemsVm);
            vm.put("hasItems", !itemsVm.isEmpty());

            // For kitchen board: show calculated total (even if not delivered yet)
            BigDecimal total = (o.getItems() == null) ? BigDecimal.ZERO : o.calculateTotalFromItems();
            vm.put("total", formatMoney(total));

            result.add(vm);
        }

        return result;
    }

    private String buildItemNote(OrderItem it) {
        List<String> parts = new ArrayList<>();

        if (it.getMeatPoint() != null && !it.getMeatPoint().isBlank()) {
            String mp = it.getMeatPoint().replace("_", " ").toLowerCase();
            parts.add(mp);
        }

        if (it.getKitchenNote() != null && !it.getKitchenNote().isBlank()) {
            parts.add(it.getKitchenNote());
        }

        return String.join(" · ", parts);
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) return "0,00€";
        String s = value.setScale(2, RoundingMode.HALF_UP).toString();
        s = s.replace(".", ",");
        return s + "€";
    }
}
package com.fuegolento.backend.controller;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fuegolento.backend.model.User;
import com.fuegolento.backend.service.UserService;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {

        Optional<User> optUser = userService.getAuthenticatedUser();

        if (optUser.isEmpty()) {
            // Should not happen because /profile requires ROLE_USER in your security,
            // but it's a safe fallback.
            return "redirect:/login";
        }

        User user = optUser.get();

        model.addAttribute("user", user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("birthDate", user.getBirthDate());

        boolean isAdmin = user.getRoles() != null && user.getRoles().contains("ADMIN");
        boolean isUser = user.getRoles() != null && user.getRoles().contains("USER");

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isUser", isUser);

        // Useful for templates if you want to highlight nav later
        model.addAttribute("profileActive", true);

        return "profile";
    }
}
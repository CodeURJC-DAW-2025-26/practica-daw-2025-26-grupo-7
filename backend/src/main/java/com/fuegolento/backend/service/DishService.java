package com.fuegolento.backend.service;

import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.model.Dish;
import com.fuegolento.backend.repository.DishRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;

    public DishService(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    /* =========================
       PUBLIC MENU (only available)
       ========================= */

    public List<Dish> findAllAvailable() {
        return dishRepository.findByAvailableTrue();
    }

    public List<Dish> findAvailableByCategory(DishCategory category) {
        return dishRepository.findByCategoryAndAvailableTrue(category);
    }

    public List<Dish> searchAvailableByName(String query) {
        if (query == null || query.isBlank()) {
            return findAllAvailable();
        }
        return dishRepository.findByNameContainingIgnoreCaseAndAvailableTrue(query.trim());
    }

    /* =========================
       ADMIN (all dishes)
       ========================= */

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public Dish findById(Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dish not found with id: " + id));
    }

    public List<Dish> findByCategory(DishCategory category) {
        return dishRepository.findByCategory(category);
    }

    public List<Dish> searchByName(String query) {
        if (query == null || query.isBlank()) {
            return dishRepository.findAll();
        }
        return dishRepository.findByNameContainingIgnoreCase(query.trim());
    }

    /* =========================
       CREATE / UPDATE / DELETE
       ========================= */

    public Dish create(Dish dish) {
        validateDish(dish);

        if (dish.getId() != null) {
            throw new IllegalArgumentException("New dish must not have an id");
        }

        // If not set, keep default true
        return dishRepository.save(dish);
    }

    public Dish update(Long id, Dish updated) {
        Dish existing = findById(id);

        validateDish(updated);

        existing.setCategory(updated.getCategory());
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setAllergens(updated.getAllergens());
        existing.setPrice(updated.getPrice());
        existing.setAvailable(updated.isAvailable());

        // Keep existing image unless a new one is provided
        if (updated.getImage() != null && updated.getImage().length > 0) {
            existing.setImage(updated.getImage());
        }

        return dishRepository.save(existing);
    }

    public void deleteById(Long id) {
        if (!dishRepository.existsById(id)) {
            throw new RuntimeException("Dish not found with id: " + id);
        }
        dishRepository.deleteById(id);
    }

    /* =========================
       VALIDATION
       ========================= */

    private void validateDish(Dish dish) {
        if (dish.getCategory() == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (dish.getName() == null || dish.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (dish.getPrice() == null) {
            throw new IllegalArgumentException("Price is required");
        }
        if (dish.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
    }
}

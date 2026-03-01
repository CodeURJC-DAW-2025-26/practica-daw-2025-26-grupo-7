package com.fuegolento.backend.service;

import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.model.Dish;
import com.fuegolento.backend.model.Image;
import com.fuegolento.backend.repository.DishRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;    
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final ImageService imageService;

    public DishService(DishRepository dishRepository, ImageService imageService) {
        this.dishRepository = dishRepository;
        this.imageService = imageService;
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

    // For AJAX pagination: combines search + filter + pagination
     public Page<Dish> findAvailableMenuPage(String q, DishCategory category, int page, int size) {
        String query = (q == null) ? "" : q.trim();

        // Sort is optional, but helps keep stable results
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

        boolean hasQuery = !query.isBlank();
        boolean hasCategory = category != null;

        if (hasQuery && hasCategory) {
            return dishRepository.findByNameContainingIgnoreCaseAndCategoryAndAvailableTrue(query, category, pageable);
        }
        if (hasQuery) {
            return dishRepository.findByNameContainingIgnoreCaseAndAvailableTrue(query, pageable);
        }
        if (hasCategory) {
            return dishRepository.findByCategoryAndAvailableTrue(category, pageable);
        }
        return dishRepository.findByAvailableTrue(pageable);
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

    public Dish create(Dish dish, MultipartFile imageFile) throws IOException {
        validateDish(dish);

        if (dish.getId() != null) {
            throw new IllegalArgumentException("New dish must not have an id");
        }

        // Attach image if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            Image img = imageService.createImage(imageFile);
            dish.setImage(img);
        }

        return dishRepository.save(dish);
    }

    public Dish update(Long id, Dish updated, MultipartFile imageFile) throws IOException {
        Dish existing = findById(id);

        validateDish(updated);

        existing.setCategory(updated.getCategory());
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setAllergens(updated.getAllergens());
        existing.setPrice(updated.getPrice());
        existing.setAvailable(updated.isAvailable());

        // Keep existing image unless a new one is provided
        if (imageFile != null && !imageFile.isEmpty()) {
            Image newImg = imageService.createImage(imageFile);
            existing.setImage(newImg); // orphanRemoval=true will delete old image
        }

        return dishRepository.save(existing);
    }

    public void deleteById(Long id) {
        if (!dishRepository.existsById(id)) {
            throw new RuntimeException("Dish not found with id: " + id);
        }
        dishRepository.deleteById(id);
        // Image is automatically removed thanks to cascade + orphanRemoval in Dish
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

package com.fuegolento.backend.service;

import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.exception.custom.ResourceNotFoundException;
import com.fuegolento.backend.model.Dish;
import com.fuegolento.backend.model.Image;
import com.fuegolento.backend.repository.DishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final ImageService imageService;

    public DishService(DishRepository dishRepository, ImageService imageService) {
        this.dishRepository = dishRepository;
        this.imageService = imageService;
    }

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

    public Page<Dish> findAvailableMenuPage(String q, DishCategory category, int page, int size) {
        String query = (q == null) ? "" : q.trim().toLowerCase();

        List<Dish> all = dishRepository.findByAvailableTrue();
        all.sort((a, b) -> Long.compare(a.getId(), b.getId()));

        List<Dish> filtered = new ArrayList<>();
        for (Dish d : all) {
            boolean okQuery = query.isBlank()
                    || (d.getName() != null && d.getName().toLowerCase().contains(query));

            boolean okCategory = (category == null)
                    || (d.getCategory() == category);

            if (okQuery && okCategory) {
                filtered.add(d);
            }
        }

        int fromIndex = page * size;
        if (fromIndex >= filtered.size()) {
            return new PageImpl<>(List.of(), PageRequest.of(page, size, Sort.by("id")), filtered.size());
        }

        int toIndex = Math.min(fromIndex + size, filtered.size());
        List<Dish> pageContent = filtered.subList(fromIndex, toIndex);

        return new PageImpl<>(pageContent, PageRequest.of(page, size, Sort.by("id")), filtered.size());
    }

    public List<Dish> findAll() {
        return dishRepository.findAll();
    }

    public Dish findById(Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dish not found with id: " + id));
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

    public Dish create(Dish dish, MultipartFile imageFile) throws IOException {
        validateDish(dish);

        if (dish.getId() != null) {
            throw new IllegalArgumentException("New dish must not have an id");
        }

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

        if (imageFile != null && !imageFile.isEmpty()) {
            Image newImg = imageService.createImage(imageFile);
            existing.setImage(newImg);
        }

        return dishRepository.save(existing);
    }

    /**
     * Updates only the image of a dish.
     */
    public Dish updateDishImage(Long id, MultipartFile imageFile) throws IOException {

        Dish existing = findById(id);

        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        Image newImg = imageService.createImage(imageFile);
        existing.setImage(newImg);

        return dishRepository.save(existing);
    }

    public void deleteById(Long id) {
        if (!dishRepository.existsById(id)) {
            throw new ResourceNotFoundException("Dish not found with id: " + id);
        }
        dishRepository.deleteById(id);
    }

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
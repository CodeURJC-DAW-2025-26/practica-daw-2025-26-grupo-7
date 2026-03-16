package com.fuegolento.backend.restController;

import com.fuegolento.backend.dto.DishDTO;
import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.mapper.DishMapper;
import com.fuegolento.backend.model.Dish;
import com.fuegolento.backend.service.DishService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/dishes")
public class DishRestController {

    private final DishService dishService;
    private final DishMapper dishMapper;

    public DishRestController(DishService dishService, DishMapper dishMapper) {
        this.dishService = dishService;
        this.dishMapper = dishMapper;
    }

    @GetMapping
    public ResponseEntity<List<DishDTO>> getDishes(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) DishCategory category,
            @RequestParam(defaultValue = "false") boolean availableOnly
    ) {
        List<Dish> dishes;

        if (availableOnly) {
            if (q != null && !q.isBlank()) {
                dishes = dishService.searchAvailableByName(q);
            } else if (category != null) {
                dishes = dishService.findAvailableByCategory(category);
            } else {
                dishes = dishService.findAllAvailable();
            }
        } else {
            if (q != null && !q.isBlank()) {
                dishes = dishService.searchByName(q);
            } else if (category != null) {
                dishes = dishService.findByCategory(category);
            } else {
                dishes = dishService.findAll();
            }
        }

        List<DishDTO> response = dishes.stream()
                .map(dishMapper::toDTO)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishDTO> getDishById(@PathVariable Long id) {
        Dish dish = dishService.findById(id);
        return ResponseEntity.ok(dishMapper.toDTO(dish));
    }

    @GetMapping("/page")
    public ResponseEntity<Page<DishDTO>> getPagedDishes(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) DishCategory category,
            @RequestParam(defaultValue = "true") boolean availableOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 0) {
            throw new IllegalArgumentException("Page must be 0 or greater");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }

        Page<DishDTO> result;

        if (availableOnly) {
            result = dishService.findAvailableMenuPage(q, category, page, size)
                    .map(dishMapper::toDTO);
        } else {
            List<Dish> dishes;

            if (q != null && !q.isBlank()) {
                dishes = dishService.searchByName(q);
            } else if (category != null) {
                dishes = dishService.findByCategory(category);
            } else {
                dishes = dishService.findAll();
            }

            int fromIndex = page * size;
            if (fromIndex >= dishes.size()) {
                result = Page.empty(PageRequest.of(page, size));
            } else {
                int toIndex = Math.min(fromIndex + size, dishes.size());
                List<DishDTO> content = dishes.subList(fromIndex, toIndex).stream()
                        .map(dishMapper::toDTO)
                        .toList();

                result = new PageImpl<>(content, PageRequest.of(page, size), dishes.size());
            }
        }

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<DishDTO> createDish(@RequestBody DishDTO dishDTO) throws IOException {
        Dish dish = dishMapper.toEntity(dishDTO);
        Dish createdDish = dishService.create(dish, null);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDish.getId())
                .toUri();

        return ResponseEntity.created(location).body(dishMapper.toDTO(createdDish));
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<DishDTO> uploadDishImage(@PathVariable Long id,
                                                   @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        Dish updatedDish = dishService.updateDishImage(id, imageFile);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/v1/images/{id}")
                .buildAndExpand(updatedDish.getImage().getId())
                .toUri();

        return ResponseEntity.created(location).body(dishMapper.toDTO(updatedDish));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishDTO> updateDish(@PathVariable Long id,
                                              @RequestBody DishDTO dishDTO) throws IOException {
        Dish dish = dishMapper.toEntity(dishDTO);
        Dish updatedDish = dishService.update(id, dish, null);

        return ResponseEntity.ok(dishMapper.toDTO(updatedDish));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
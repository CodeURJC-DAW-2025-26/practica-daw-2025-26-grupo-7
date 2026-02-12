package com.fuegolento.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.model.Dish;

/**
 * Repository for Dish entity.
 */
public interface DishRepository extends JpaRepository<Dish, Long> {

    // Public menu: only available dishes
    List<Dish> findByAvailableTrue();

    // Public menu by category: only available dishes
    List<Dish> findByCategoryAndAvailableTrue(DishCategory category);

    // Public search: only available dishes
    List<Dish> findByNameContainingIgnoreCaseAndAvailableTrue(String name);

    // Admin search (optional): includes unavailable dishes too
    List<Dish> findByCategory(DishCategory category);
    List<Dish> findByNameContainingIgnoreCase(String name);
}

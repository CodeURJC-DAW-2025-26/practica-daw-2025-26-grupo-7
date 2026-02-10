package com.fuegolento.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fuegolento.backend.enums.DishCategory;
import com.fuegolento.backend.model.Dish;

/**
 * Repository for Dish entity.
 */
public interface DishRepository extends JpaRepository<Dish, Long> {

    // Get all dishes of a given category
    List<Dish> findByCategory(DishCategory category);

    // Search dishes by name (case-insensitive)
    List<Dish> findByNameContainingIgnoreCase(String name);
}

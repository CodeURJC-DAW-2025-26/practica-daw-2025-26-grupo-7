package com.fuegolento.backend.mapper;

import com.fuegolento.backend.dto.DishDTO;
import com.fuegolento.backend.model.Dish;
import org.springframework.stereotype.Component;

@Component
public class DishMapper {

    private final ImageMapper imageMapper;

    public DishMapper(ImageMapper imageMapper) {
        this.imageMapper = imageMapper;
    }

    public DishDTO toDTO(Dish dish) {
        if (dish == null) {
            return null;
        }

        return new DishDTO(
                dish.getId(),
                dish.getCategory(),
                dish.getName(),
                dish.getDescription(),
                dish.getAllergens(),
                dish.getPrice(),
                dish.isAvailable(),
                imageMapper.toDTO(dish.getImage())
        );
    }

    public Dish toEntity(DishDTO dishDTO) {
        if (dishDTO == null) {
            return null;
        }

        Dish dish = new Dish();
        dish.setCategory(dishDTO.getCategory());
        dish.setName(dishDTO.getName());
        dish.setDescription(dishDTO.getDescription());
        dish.setAllergens(dishDTO.getAllergens());
        dish.setPrice(dishDTO.getPrice());
        dish.setAvailable(dishDTO.isAvailable());

        return dish;
    }
}
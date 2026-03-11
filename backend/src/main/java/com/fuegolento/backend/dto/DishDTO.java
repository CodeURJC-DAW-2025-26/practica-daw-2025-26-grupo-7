package com.fuegolento.backend.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fuegolento.backend.enums.Allergen;
import com.fuegolento.backend.enums.DishCategory;

public class DishDTO {

    private Long id;
    private DishCategory category;
    private String name;
    private String description;
    private List<Allergen> allergens;
    private BigDecimal price;
    private boolean available;
    private ImageDTO image;

    public DishDTO() {
    }

    public DishDTO(Long id, DishCategory category, String name, String description,
                   List<Allergen> allergens, BigDecimal price, boolean available, ImageDTO image) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.description = description;
        this.allergens = allergens;
        this.price = price;
        this.available = available;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DishCategory getCategory() {
        return category;
    }

    public void setCategory(DishCategory category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Allergen> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<Allergen> allergens) {
        this.allergens = allergens;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public ImageDTO getImage() {
        return image;
    }

    public void setImage(ImageDTO image) {
        this.image = image;
    }
}
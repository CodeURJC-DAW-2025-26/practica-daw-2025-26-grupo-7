package com.fuegolento.backend.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

import com.fuegolento.backend.enums.Allergen;
import com.fuegolento.backend.enums.DishCategory;

/**
 * Represents a menu dish.
 * Images are stored in the database as a BLOB to simplify deployment.
 */
@Entity(name = "DishTable")
public class Dish {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DishCategory category;

    private String name;

    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<Allergen> allergens;

    private BigDecimal price;

    @Lob
    private byte[] image;

    public Dish() {
        // Required by JPA
    }

    public Dish(DishCategory category, String name, String description, List<Allergen> allergens, BigDecimal price, byte[] image) {
        this.category = category;
        this.name = name;
        this.description = description;
        this.allergens = allergens;
        this.price = price;
        this.image = image;
    }

    public Long getId() {
        return id;
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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}

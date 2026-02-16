package com.fuegolento.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fuegolento.backend.model.Image;

/**
 * Repository for Image entity.
 */
public interface ImageRepository extends JpaRepository<Image, Long> {
}

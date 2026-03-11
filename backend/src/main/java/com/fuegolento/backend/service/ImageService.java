package com.fuegolento.backend.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fuegolento.backend.exception.custom.ResourceNotFoundException;
import com.fuegolento.backend.model.Image;
import com.fuegolento.backend.repository.ImageRepository;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image getImage(long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + id));
    }

    public Image createImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        Image image = new Image();
        try {
            image.setImageFile(new SerialBlob(imageFile.getBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }

        return imageRepository.save(image);
    }

    public Resource getImageFile(long id) throws SQLException {
        Image image = getImage(id);

        if (image.getImageFile() == null) {
            throw new ResourceNotFoundException("Image file not found");
        }

        return new InputStreamResource(image.getImageFile().getBinaryStream());
    }

    public void replaceImageFile(long id, MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new IllegalArgumentException("Image file is empty");
        }

        Image image = getImage(id);

        try {
            image.setImageFile(new SerialBlob(imageFile.getBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to replace image", e);
        }

        imageRepository.save(image);
    }

    public Image deleteImage(long id) {
        Image image = getImage(id);
        imageRepository.delete(image);
        return image;
    }
}
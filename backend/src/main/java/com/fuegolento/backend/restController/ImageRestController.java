package com.fuegolento.backend.restController;

import com.fuegolento.backend.dto.ImageDTO;
import com.fuegolento.backend.mapper.ImageMapper;
import com.fuegolento.backend.model.Image;
import com.fuegolento.backend.service.ImageService;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/images")
public class ImageRestController {

    private final ImageService imageService;
    private final ImageMapper imageMapper;

    public ImageRestController(ImageService imageService, ImageMapper imageMapper) {
        this.imageService = imageService;
        this.imageMapper = imageMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImageDTO> getImage(@PathVariable long id) {
        Image image = imageService.getImage(id);
        return ResponseEntity.ok(imageMapper.toDTO(image));
    }

    @GetMapping("/{id}/media")
    public ResponseEntity<Resource> getImageFile(@PathVariable long id) throws SQLException, IOException {
        Resource imageFile = imageService.getImageFile(id);

        MediaType mediaType = MediaTypeFactory
                .getMediaType(imageFile)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(imageFile);
    }

    @PutMapping("/{id}/media")
    public ResponseEntity<Void> replaceImageFile(@PathVariable long id,
                                                 @RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        imageService.replaceImageFile(id, imageFile);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ImageDTO> deleteImage(@PathVariable long id) {
        Image deletedImage = imageService.deleteImage(id);
        return ResponseEntity.ok(imageMapper.toDTO(deletedImage));
    }
}
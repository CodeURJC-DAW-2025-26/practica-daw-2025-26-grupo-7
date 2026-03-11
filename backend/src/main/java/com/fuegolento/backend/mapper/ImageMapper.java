package com.fuegolento.backend.mapper;

import com.fuegolento.backend.dto.ImageDTO;
import com.fuegolento.backend.model.Image;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public ImageDTO toDTO(Image image) {
        if (image == null) {
            return null;
        }
        return new ImageDTO(image.getId());
    }
}
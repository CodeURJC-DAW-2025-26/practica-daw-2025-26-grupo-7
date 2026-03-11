package com.fuegolento.backend.dto;

public class ImageDTO {

    private Long id;

    public ImageDTO() {
    }

    public ImageDTO(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
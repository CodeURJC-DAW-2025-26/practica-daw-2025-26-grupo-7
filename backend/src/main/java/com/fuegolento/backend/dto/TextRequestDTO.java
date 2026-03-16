package com.fuegolento.backend.dto;

public class TextRequestDTO {

    private String value;

    public TextRequestDTO() {
    }

    public TextRequestDTO(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
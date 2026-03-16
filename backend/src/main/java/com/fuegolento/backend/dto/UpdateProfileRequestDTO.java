package com.fuegolento.backend.dto;

import java.time.LocalDate;

public class UpdateProfileRequestDTO {

    private String email;
    private LocalDate birthDate;

    public UpdateProfileRequestDTO() {
    }

    public UpdateProfileRequestDTO(String email, LocalDate birthDate) {
        this.email = email;
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
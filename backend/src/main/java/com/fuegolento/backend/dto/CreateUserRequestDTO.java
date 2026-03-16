package com.fuegolento.backend.dto;

import java.time.LocalDate;

public class CreateUserRequestDTO {

    private String username;
    private String email;
    private String password;
    private LocalDate birthDate;

    public CreateUserRequestDTO() {
    }

    public CreateUserRequestDTO(String username, String email, String password, LocalDate birthDate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
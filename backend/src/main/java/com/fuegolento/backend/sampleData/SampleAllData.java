package com.fuegolento.backend.sampleData;

import java.time.LocalDate;

import jakarta.annotation.PostConstruct;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fuegolento.backend.model.User;
import com.fuegolento.backend.repository.UserRepository;

/**
 * Loads sample data into the database when the application starts.
 */
@Service
public class SampleAllData {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SampleAllData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        // Avoid duplicating data on every restart
        if (userRepository.count() > 0) {
            return;
        }

        userRepository.save(new User(
            "user",
            "user@fuegolento.com",
            LocalDate.of(2000, 5, 10),
            passwordEncoder.encode("user123"),
            "USER"
        ));

        userRepository.save(new User(
            "admin",
            "admin@fuegolento.com",
            LocalDate.of(1995, 1, 1),
            passwordEncoder.encode("admin123"),
            "USER", "ADMIN"
        ));
    }
}

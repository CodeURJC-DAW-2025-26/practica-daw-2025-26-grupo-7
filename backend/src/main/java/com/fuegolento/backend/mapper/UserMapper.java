package com.fuegolento.backend.mapper;

import com.fuegolento.backend.dto.UserDTO;
import com.fuegolento.backend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBirthDate(),
                user.getRoles(),
                user.isBanned(),
                user.getCreatedAt()
        );
    }

    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setBirthDate(userDTO.getBirthDate());
        user.setRoles(userDTO.getRoles());
        user.setBanned(userDTO.isBanned());
        user.setCreatedAt(userDTO.getCreatedAt());

        return user;
    }
}
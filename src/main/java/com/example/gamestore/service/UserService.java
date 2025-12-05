package com.example.gamestore.service;

import com.example.gamestore.dto.UserDTO;
import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getUserByUsername(String username);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);


    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
package com.example.gamestore.service;

import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    User save(User user);
    void deleteById(Long id);
    User createUser(String username, String email, String password);
    User updateUser(Long id, User userDetails);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByActiveTrue();

    List<UserDTO> getAllUsers();
    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getUserByUsername(String username);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
}
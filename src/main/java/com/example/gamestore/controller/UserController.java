package com.example.gamestore.controller;

import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("Getting all users");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("Getting user by id: {}", id);
        return userService.getUserById(id)
                .map(userDTO -> ResponseEntity.ok(userDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        log.info("Getting user by username: {}", username);
        return userService.getUserByUsername(username)
                .map(userDTO -> ResponseEntity.ok(userDTO))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            log.info("Updating user with id: {}", id);
            UserDTO updatedUser = userService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            log.error("Error updating user: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            log.info("Deleting user with id: {}", id);
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error deleting user: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/check-username/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
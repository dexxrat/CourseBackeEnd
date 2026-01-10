package com.example.gamestore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private Set<String> roles = new HashSet<>();
    private Boolean active = true;
    private LocalDateTime createdAt;

    public UserDTO() {}

    public UserDTO(Long id, String username, String email, Set<String> roles,
                   Boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles != null ? roles : new HashSet<>();
        this.active = active != null ? active : true;
        this.createdAt = createdAt;
    }
}
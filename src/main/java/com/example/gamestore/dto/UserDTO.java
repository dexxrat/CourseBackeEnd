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

    private String confirmPassword;
    private Set<String> roles = new HashSet<>();
    private Boolean active = true;
    private LocalDateTime createdAt;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;


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

    public UserDTO(Long id, String username, String email, Set<String> roles) {
        this(id, username, email, roles, true, null);
    }


    public boolean isActive() {
        return Boolean.TRUE.equals(active);
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    public boolean isAdmin() {
        return hasRole("ROLE_ADMIN");
    }

    public boolean isValidForRegistration() {
        return username != null && !username.trim().isEmpty() &&
                email != null && !email.trim().isEmpty() &&
                password != null && !password.trim().isEmpty() &&
                confirmPassword != null && !confirmPassword.trim().isEmpty() &&
                password.equals(confirmPassword);
    }

    public boolean isValidForUpdate() {
        return (username == null || username.length() >= 3) &&
                (email == null || email.contains("@")) &&
                (password == null || password.length() >= 6);
    }
}
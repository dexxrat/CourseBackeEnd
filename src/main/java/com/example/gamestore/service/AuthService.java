package com.example.gamestore.service;

import com.example.gamestore.dto.AuthRequest;
import com.example.gamestore.dto.AuthResponse;
import com.example.gamestore.dto.RegisterRequest;

public interface AuthService {
    AuthResponse authenticate(AuthRequest request);
    AuthResponse register(RegisterRequest request);
    boolean usernameExists(String username);
    boolean emailExists(String email);
}
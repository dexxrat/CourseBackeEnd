package com.example.gamestore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(min = 2, max = 50)
    private String firstName;

    @Size(min = 2, max = 50)
    private String lastName;

    @Email
    private String email;

    @Size(min = 10, max = 15)
    private String phoneNumber;

    @Size(max = 255)
    private String address;
}
package com.plh.foodappbackend.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String token; // Firebase ID Token
    private String fullName;
    private String email;
    private String password; // Optional, handled by Firebase usually
    private String role;
    private String phoneNumber;
    private String otp;
}

package com.plh.foodappbackend.request;

import lombok.Data;

@Data
public class PhoneLoginRequest {
    private String token; // Firebase ID Token
    private String fullName;
    private String email;
    private String role;
}

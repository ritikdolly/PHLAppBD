package com.plh.foodappbackend.response;

import com.plh.foodappbackend.model.USER_ROLE;
import com.plh.foodappbackend.model.User;
import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private String message;
    private String userId;
    private USER_ROLE role;
}

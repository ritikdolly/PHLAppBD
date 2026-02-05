package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.request.LoginRequest;
import com.plh.foodappbackend.response.AuthResponse;

public interface AuthService {
    AuthResponse register(com.plh.foodappbackend.request.RegisterRequest request);

    void sendRegisterOtp(String email);

    AuthResponse login(LoginRequest loginRequest);

    AuthResponse verifyEmail(String email, String otp);

    void sendLoginOtp(String email);

    AuthResponse loginWithOtp(String email, String otp);
}

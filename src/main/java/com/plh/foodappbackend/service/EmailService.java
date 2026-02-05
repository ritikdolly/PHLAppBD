package com.plh.foodappbackend.service;

public interface EmailService {
    void sendWelcomeEmail(String to);

    void sendLoginAlert(String to);

    void sendVerificationOtp(String to, String otp);
}

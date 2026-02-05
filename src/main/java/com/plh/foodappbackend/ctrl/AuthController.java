package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.request.LoginRequest;

import com.plh.foodappbackend.response.AuthResponse;
import com.plh.foodappbackend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody com.plh.foodappbackend.request.RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signup/otp")
    public ResponseEntity<String> sendRegisterOtp(@RequestBody com.plh.foodappbackend.request.RegisterRequest request) {
        // Reuse RegisterRequest just for email
        authService.sendRegisterOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("/verify/email")
    public ResponseEntity<AuthResponse> verifyEmail(@RequestBody com.plh.foodappbackend.model.VerificationCode code) {
        // Using VerificationCode model as DTO for simplicity, ideally create
        // VerifyOtpRequest
        AuthResponse response = authService.verifyEmail(code.getEmail(), code.getOtp());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signin(@RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/otp/send")
    public ResponseEntity<String> sendOtp(@RequestBody com.plh.foodappbackend.request.LoginRequest request) {
        // Reusing LoginRequest to carry email
        authService.sendLoginOtp(request.getEmail());
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("/otp/login")
    public ResponseEntity<AuthResponse> otpLogin(@RequestBody com.plh.foodappbackend.model.VerificationCode code) {
        // Using VerificationCode model as DTO (email, otp)
        AuthResponse response = authService.loginWithOtp(code.getEmail(), code.getOtp());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}

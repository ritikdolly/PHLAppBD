package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.model.USER_ROLE;
import com.plh.foodappbackend.model.VerificationCode;
import com.plh.foodappbackend.repository.UserRepository;
import com.plh.foodappbackend.repository.VerificationCodeRepository;
import com.plh.foodappbackend.request.LoginRequest;
import com.plh.foodappbackend.request.RegisterRequest;
import com.plh.foodappbackend.response.AuthResponse;
import com.plh.foodappbackend.security.JwtTokenProvider;
import com.plh.foodappbackend.service.AuthService;
import com.plh.foodappbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Value("${google.client.id}")
    private String googleClientId;

    @Override
    public void sendRegisterOtp(String email) {
        Optional<User> isEmailExist = userRepository.findByEmail(email);
        if (isEmailExist.isPresent()) {
            throw new RuntimeException("Email is already used with another account");
        }

        // Generate Email OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);
        if (verificationCode == null) {
            verificationCode = new VerificationCode();
            verificationCode.setEmail(email);
        }
        verificationCode.setOtp(otp);
        verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        verificationCodeRepository.save(verificationCode);

        emailService.sendVerificationOtp(email, otp);
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // 1. Verify OTP first
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(request.getEmail());
        if (verificationCode == null || !verificationCode.getOtp().equals(request.getOtp())) {
            throw new BadCredentialsException("Invalid OTP");
        }

        if (verificationCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("OTP Expired");
        }

        // 2. Clear OTP
        verificationCodeRepository.delete(verificationCode);

        // 3. Create User
        Optional<User> isEmailExist = userRepository.findByEmail(request.getEmail());
        if (isEmailExist.isPresent()) {
            throw new RuntimeException("Email is already used with another account");
        }
        Optional<User> isPhoneExist = userRepository.findByPhone(request.getPhoneNumber());
        if (isPhoneExist.isPresent()) {
            throw new RuntimeException("Phone number is already used with another account");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getFullName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // SECURITY: Public registration always assigns ROLE_CUSTOMER.
        // Privileged roles (ADMIN, RESTAURANT_OWNER, DELIVERY) can only be
        // assigned by an Admin via /api/admin/users endpoint.
        user.setRole(USER_ROLE.ROLE_CUSTOMER);

        if (request.getPhoneNumber() != null) {
            // Optional: Handle phone number if needed, but we rely on email
        }

        user.setEmailVerified(true); // Verified via OTP
        User savedUser = userRepository.save(user);

        // 4. Return Response
        AuthResponse authResponse = new AuthResponse();
        authResponse.setMessage("Register Success.");
        authResponse.setRole(savedUser.getRole());
        authResponse.setUserId(savedUser.getId());
        return authResponse;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect email or password");
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .or(() -> userRepository.findByPhone(loginRequest.getEmail()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEmailVerified()) {
            // Re-send OTP if needed or just block
            throw new RuntimeException("Email not verified. Please verify your email.");
        }

        String token = jwtTokenProvider.generateToken(authentication);

        emailService.sendLoginAlert(loginRequest.getEmail());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Login Success");
        authResponse.setRole(user.getRole());
        authResponse.setUserId(user.getId());
        return authResponse;
    }

    @Override
    public AuthResponse verifyEmail(String email, String otp) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("Invalid OTP");
        }

        if (verificationCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("OTP Expired");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmailVerified(true);
        userRepository.save(user);

        verificationCodeRepository.delete(verificationCode);

        // Authenticate the user to generate token
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("Email Verified and Login Success");
        authResponse.setRole(user.getRole());
        authResponse.setUserId(user.getId());
        return authResponse;
    }

    @Override
    public void sendLoginOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.format("%06d", new Random().nextInt(999999));

        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);
        if (verificationCode == null) {
            verificationCode = new VerificationCode();
            verificationCode.setEmail(email);
        }
        verificationCode.setOtp(otp);
        verificationCode.setExpiryDate(LocalDateTime.now().plusMinutes(5));
        verificationCodeRepository.save(verificationCode);

        emailService.sendVerificationOtp(user.getEmail(), otp);
    }

    @Override
    public AuthResponse loginWithOtp(String email, String otp) {
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(email);

        if (verificationCode == null || !verificationCode.getOtp().equals(otp)) {
            throw new BadCredentialsException("Invalid OTP");
        }

        if (verificationCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("OTP Expired");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEmailVerified()) {
            user.setEmailVerified(true);
            userRepository.save(user);
        }

        verificationCodeRepository.delete(verificationCode);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);

        emailService.sendLoginAlert(user.getEmail());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setMessage("OTP Login Success");
        authResponse.setRole(user.getRole());
        authResponse.setUserId(user.getId());
        return authResponse;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AuthResponse loginWithGoogle(String googleToken) {
        try {
            logger.info("Verifying Google token with client ID: {}...",
                    googleClientId != null ? googleClientId.substring(0, Math.min(20, googleClientId.length())) + "..."
                            : "NULL");

            // Use Google's tokeninfo endpoint to verify the ID token
            RestTemplate restTemplate = new RestTemplate();
            String tokenInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + googleToken;

            Map<String, Object> tokenInfo;
            try {
                tokenInfo = restTemplate.getForObject(tokenInfoUrl, Map.class);
            } catch (Exception e) {
                logger.error("Google token verification failed: {}", e.getMessage());
                throw new BadCredentialsException("Invalid Google token: verification failed");
            }

            if (tokenInfo == null) {
                throw new BadCredentialsException("Invalid Google token: empty response from Google");
            }

            // Verify the audience matches our client ID
            String aud = (String) tokenInfo.get("aud");
            if (!googleClientId.equals(aud)) {
                logger.error("Google token audience mismatch. Expected: {}, Got: {}", googleClientId, aud);
                throw new BadCredentialsException("Invalid Google token: audience mismatch");
            }

            String email = (String) tokenInfo.get("email");
            String name = (String) tokenInfo.get("name");

            if (email == null || email.isEmpty()) {
                throw new BadCredentialsException("Invalid Google token: no email in token");
            }

            logger.info("Google token verified successfully for email: {}", email);

            // Find existing user or create a new one
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name != null ? name : email);
                newUser.setEmailVerified(true);
                newUser.setRole(USER_ROLE.ROLE_CUSTOMER);
                // No password for Google users
                return userRepository.save(newUser);
            });

            String token = jwtTokenProvider.generateToken(email);

            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(token);
            authResponse.setMessage("Google Login Success");
            authResponse.setRole(user.getRole());
            authResponse.setUserId(user.getId());
            return authResponse;

        } catch (BadCredentialsException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Google authentication failed", e);
            throw new RuntimeException("Google authentication failed: " + e.getMessage());
        }
    }
}

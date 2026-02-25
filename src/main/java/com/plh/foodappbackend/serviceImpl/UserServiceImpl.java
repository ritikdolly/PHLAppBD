package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.model.USER_ROLE;
import com.plh.foodappbackend.repository.UserRepository;
import com.plh.foodappbackend.request.CreateUserRequest;
import com.plh.foodappbackend.security.JwtTokenProvider;
import com.plh.foodappbackend.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void addFavorite(String userId, String foodId) {
        User u = userRepository.findById(userId).orElse(null);
        if (u != null) {
            if (u.getFavorites() == null) {
                u.setFavorites(new ArrayList<>());
            }
            if (!u.getFavorites().contains(foodId)) {
                u.getFavorites().add(foodId);
                userRepository.save(u);
            }
        }
    }

    @Override
    public void removeFavorite(String userId, String foodId) {
        User u = userRepository.findById(userId).orElse(null);
        if (u != null && u.getFavorites() != null) {
            u.getFavorites().remove(foodId);
            userRepository.save(u);
        }
    }

    @Override
    public List<String> getFavorites(String userId) {
        User u = userRepository.findById(userId).orElse(null);
        return (u != null && u.getFavorites() != null) ? u.getFavorites() : new ArrayList<>();
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).orElse(null);
        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setPhone(user.getPhone());
            existingUser.setBio(user.getBio());
            existingUser.setAddress(user.getAddress());
            // SECURITY: Do NOT update role, email, or password here.
            // Role changes are restricted to Admin via /api/admin/users.
            // This prevents privilege escalation via profile update API tampering.
            return userRepository.save(existingUser);
        }
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        // Extract token from Bearer string if present
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }

        String email = jwtTokenProvider.getUsernameFromToken(jwt);

        if (email == null) {
            throw new Exception("Invalid token");
        }

        User user = findUserByEmail(email);
        if (user == null) {
            throw new Exception("User not found with email " + email);
        }
        return user;
    }

    @Override
    public User createUserByAdmin(CreateUserRequest request) {
        // Validate role
        USER_ROLE role;
        try {
            role = USER_ROLE.valueOf(request.getRole());
        } catch (Exception e) {
            throw new RuntimeException("Invalid role: " + request.getRole()
                    + ". Valid roles: ROLE_CUSTOMER, ROLE_ADMIN, ROLE_RESTAURANT_OWNER, ROLE_DELIVERY");
        }

        // Check for duplicate email
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email is already used with another account");
        }

        User user = new User();
        user.setName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhoneNumber());
        user.setRole(role);
        user.setEmailVerified(true); // Admin-created users skip email verification

        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.request.CreateUserRequest;
import com.plh.foodappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin-only controller for user management.
 * All endpoints under /api/admin/users are secured by SecurityConfig
 * to require ROLE_ADMIN.
 */
@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * Create a new user with any role (Admin only).
     * This is the ONLY way to create users with elevated roles
     * (ROLE_ADMIN, ROLE_RESTAURANT_OWNER, ROLE_DELIVERY).
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User user = userService.createUserByAdmin(request);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    /**
     * List all users (Admin only).
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}

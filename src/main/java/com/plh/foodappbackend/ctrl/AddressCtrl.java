package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.Address;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressCtrl {

    @Autowired
    private UserRepository userRepository;

    // Helper to get dummy user for now since Auth is unclear.
    // In a real app, this would get the user from the SecurityContext
    private User getUser() throws Exception {
        // Fetch the first user or a specific test user
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new Exception("No users found");
        }
        return users.get(0);
    }

    @PostMapping
    public ResponseEntity<User> addAddress(@RequestBody Address address) {
        try {
            User user = getUser();
            if (user.getAddress() == null) {
                user.setAddress(new ArrayList<>());
            }
            user.getAddress().add(address);
            userRepository.save(user);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Address>> getAddresses() {
        try {
            User user = getUser();
            return ResponseEntity.ok(user.getAddress() != null ? user.getAddress() : new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable String id) {
        try {
            User user = getUser();
            if (user.getAddress() != null) {
                user.getAddress().removeIf(address -> address.getId().equals(id));
                userRepository.save(user);
            }
            return ResponseEntity.ok("Address deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete address");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateAddress(@PathVariable String id, @RequestBody Address updatedAddress) {
        try {
            User user = getUser();
            if (user.getAddress() != null) {
                for (int i = 0; i < user.getAddress().size(); i++) {
                    Address address = user.getAddress().get(i);
                    if (address.getId().equals(id)) {
                        // Preserve the ID
                        updatedAddress.setId(id);
                        user.getAddress().set(i, updatedAddress);
                        userRepository.save(user);
                        return ResponseEntity.ok(user);
                    }
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

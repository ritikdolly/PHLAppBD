package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.Address;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressCtrl {

    @Autowired
    private UserService userService;

    private User resolveUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("AddressCtrl.resolveUser: Auth: " + authentication);
            if (authentication != null && authentication.isAuthenticated()
                    && !authentication.getPrincipal().equals("anonymousUser")) {
                Object principal = authentication.getPrincipal();
                String email = null;

                if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                    email = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                } else if (principal instanceof String) {
                    email = (String) principal;
                }

                System.out.println("AddressCtrl.resolveUser: Email: " + email);

                if (email != null) {
                    return userService.findUserByEmail(email);
                }
            }
        } catch (Exception e) {
            System.out.println("Error resolving user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping
    public ResponseEntity<Address> addAddress(@RequestBody @jakarta.validation.Valid Address address,
            @RequestHeader(value = "Authorization", required = false) String jwt) {
        try {
            User user = resolveUser();
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (user.getAddress() == null) {
                user.setAddress(new ArrayList<>());
            }

            // Ensure ID is generated if not present (though initialized in model)
            if (address.getId() == null || address.getId().isEmpty()) {
                address.setId(java.util.UUID.randomUUID().toString());
            }

            user.getAddress().add(address);
            userService.updateUser(user);
            return ResponseEntity.ok(address);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Address>> getAddresses(
            @RequestHeader(value = "Authorization", required = false) String jwt) {
        try {
            User user = resolveUser();
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return ResponseEntity.ok(user.getAddress() != null ? user.getAddress() : new ArrayList<>());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAddress(@PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String jwt) {
        try {
            User user = resolveUser();
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (user.getAddress() != null) {
                boolean removed = user.getAddress().removeIf(address -> address.getId().equals(id));
                if (removed) {
                    userService.updateUser(user);
                    return ResponseEntity.ok("Address deleted successfully");
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete address");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable String id, @RequestBody Address updatedAddress,
            @RequestHeader(value = "Authorization", required = false) String jwt) {
        try {
            User user = resolveUser();
            if (user == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }

            if (user.getAddress() != null) {
                for (int i = 0; i < user.getAddress().size(); i++) {
                    Address address = user.getAddress().get(i);
                    if (address.getId().equals(id)) {
                        // Preserve the ID but update other fields
                        updatedAddress.setId(id);
                        user.getAddress().set(i, updatedAddress);
                        userService.updateUser(user);
                        return ResponseEntity.ok(updatedAddress);
                    }
                }
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

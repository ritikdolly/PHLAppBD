package com.plh.foodappbackend.request;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String fullName;
    private String email;
    private String password;
    private String phoneNumber;
    private String role; // e.g. ROLE_ADMIN, ROLE_CUSTOMER, ROLE_RESTAURANT_OWNER, ROLE_DELIVERY
}

package com.plh.foodappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String id = java.util.UUID.randomUUID().toString();

    @jakarta.validation.constraints.NotBlank(message = "Street is required")
    private String street;

    @jakarta.validation.constraints.NotBlank(message = "City is required")
    private String city;

    private String district;

    @jakarta.validation.constraints.NotBlank(message = "State is required")
    private String state;

    @jakarta.validation.constraints.NotBlank(message = "PIN code is required")
    @jakarta.validation.constraints.Pattern(regexp = "^[0-9]{6}$", message = "Invalid PIN code")
    private String pin;

    @jakarta.validation.constraints.NotBlank(message = "Country is required")
    private String country;

    @jakarta.validation.constraints.NotBlank(message = "Mobile number is required")
    private String mobile;

    private String type;
}

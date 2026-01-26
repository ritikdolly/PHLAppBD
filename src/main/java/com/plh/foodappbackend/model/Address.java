package com.plh.foodappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private String id = java.util.UUID.randomUUID().toString();
    private String street;
    private String city;
    private String district;
    private String state;
    private String pin;
    private String country;
}

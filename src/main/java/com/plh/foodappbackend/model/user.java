package com.plh.foodappbackend.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("users")
public class user {
    @Id
    private String id;
    @NotBlank(message = "Name is Required")
    private String name;
    private String email;
    @NotBlank(message = "Mobile Number is Required")
    private String mobileNumber;
    private List<Address> address;
    private List<Order> order;
    private List<Cart> cart;
}

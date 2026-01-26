package com.plh.foodappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId; // Reference to User
    private String date;
    private double total;
    private String status; // Delivered, Processing, Cancelled
    private List<OrderItem> items;
    private String img; // Display image for the order
    private Address address;
    private String paymentMethod;
    private String paymentId;
}

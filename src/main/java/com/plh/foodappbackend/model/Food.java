package com.plh.foodappbackend.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "foods")
public class Food {

    @Id
    private String id; // nullable (Mongo will auto-generate if null)

    @NotBlank(message = "Food name is required")
    private String name;

    @NotEmpty(message = "At least one type is required")
    private List<String> types;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @Positive(message = "Price must be positive")
    private double price;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private double rating = 0.0;   // default

    @NotBlank
    private String quantity;

    private List<String> customerImages; // nullable OK

    private List<Review> reviews; // nullable OK

    @Size(max = 200)
    private String comments;

    private boolean availability = true; // default
}

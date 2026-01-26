package com.plh.foodappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String userId; // Store userId instead of name if possible, but keeping simple for now
    private String foodId;
    private String userName; // Denormalized for display
    private String foodName; // Denormalized for display
    private double rating;
    private String comment;
    private String date; // Keep as String for simplicity or LocalDateTime
}

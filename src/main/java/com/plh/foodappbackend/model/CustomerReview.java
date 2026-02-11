package com.plh.foodappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "customer_reviews")
public class CustomerReview {
    @Id
    private String id;

    private String userId;
    private String userName; // Storing name to avoid extra lookups

    private String comment;
    private double rating; // 1 to 5

    private Date createdAt = new Date();
}

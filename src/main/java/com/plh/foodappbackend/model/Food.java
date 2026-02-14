package com.plh.foodappbackend.model;

import java.math.BigDecimal;
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
    private BigDecimal price;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private double rating = 0.0; // default

    @NotBlank
    private String quantity; // e.g. "1 kg", "500g" - display only

    @PositiveOrZero(message = "Stock cannot be negative")
    private int stock = 0; // Inventory tracking

    private List<String> customerImages; // nullable OK

    private List<Review> reviews; // nullable OK

    @Size(max = 200)
    private String comments;

    private boolean availability = true; // default

    // Offer System Fields
    private String offerType; // "percentage" or "flat"
    private BigDecimal offerValue; // Amount in ₹ or % value
    private java.util.Date offerStartDate;
    private java.util.Date offerEndDate;

    @com.fasterxml.jackson.annotation.JsonProperty("isOfferActive")
    private boolean isOfferActive = false;

    // Computed property for API response
    @com.fasterxml.jackson.annotation.JsonProperty("discountedPrice")
    public BigDecimal getDiscountedPrice() {
        if (!isOfferActive || offerType == null || offerValue == null) {
            return price;
        }

        java.util.Date now = new java.util.Date();
        if (offerStartDate != null && now.before(offerStartDate)) {
            return price;
        }
        if (offerEndDate != null && now.after(offerEndDate)) {
            return price;
        }

        BigDecimal discounted = price;

        if ("percentage".equalsIgnoreCase(offerType)) {
            BigDecimal discount = price.multiply(offerValue).divide(BigDecimal.valueOf(100));
            discounted = price.subtract(discount);
        } else if ("flat".equalsIgnoreCase(offerType)) {
            discounted = price.subtract(offerValue);
        }

        // Ensure price never goes below 0
        if (discounted.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return discounted.setScale(2, java.math.RoundingMode.HALF_UP);
    }
}

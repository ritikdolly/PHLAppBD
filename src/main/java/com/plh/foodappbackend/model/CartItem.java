package com.plh.foodappbackend.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private String foodId;
    private int quantity;
    private BigDecimal price; // Price at the time of adding to cart
    private String name;
    private String imageUrl;
}

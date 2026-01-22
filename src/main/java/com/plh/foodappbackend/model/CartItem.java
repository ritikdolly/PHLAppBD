package com.plh.foodappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    private String foodId;
    private int quantity;
    private double price; // Price at the time of adding to cart
}

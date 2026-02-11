package com.plh.foodappbackend.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private String foodId;
    private String foodName;
    private String foodImage;
    private BigDecimal price;
    private int quantity;
    private BigDecimal totalPrice;
}

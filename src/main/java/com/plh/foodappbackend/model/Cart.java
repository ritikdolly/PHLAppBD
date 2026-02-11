package com.plh.foodappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;
    private String userId;
    private String sessionId;
    private List<CartItem> items;
    private BigDecimal totalItemPrice; // Sum of items
    private BigDecimal deliveryFee;
    private BigDecimal tax;
    private BigDecimal totalAmount; // Grand Total
}

package com.plh.foodappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private String userName; // Snapshot
    private List<OrderItem> items;
    private BigDecimal totalItemPrice;
    private BigDecimal deliveryFee;
    private BigDecimal tax;
    private BigDecimal totalAmount;
    private Address shippingAddress;
    private ORDER_STATUS status;
    private PaymentDetails paymentDetails;
    private int totalItem;
    private Date createdAt;

    // Delivery Man tracking fields
    private String deliveryManId;
    private String deliveryManName;
    private String deliveryManMobile;
    private Date deliveredAt;
}

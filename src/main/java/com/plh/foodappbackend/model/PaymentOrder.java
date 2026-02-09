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
@Document(collection = "payment_orders")
public class PaymentOrder {
    @Id
    private String id;
    private String razorpayOrderId;
    private Long amount;
    private String currency;
    private String status;
    private String paymentId;
    private String userId;
    private String receipt;
    private Date createdAt;
}

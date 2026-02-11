package com.plh.foodappbackend.request;

import lombok.Data;

@Data
public class PaymentVerificationRequest {
    private String orderId;
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;
    private com.plh.foodappbackend.model.Address shippingAddress;
}

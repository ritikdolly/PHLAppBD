package com.plh.foodappbackend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDetails {
    private PAYMENT_METHOD paymentMethod;
    private PAYMENT_STATUS status;
    private String razorpayPaymentId;
    private String razorpayOrderId;
    private String razorpaySignature;
}

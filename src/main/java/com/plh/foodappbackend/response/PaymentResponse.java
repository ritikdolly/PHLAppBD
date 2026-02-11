package com.plh.foodappbackend.response;

import lombok.Data;

@Data
public class PaymentResponse {
    private String payment_url;
    private long amount;
    private String currency;
}

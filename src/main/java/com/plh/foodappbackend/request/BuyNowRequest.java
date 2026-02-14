package com.plh.foodappbackend.request;

import lombok.Data;

@Data
public class BuyNowRequest {
    private String foodId;
    private int quantity;
}

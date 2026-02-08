package com.plh.foodappbackend.request;

import lombok.Data;

@Data
public class AddCartRequest {
    private String foodId;
    private int quantity;
}

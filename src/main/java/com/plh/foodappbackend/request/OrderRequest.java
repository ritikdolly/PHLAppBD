package com.plh.foodappbackend.request;

import com.plh.foodappbackend.model.Address;
import com.plh.foodappbackend.model.PAYMENT_METHOD;
import lombok.Data;

@Data
public class OrderRequest {
    private Address shippingAddress;
    private PAYMENT_METHOD paymentMethod;
}

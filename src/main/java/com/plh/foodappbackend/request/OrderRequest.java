package com.plh.foodappbackend.request;

import com.plh.foodappbackend.model.Address;
import com.plh.foodappbackend.model.PAYMENT_METHOD;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class OrderRequest {
    @NotNull(message = "Shipping address is required")
    private Address shippingAddress;

    @NotNull(message = "Payment method is required")
    private PAYMENT_METHOD paymentMethod;

    // Optional: If present, this is a buy-now order for a single item
    private BuyNowItem buyNowItem;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuyNowItem {
        @NotBlank(message = "Food ID is required")
        private String foodId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
    }
}

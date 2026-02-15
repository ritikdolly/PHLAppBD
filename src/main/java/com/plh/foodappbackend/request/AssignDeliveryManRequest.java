package com.plh.foodappbackend.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssignDeliveryManRequest {
    @NotBlank(message = "Delivery man ID is required")
    private String deliveryManId;
}

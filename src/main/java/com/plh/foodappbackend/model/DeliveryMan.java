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
@Document(collection = "delivery_men")
public class DeliveryMan {
    @Id
    private String id;
    private String name;
    private String mobileNumber;
    private String email;
    private String vehicleType;
    private AVAILABILITY_STATUS availabilityStatus;
    private Date createdAt;
    private Date updatedAt;
}

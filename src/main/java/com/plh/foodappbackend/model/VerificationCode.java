package com.plh.foodappbackend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "verification_codes")
public class VerificationCode {
    @Id
    private String id;
    private String email;
    private String otp;
    private LocalDateTime expiryDate;
}

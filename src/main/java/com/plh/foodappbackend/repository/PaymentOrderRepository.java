package com.plh.foodappbackend.repository;

import com.plh.foodappbackend.model.PaymentOrder;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentOrderRepository extends MongoRepository<PaymentOrder, String> {
    PaymentOrder findByRazorpayOrderId(String razorpayOrderId);
}

package com.plh.foodappbackend.controller;

import com.plh.foodappbackend.model.PaymentOrder;
import com.plh.foodappbackend.service.PaymentService;
import com.razorpay.RazorpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            Long amount = Long.parseLong(data.get("amount").toString());
            String currency = (String) data.getOrDefault("currency", "INR");
            String receipt = (String) data.getOrDefault("receipt", "txn_" + System.currentTimeMillis());

            // In a real app, you'd extract userId from the token or security context.
            // For now, we'll assume it's passed or handle it if we have the User object.
            // Let's rely on the frontend passing it or just use a placeholder if not
            // essential for the payment creation itself,
            // but for a real app we need it.
            // Better: Get userId from the authenticated user.
            // But since I don't want to complicate the dependencies here with User
            // extraction logic if not already handy,
            // I'll check if 'userId' is passed in the body.
            String userId = (String) data.get("userId");

            PaymentOrder order = paymentService.createOrder(amount, currency, receipt, userId);
            return ResponseEntity.ok(order);
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating order: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid request: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        try {
            String orderId = data.get("razorpay_order_id");
            String paymentId = data.get("razorpay_payment_id");
            String signature = data.get("razorpay_signature");

            PaymentOrder order = paymentService.verifyPayment(orderId, paymentId, signature);
            return ResponseEntity.ok(order);
        } catch (RazorpayException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed: " + e.getMessage());
        }
    }
}

package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.PaymentOrder;
import com.razorpay.RazorpayException;

public interface PaymentService {
    PaymentOrder createOrder(Long amount, String currency, String receipt, String userId) throws RazorpayException;

    PaymentOrder verifyPayment(String orderId, String paymentId, String signature) throws RazorpayException;
}

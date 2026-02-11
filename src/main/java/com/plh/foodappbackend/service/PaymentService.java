package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.response.PaymentResponse;
import com.razorpay.RazorpayException;

public interface PaymentService {
    PaymentResponse createPaymentLink(Order order) throws RazorpayException;

    PaymentResponse createRazorpayOrder(java.math.BigDecimal amount) throws RazorpayException;
}

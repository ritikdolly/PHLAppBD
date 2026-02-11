package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.response.PaymentResponse;
import com.plh.foodappbackend.service.PaymentService;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String apiKey;

    @Value("${razorpay.key.secret}")
    private String apiSecret;

    @Override
    public PaymentResponse createPaymentLink(Order order) throws RazorpayException {
        // Since we are using standard checkout, we might just need order_id
        // But requested specifically to create payment link or order

        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
        JSONObject orderRequest = new JSONObject();
        // Amount in paise
        orderRequest.put("amount", order.getTotalAmount().multiply(BigDecimal.valueOf(100)).longValue()); // Casting to
                                                                                                          // long for
                                                                                                          // paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "order_" + order.getId());

        com.razorpay.Order createdOrder = razorpay.orders.create(orderRequest);

        String orderId = createdOrder.get("id");

        PaymentResponse response = new PaymentResponse();
        response.setPayment_url(orderId);
        response.setAmount(orderRequest.getLong("amount"));
        response.setCurrency(orderRequest.getString("currency"));

        return response;// We return the orderId as the 'url' or identifier for frontend to use in
                        // checkout options
    }

    @Override
    public PaymentResponse createRazorpayOrder(BigDecimal amount) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
        JSONObject orderRequest = new JSONObject();
        // Amount in paise
        orderRequest.put("amount", amount.multiply(BigDecimal.valueOf(100)).longValue());
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt_" + System.currentTimeMillis());

        com.razorpay.Order createdOrder = razorpay.orders.create(orderRequest);
        String orderId = createdOrder.get("id");

        PaymentResponse response = new PaymentResponse();
        response.setPayment_url(orderId);

        return response;
    }
}

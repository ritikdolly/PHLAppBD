package com.plh.foodappbackend.serviceImpl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    // In a real app, these should be in application.properties
    // user should provide these
    private String KEY_ID = "rzp_test_YOUR_KEY_ID";
    private String KEY_SECRET = "YOUR_KEY_SECRET";

    public String createOrder(int amount) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient(KEY_ID, KEY_SECRET);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpay.orders.create(orderRequest);
        return order.toString();
    }
}

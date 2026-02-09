package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.PaymentOrder;
import com.plh.foodappbackend.repository.PaymentOrderRepository;
import com.plh.foodappbackend.service.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    public PaymentOrder createOrder(Long amount, String currency, String receipt, String userId)
            throws RazorpayException {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // Amount in paise
        orderRequest.put("currency", currency);
        orderRequest.put("receipt", receipt);
        orderRequest.put("payment_capture", 1); // Auto capture

        Order order = client.orders.create(orderRequest);

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setRazorpayOrderId(order.get("id"));
        paymentOrder.setAmount(amount * 100);
        paymentOrder.setCurrency(currency);
        paymentOrder.setStatus("CREATED");
        paymentOrder.setUserId(userId);
        paymentOrder.setReceipt(receipt);
        paymentOrder.setCreatedAt(new Date());

        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder verifyPayment(String orderId, String paymentId, String signature) throws RazorpayException {
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", orderId);
        options.put("razorpay_payment_id", paymentId);
        options.put("razorpay_signature", signature);

        boolean isValid = Utils.verifyPaymentSignature(options, keySecret);

        if (isValid) {
            PaymentOrder paymentOrder = paymentOrderRepository.findByRazorpayOrderId(orderId);
            if (paymentOrder != null) {
                paymentOrder.setStatus("PAID");
                paymentOrder.setPaymentId(paymentId);
                return paymentOrderRepository.save(paymentOrder);
            }
        }

        throw new RazorpayException("Payment verification failed");
    }
}

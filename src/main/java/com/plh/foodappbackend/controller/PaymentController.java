package com.plh.foodappbackend.controller;

import com.plh.foodappbackend.model.Cart;
import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.model.ORDER_STATUS;
import com.plh.foodappbackend.model.PAYMENT_METHOD;
import com.plh.foodappbackend.model.PAYMENT_STATUS;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.repository.OrderRepository;
import com.plh.foodappbackend.request.OrderRequest;
import com.plh.foodappbackend.request.PaymentVerificationRequest;
import com.plh.foodappbackend.response.PaymentResponse;
import com.plh.foodappbackend.service.CartService;
import com.plh.foodappbackend.service.OrderService;
import com.plh.foodappbackend.service.PaymentService;
import com.plh.foodappbackend.service.UserService;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Value("${razorpay.key.secret}")
    private String apiSecret;

    @PostMapping("/payment/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(@RequestHeader("Authorization") String jwt)
            throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Cart cart = cartService.getCart(user);

        if (cart.getItems().isEmpty()) {
            throw new Exception("Cart is empty");
        }

        // Calculate total amount from cart (already calculated in getCart usually, but
        // ensures it matches backend)
        // Accessing totalAmount directly from Cart
        if (cart.getTotalAmount() == null) {
            // force calculation if null, though getCart usually does it
            // Assuming CartService logic handles this.
            // If not, we might need to trigger calculation.
        }

        PaymentResponse res = paymentService.createRazorpayOrder(cart.getTotalAmount());
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/payment/initiate/{orderId}")
    public ResponseEntity<PaymentResponse> initiatePaymentForOrder(@PathVariable String orderId,
            @RequestHeader("Authorization") String jwt)
            throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.findOrderById(orderId);

        if (!order.getUserId().equals(user.getId())) {
            throw new Exception("Access Denied");
        }

        PaymentResponse res = paymentService.createPaymentLink(order);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PutMapping("/payment/verify")
    public ResponseEntity<Order> verifyPayment(@RequestBody PaymentVerificationRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {
        // valid signature
        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", req.getRazorpayOrderId());
        options.put("razorpay_payment_id", req.getRazorpayPaymentId());
        options.put("razorpay_signature", req.getRazorpaySignature());

        boolean valid = Utils.verifyPaymentSignature(options, apiSecret);

        if (valid) {
            User user = userService.findUserByJwtToken(jwt);

            // Create OrderRequest
            OrderRequest orderRequest = new OrderRequest();
            orderRequest.setShippingAddress(req.getShippingAddress());
            orderRequest.setPaymentMethod(PAYMENT_METHOD.ONLINE); // Or get from req if we expanded it

            // Create Order (This clears the cart)
            Order order = orderService.createOrder(orderRequest, user);

            // Update Order Payment Details
            order.getPaymentDetails().setRazorpayPaymentId(req.getRazorpayPaymentId());
            order.getPaymentDetails().setRazorpayOrderId(req.getRazorpayOrderId());
            order.getPaymentDetails().setRazorpaySignature(req.getRazorpaySignature());
            order.getPaymentDetails().setStatus(PAYMENT_STATUS.PAID);

            // Update Order Status
            order.setStatus(ORDER_STATUS.CONFIRMED);

            Order savedOrder = orderRepository.save(order);
            return new ResponseEntity<>(savedOrder, HttpStatus.OK);
        }

        throw new Exception("Payment Failed");
    }
}

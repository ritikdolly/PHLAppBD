package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.*;
import com.plh.foodappbackend.repository.OrderRepository;
import com.plh.foodappbackend.repository.UserRepository;
import com.plh.foodappbackend.request.OrderRequest;
import com.plh.foodappbackend.service.CartService;
import com.plh.foodappbackend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Override
    public Order createOrder(OrderRequest req, User user) throws Exception {
        if (user == null) {
            throw new Exception("User not found");
        }

        Address shippingAddress = req.getShippingAddress();

        Cart cart = cartService.getCart(user);
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setFoodId(item.getFoodId());
            orderItem.setFoodName(item.getName());
            orderItem.setFoodImage(item.getImageUrl());
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(item.getPrice());

            // Derive unit price safely
            if (item.getQuantity() > 0) {
                orderItem.setPrice(
                        item.getPrice().divide(BigDecimal.valueOf(item.getQuantity()), java.math.RoundingMode.HALF_UP));
            } else {
                orderItem.setPrice(BigDecimal.ZERO);
            }

            orderItems.add(orderItem);
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setUserName(user.getName());
        order.setItems(orderItems);
        order.setTotalAmount(cart.getTotalAmount());
        order.setTotalItemPrice(cart.getTotalItemPrice());
        order.setDeliveryFee(cart.getDeliveryFee());
        order.setTax(cart.getTax());
        order.setShippingAddress(shippingAddress);
        order.setTotalItem(cart.getItems().size());
        order.setCreatedAt(new Date());

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setPaymentMethod(req.getPaymentMethod());

        if (req.getPaymentMethod() == PAYMENT_METHOD.COD) {
            paymentDetails.setStatus(PAYMENT_STATUS.PENDING);
            order.setStatus(ORDER_STATUS.PENDING);
        } else {
            paymentDetails.setStatus(PAYMENT_STATUS.PENDING);
            order.setStatus(ORDER_STATUS.PAYMENT_PENDING);
        }

        order.setPaymentDetails(paymentDetails);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(user);

        return savedOrder;
    }

    @Override
    public Order updateOrder(String orderId, String orderStatus) throws Exception {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new Exception("Order not found with id " + orderId);
        }
        Order order = orderOpt.get();

        if (orderStatus.equals("OUT_FOR_DELIVERY") ||
                orderStatus.equals("DELIVERED") ||
                orderStatus.equals("COMPLETED") ||
                orderStatus.equals("PENDING") ||
                orderStatus.equals("CONFIRMED")) {

            try {
                ORDER_STATUS status = ORDER_STATUS.valueOf(orderStatus);
                order.setStatus(status);
            } catch (IllegalArgumentException e) {
                throw new Exception("Invalid order status");
            }
            return orderRepository.save(order);
        }
        throw new Exception("Please select a valid order status");
    }

    @Override
    public void cancelOrder(String orderId) throws Exception {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new Exception("Order not found");
        }
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<Order> getUsersOrder(String userId) throws Exception {
        return orderRepository.findByUserId(userId);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public java.util.Map<String, Object> getDashboardStats() {
        List<Order> orders = getAllOrders();
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        long totalOrders = orders.size();

        BigDecimal revenue = BigDecimal.ZERO;
        for (Order order : orders) {
            if (order.getTotalAmount() != null) {
                revenue = revenue.add(order.getTotalAmount());
            }
        }

        long pendingOrders = orders.stream().filter(o -> o.getStatus() == ORDER_STATUS.PENDING ||
                o.getStatus() == ORDER_STATUS.PAYMENT_PENDING ||
                o.getStatus() == ORDER_STATUS.CONFIRMED ||
                o.getStatus() == ORDER_STATUS.PREPARING ||
                o.getStatus() == ORDER_STATUS.OUT_FOR_DELIVERY).count();

        long completedOrders = orders.stream()
                .filter(o -> o.getStatus() == ORDER_STATUS.DELIVERED || o.getStatus() == ORDER_STATUS.COMPLETED)
                .count();
        long cancelledOrders = orders.stream().filter(o -> o.getStatus() == ORDER_STATUS.CANCELLED).count();

        stats.put("totalOrders", totalOrders);
        stats.put("revenue", revenue);
        stats.put("pendingOrders", pendingOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("cancelledOrders", cancelledOrders);

        // Placeholder for other stats expected by frontend, can be real implementation
        // later
        stats.put("foodItems", 0);
        stats.put("activeOffers", 0);

        return stats;
    }

    @Override
    public Order findOrderById(String orderId) throws Exception {
        Optional<Order> order = orderRepository.findById(orderId);
        if (order.isEmpty()) {
            throw new Exception("Order not found");
        }
        return order.get();
    }
}

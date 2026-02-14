package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.*;
import com.plh.foodappbackend.repository.OrderRepository;
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
    private com.plh.foodappbackend.repository.FoodRepository foodRepository;

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
                orderStatus.equals("CONFIRMED") ||
                orderStatus.equals("CANCELLED")) {

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
    public java.util.Map<String, Object> getDashboardStats(String period) {
        List<Order> allOrders = getAllOrders();
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        // Filter orders based on period
        List<Order> filteredOrders = filterOrdersByPeriod(allOrders, period);

        long totalOrders = filteredOrders.size();
        BigDecimal revenue = BigDecimal.ZERO;
        for (Order order : filteredOrders) {
            if (order.getStatus() != ORDER_STATUS.CANCELLED && order.getTotalAmount() != null) {
                revenue = revenue.add(order.getTotalAmount());
            }
        }

        long pendingOrders = filteredOrders.stream().filter(o -> o.getStatus() == ORDER_STATUS.PENDING ||
                o.getStatus() == ORDER_STATUS.PAYMENT_PENDING ||
                o.getStatus() == ORDER_STATUS.CONFIRMED ||
                o.getStatus() == ORDER_STATUS.PREPARING ||
                o.getStatus() == ORDER_STATUS.OUT_FOR_DELIVERY).count();

        long completedOrders = filteredOrders.stream()
                .filter(o -> o.getStatus() == ORDER_STATUS.DELIVERED || o.getStatus() == ORDER_STATUS.COMPLETED)
                .count();
        long cancelledOrders = filteredOrders.stream().filter(o -> o.getStatus() == ORDER_STATUS.CANCELLED).count();

        stats.put("totalOrders", totalOrders);
        stats.put("revenue", revenue);
        stats.put("pendingOrders", pendingOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("cancelledOrders", cancelledOrders);

        // Food Items count
        long foodItems = foodRepository.count();
        stats.put("foodItems", foodItems);
        stats.put("activeOffers", 0); // No Offer model yet

        // Revenue Trends (Last 7 days or based on period - keeping it simple for now)
        // Group revenue by date (simplified approach)
        java.util.Map<String, BigDecimal> revenueTrends = new java.util.LinkedHashMap<>();
        // Logic to populate trends could be added here, but for now let's provide basic
        // aggregated data or just placeholder structure if complex chart needed.
        // For distinct visual, let's group by day for the filter period.
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        for (Order order : filteredOrders) {
            if (order.getStatus() != ORDER_STATUS.CANCELLED && order.getCreatedAt() != null
                    && order.getTotalAmount() != null) {
                String dateKey = sdf.format(order.getCreatedAt());
                revenueTrends.put(dateKey,
                        revenueTrends.getOrDefault(dateKey, BigDecimal.ZERO).add(order.getTotalAmount()));
            }
        }

        // Transform to List of Objects for frontend {label, amount}
        List<java.util.Map<String, Object>> revenueData = new ArrayList<>();
        for (java.util.Map.Entry<String, BigDecimal> entry : revenueTrends.entrySet()) {
            java.util.Map<String, Object> dataPoint = new java.util.HashMap<>();
            dataPoint.put("label", entry.getKey());
            dataPoint.put("amount", entry.getValue());
            revenueData.add(dataPoint);
        }
        stats.put("revenueData", revenueData);

        // Recent Activity (Last 5 orders)
        List<Order> recentActivity = filteredOrders.stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .limit(5)
                .collect(java.util.stream.Collectors.toList());
        stats.put("recentOrders", recentActivity);

        return stats;
    }

    private List<Order> filterOrdersByPeriod(List<Order> orders, String period) {
        if (period == null || period.equalsIgnoreCase("all")) {
            return orders;
        }

        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        Date startDate;

        switch (period.toLowerCase()) {
            case "daily":
                // Today
                startDate = cal.getTime();
                break;
            case "weekly":
                // Last 7 days
                cal.add(java.util.Calendar.DAY_OF_YEAR, -7);
                startDate = cal.getTime();
                break;
            case "monthly":
                // Last 30 days
                cal.add(java.util.Calendar.DAY_OF_YEAR, -30);
                startDate = cal.getTime();
                break;
            case "yearly":
                // Last 365 days
                cal.add(java.util.Calendar.DAY_OF_YEAR, -365);
                startDate = cal.getTime();
                break;
            default:
                return orders;
        }

        return orders.stream()
                .filter(o -> o.getCreatedAt() != null && !o.getCreatedAt().before(startDate))
                .collect(java.util.stream.Collectors.toList());
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

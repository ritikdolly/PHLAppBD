package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.*;
import com.plh.foodappbackend.repository.OrderRepository;
import com.plh.foodappbackend.request.OrderRequest;
import com.plh.foodappbackend.request.BuyNowRequest;
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

        // Validate required fields
        if (req.getShippingAddress() == null) {
            throw new Exception("Shipping address is required");
        }

        if (req.getPaymentMethod() == null) {
            throw new Exception("Payment method is required");
        }

        Address shippingAddress = req.getShippingAddress();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalItemPrice;
        int totalItemCount;

        // Check if this is a buy-now order or cart-based order
        if (req.getBuyNowItem() != null) {
            // Buy Now Flow: Create order from single item
            OrderRequest.BuyNowItem buyNowItem = req.getBuyNowItem();

            Food food = foodRepository.findById(buyNowItem.getFoodId())
                    .orElseThrow(() -> new Exception("Food not found"));

            if (!food.isAvailability()) {
                throw new Exception("Food is currently unavailable");
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setFoodId(food.getId());
            orderItem.setFoodName(food.getName());
            orderItem.setFoodImage(food.getImageUrl());
            orderItem.setQuantity(buyNowItem.getQuantity());

            // Use discounted price logic
            BigDecimal price = food.getDiscountedPrice();
            orderItem.setPrice(price);
            orderItem.setTotalPrice(price.multiply(BigDecimal.valueOf(buyNowItem.getQuantity())));

            orderItems.add(orderItem);
            totalItemPrice = orderItem.getTotalPrice();
            totalItemCount = buyNowItem.getQuantity();

        } else {
            // Cart Checkout Flow: Create order from cart
            Cart cart = cartService.getCart(user);

            if (cart.getItems().isEmpty()) {
                throw new Exception("Cart is empty");
            }

            for (CartItem item : cart.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setFoodId(item.getFoodId());
                orderItem.setFoodName(item.getName());
                orderItem.setFoodImage(item.getImageUrl());
                orderItem.setQuantity(item.getQuantity());
                orderItem.setTotalPrice(item.getPrice());

                // Derive unit price safely
                if (item.getQuantity() > 0) {
                    orderItem.setPrice(
                            item.getPrice().divide(BigDecimal.valueOf(item.getQuantity()),
                                    java.math.RoundingMode.HALF_UP));
                } else {
                    orderItem.setPrice(BigDecimal.ZERO);
                }

                orderItems.add(orderItem);
            }

            totalItemPrice = cart.getTotalItemPrice();
            totalItemCount = cart.getItems().size();
        }

        // Calculate delivery fee
        BigDecimal deliveryFee = BigDecimal.ZERO;
        if (totalItemPrice.compareTo(BigDecimal.valueOf(300)) < 0) {
            deliveryFee = BigDecimal.valueOf(40);
        }

        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal totalAmount = totalItemPrice.add(deliveryFee).add(tax);

        // Create Order
        Order order = new Order();
        order.setUserId(user.getId());
        order.setUserName(user.getName());
        order.setItems(orderItems);
        order.setTotalItemPrice(totalItemPrice);
        order.setDeliveryFee(deliveryFee);
        order.setTax(tax);
        order.setTotalAmount(totalAmount);
        order.setShippingAddress(shippingAddress);
        order.setTotalItem(totalItemCount);
        order.setCreatedAt(new Date());

        // Set Payment Details and Status
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

        // Clear cart only if this was a cart-based checkout
        if (req.getBuyNowItem() == null) {
            cartService.clearCart(user);
        }

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
                orderStatus.equals("PREPARING") ||
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

    // ==================== Delivery Management Methods ====================

    @Autowired
    private com.plh.foodappbackend.service.DeliveryManService deliveryManService;

    @Override
    public Order assignDeliveryMan(String orderId, String deliveryManId, User admin) throws Exception {
        // Verify admin role
        if (admin.getRole() != USER_ROLE.ROLE_ADMIN) {
            throw new Exception("Only admins can assign delivery men");
        }

        // Find order
        Order order = findOrderById(orderId);

        // Validate order can be assigned
        if (order.getStatus() != ORDER_STATUS.PENDING &&
                order.getStatus() != ORDER_STATUS.CONFIRMED &&
                order.getStatus() != ORDER_STATUS.PREPARING) {
            throw new Exception("Order cannot be assigned in current status: " + order.getStatus());
        }

        // Check if already assigned
        if (order.getDeliveryManId() != null) {
            throw new Exception("Order already assigned to a delivery man");
        }

        // Get delivery man and validate availability
        com.plh.foodappbackend.model.DeliveryMan deliveryMan = deliveryManService.getDeliveryManById(deliveryManId);

        if (deliveryMan.getAvailabilityStatus() != AVAILABILITY_STATUS.AVAILABLE) {
            throw new Exception(
                    "Delivery man is not available. Current status: " + deliveryMan.getAvailabilityStatus());
        }

        // Atomic update: Assign delivery man to order
        order.setDeliveryManId(deliveryMan.getId());
        order.setDeliveryManName(deliveryMan.getName());
        order.setDeliveryManMobile(deliveryMan.getMobileNumber());
        order.setStatus(ORDER_STATUS.OUT_FOR_DELIVERY);

        // Save order first
        Order updatedOrder = orderRepository.save(order);

        // Update delivery man status to BUSY
        deliveryManService.updateAvailabilityStatus(deliveryManId, AVAILABILITY_STATUS.BUSY);

        return updatedOrder;
    }

    @Override
    public Order markAsDelivered(String orderId, User deliveryMan) throws Exception {
        // Verify delivery man role
        if (deliveryMan.getRole() != USER_ROLE.ROLE_DELIVERY) {
            throw new Exception("Only delivery personnel can mark orders as delivered");
        }

        // Find order
        Order order = findOrderById(orderId);

        // Validate this delivery man is assigned to this order
        if (order.getDeliveryManId() == null) {
            throw new Exception("No delivery man assigned to this order");
        }

        if (!order.getDeliveryManId().equals(deliveryMan.getId())) {
            throw new Exception("This order is not assigned to you");
        }

        // Validate order status
        if (order.getStatus() != ORDER_STATUS.OUT_FOR_DELIVERY) {
            throw new Exception("Order cannot be marked as delivered in current status: " + order.getStatus());
        }

        // Update order status
        order.setStatus(ORDER_STATUS.DELIVERED);
        order.setDeliveredAt(new Date());

        // Save order
        Order updatedOrder = orderRepository.save(order);

        // Update delivery man status back to AVAILABLE
        deliveryManService.updateAvailabilityStatus(order.getDeliveryManId(), AVAILABILITY_STATUS.AVAILABLE);

        return updatedOrder;
    }

    @Override
    public List<Order> getDeliveryManOrders(String deliveryManId) {
        return orderRepository.findByDeliveryManId(deliveryManId);
    }
}

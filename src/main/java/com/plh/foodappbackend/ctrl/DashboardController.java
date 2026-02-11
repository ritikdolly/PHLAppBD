package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.repository.FoodRepository;
import com.plh.foodappbackend.repository.OrderRepository;
import com.plh.foodappbackend.response.DashboardStats;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@AllArgsConstructor
public class DashboardController {

    private final OrderRepository orderRepository;
    private final FoodRepository foodRepository;

    @GetMapping("/stats")
    public DashboardStats getDashboardStats() {
        List<Order> orders = orderRepository.findAll();
        long totalOrders = orders.size();
        long foodItems = foodRepository.count();
        long activeOffers = 5; // Placeholder for now

        double revenue = orders.stream()
                .filter(o -> o.getStatus() == com.plh.foodappbackend.model.ORDER_STATUS.DELIVERED ||
                        o.getStatus() == com.plh.foodappbackend.model.ORDER_STATUS.COMPLETED ||
                        o.getStatus() == com.plh.foodappbackend.model.ORDER_STATUS.CONFIRMED) // Assuming confirmed also
                                                                                              // counts for now
                .map(order -> order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        // Recent Orders (Top 5)
        List<Order> recentOrders = orders.stream()
                .sorted((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()))
                .limit(5)
                .toList();

        // Revenue Data (Last 7 days simplified logic)
        // In a real app, use DB aggregation. Here doing in-memory for simplicity as per
        // previous patterns.
        java.util.Map<String, Double> dailyRevenue = new java.util.LinkedHashMap<>();
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("EEE"); // Mon, Tue...

        java.time.LocalDate today = java.time.LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate date = today.minusDays(i);
            dailyRevenue.put(date.format(dtf), 0.0);
        }

        for (Order order : orders) {
            if ((order.getStatus() == com.plh.foodappbackend.model.ORDER_STATUS.DELIVERED ||
                    order.getStatus() == com.plh.foodappbackend.model.ORDER_STATUS.COMPLETED ||
                    order.getStatus() == com.plh.foodappbackend.model.ORDER_STATUS.CONFIRMED)
                    && order.getCreatedAt() != null) {

                java.time.LocalDate orderDate = order.getCreatedAt().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDate();
                String label = orderDate.format(dtf);

                if (dailyRevenue.containsKey(label)) { // Only count if within last 7 days window (roughly, simplified
                                                       // by day name collision issues if not careful, but okay for
                                                       // demo)
                    // Actually, map by date first to be accurate then convert to label
                }
            }
        }

        // Better Approach for Chart Data:
        // Create list of last 7 days.
        List<com.plh.foodappbackend.response.RevenueItem> revenueData = new java.util.ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            java.time.LocalDate d = today.minusDays(i);
            String label = d.format(dtf);

            double dailySum = orders.stream()
                    .filter(o -> (o.getStatus() == com.plh.foodappbackend.model.ORDER_STATUS.DELIVERED ||
                            o.getStatus() == com.plh.foodappbackend.model.ORDER_STATUS.COMPLETED ||
                            o.getStatus() == com.plh.foodappbackend.model.ORDER_STATUS.CONFIRMED))
                    .filter(o -> o.getCreatedAt() != null)
                    .filter(o -> o.getCreatedAt().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                            .equals(d))
                    .map(o -> o.getTotalAmount() != null ? o.getTotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .doubleValue();

            revenueData.add(new com.plh.foodappbackend.response.RevenueItem(label, dailySum));
        }

        return new DashboardStats(totalOrders, foodItems, activeOffers, revenue, revenueData, recentOrders);
    }
}

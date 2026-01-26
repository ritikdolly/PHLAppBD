package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.repository.FoodRepository;
import com.plh.foodappbackend.repository.OrderRepository;
import com.plh.foodappbackend.response.DashboardStats;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                .mapToDouble(Order::getTotal)
                .sum();

        return new DashboardStats(totalOrders, foodItems, activeOffers, revenue);
    }
}

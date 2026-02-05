package com.plh.foodappbackend.serviceImpl;

import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.repository.OrderRepository;
import com.plh.foodappbackend.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(Order order) {
        if (order.getDate() == null || order.getDate().isEmpty()) {
            order.setDate(LocalDate.now().toString()); // Set default date to today (YYYY-MM-DD)
        }
        // Ensure total is calculated or set frontend side. For now, trust the payload.
        if (order.getStatus() == null) {
            order.setStatus("Processing");
        }
        return orderRepository.save(order);
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order getOrderById(String id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    public Order updateStatus(String id, String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    @Override
    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUserId(userId);
    }
}

package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(Order order);

    List<Order> getAllOrders();

    Order getOrderById(String id);

    Order updateStatus(String id, String status);
}

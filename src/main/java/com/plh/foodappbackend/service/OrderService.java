package com.plh.foodappbackend.service;

import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.request.OrderRequest;

import java.util.List;
import java.util.Map;

public interface OrderService {
    Order createOrder(OrderRequest req, User user) throws Exception;

    Order updateOrder(String orderId, String orderStatus) throws Exception;

    void cancelOrder(String orderId) throws Exception;

    List<Order> getUsersOrder(String userId) throws Exception; // all orders for a user

    List<Order> getAllOrders(); // admin

    Map<String, Object> getDashboardStats(String period);

    Order findOrderById(String orderId) throws Exception;
}

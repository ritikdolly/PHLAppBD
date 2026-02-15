package com.plh.foodappbackend.controller;

import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.service.OrderService;
import com.plh.foodappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAssignedOrders(
            @RequestHeader("Authorization") String jwt) throws Exception {
        User deliveryMan = userService.findUserByJwtToken(jwt);
        List<Order> orders = orderService.getDeliveryManOrders(deliveryMan.getId());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/orders/{id}/deliver")
    public ResponseEntity<Order> markAsDelivered(
            @PathVariable String id,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User deliveryMan = userService.findUserByJwtToken(jwt);
        Order order = orderService.markAsDelivered(id, deliveryMan);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}

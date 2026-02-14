package com.plh.foodappbackend.controller;

import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.model.User;
import com.plh.foodappbackend.request.OrderRequest;
import com.plh.foodappbackend.request.BuyNowRequest;
import com.plh.foodappbackend.service.OrderService;
import com.plh.foodappbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostMapping("/order")
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.createOrder(req, user);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PostMapping("/order/buy-now")
    public ResponseEntity<Order> buyNow(@RequestBody BuyNowRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.createBuyNowOrder(req, user);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @PutMapping("/order/{id}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable String id,
            @RequestBody OrderRequest req,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.confirmOrder(id, req, user);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable String id,
            @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        Order order = orderService.findOrderById(id);
        if (!order.getUserId().equals(user.getId())) {
            throw new Exception("Access Denied");
        }
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/order/user")
    public ResponseEntity<List<Order>> getOrderHistory(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        List<Order> orders = orderService.getUsersOrder(user.getId());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PutMapping("/admin/order/{id}/{orderStatus}")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable String id,
            @PathVariable String orderStatus,
            @RequestHeader("Authorization") String jwt) throws Exception {
        Order order = orderService.updateOrder(id, orderStatus);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}

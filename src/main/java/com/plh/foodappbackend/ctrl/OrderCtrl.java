package com.plh.foodappbackend.ctrl;

import com.plh.foodappbackend.model.Order;
import com.plh.foodappbackend.service.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderCtrl {

    private final OrderService orderService;

    @PostMapping("/add")
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }
}

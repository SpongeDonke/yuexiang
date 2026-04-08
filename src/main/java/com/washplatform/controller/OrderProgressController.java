package com.washplatform.controller;

import com.washplatform.entity.OrderProgress;
import com.washplatform.service.OrderProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-progresses")
public class OrderProgressController {

    @Autowired
    private OrderProgressService orderProgressService;

    @GetMapping("/order/{orderId}")
    public List<OrderProgress> findByOrderId(@PathVariable Integer orderId) {
        return orderProgressService.findByOrderId(orderId);
    }

    @PostMapping
    public OrderProgress save(@RequestBody OrderProgress orderProgress) {
        return orderProgressService.save(orderProgress);
    }
}
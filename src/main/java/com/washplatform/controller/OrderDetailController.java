package com.washplatform.controller;

import com.washplatform.entity.OrderDetail;
import com.washplatform.service.OrderDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailController {

    @Autowired
    private OrderDetailService orderDetailService;

    @GetMapping
    public List<OrderDetail> findAll() {
        return orderDetailService.findAll();
    }

    @GetMapping("/order/{orderId}")
    public List<OrderDetail> findByOrderId(@PathVariable Integer orderId) {
        return orderDetailService.findByOrderId(orderId);
    }

    @GetMapping("/{id}")
    public OrderDetail findById(@PathVariable Integer id) {
        return orderDetailService.findById(id);
    }

    @PostMapping
    public OrderDetail save(@RequestBody OrderDetail orderDetail) {
        return orderDetailService.save(orderDetail);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        orderDetailService.deleteById(id);
    }

    @PutMapping("/{id}")
    public OrderDetail update(@PathVariable Integer id, @RequestBody OrderDetail orderDetail) {
        orderDetail.setId(id);
        return orderDetailService.save(orderDetail);
    }
}

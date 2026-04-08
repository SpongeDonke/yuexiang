package com.washplatform.controller;

import com.washplatform.entity.Order;
import com.washplatform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @GetMapping("/customer/{customerId}")
    public List<Order> findByCustomerId(@PathVariable Integer customerId) {
        return orderService.findByCustomerId(customerId);
    }

    @GetMapping("/shop/{shopId}")
    public List<Order> findByShopId(@PathVariable Integer shopId) {
        return orderService.findByShopId(shopId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Integer id) {
        try {
            Order order = orderService.findById(id);
            if (order != null) {
                return ResponseEntity.ok(order);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
            }
        } catch (Exception e) {
            System.err.println("Error finding order: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error finding order: " + e.getMessage());
        }
    }

    @PostMapping
    public Order save(@RequestBody Order order) {
        return orderService.save(order);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        orderService.deleteById(id);
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable Integer id, @RequestBody Order order) {
        order.setId(id);
        return orderService.save(order);
    }

    @GetMapping("/customer/{customerId}/search")
    public List<Order> searchCustomerOrders(
            @PathVariable Integer customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String phone) {
        return orderService.searchCustomerOrders(customerId, status, phone);
    }

    @GetMapping("/shop/{shopId}/search")
    public List<Order> searchShopOrders(
            @PathVariable Integer shopId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String name) {
        return orderService.searchShopOrders(shopId, status, name);
    }
}
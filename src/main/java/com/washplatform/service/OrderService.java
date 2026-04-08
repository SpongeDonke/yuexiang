package com.washplatform.service;

import com.washplatform.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> findAll();
    List<Order> findByCustomerId(Integer customerId);
    List<Order> findByShopId(Integer shopId);
    List<Order> searchCustomerOrders(Integer customerId, String status, String phone);
    List<Order> searchShopOrders(Integer shopId, String status, String name);
    Order findById(Integer id);
    Order save(Order order);
    void deleteById(Integer id);
}
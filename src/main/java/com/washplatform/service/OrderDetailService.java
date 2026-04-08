package com.washplatform.service;

import com.washplatform.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> findAll();
    List<OrderDetail> findByOrderId(Integer orderId);
    OrderDetail findById(Integer id);
    OrderDetail save(OrderDetail orderDetail);
    void deleteById(Integer id);
}

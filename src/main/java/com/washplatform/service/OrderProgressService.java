package com.washplatform.service;

import com.washplatform.entity.OrderProgress;

import java.util.List;

public interface OrderProgressService {
    List<OrderProgress> findByOrderId(Integer orderId);
    OrderProgress save(OrderProgress orderProgress);
}
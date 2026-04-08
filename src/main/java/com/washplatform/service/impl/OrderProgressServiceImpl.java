package com.washplatform.service.impl;

import com.washplatform.entity.OrderProgress;
import com.washplatform.repository.OrderProgressRepository;
import com.washplatform.service.OrderProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;

@Service
public class OrderProgressServiceImpl implements OrderProgressService {

    @Autowired
    private OrderProgressRepository orderProgressRepository;

    @Override
    public List<OrderProgress> findByOrderId(Integer orderId) {
        return orderProgressRepository.findByOrderId(orderId);
    }

    @Override
    public OrderProgress save(OrderProgress orderProgress) {
        if (orderProgress.getCreatedAt() == null) {
            orderProgress.setCreatedAt(new Date());
        }
        return orderProgressRepository.save(orderProgress);
    }
}
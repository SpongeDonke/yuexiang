package com.washplatform.repository;

import com.washplatform.entity.OrderProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProgressRepository extends JpaRepository<OrderProgress, Integer> {
    List<OrderProgress> findByOrderId(Integer orderId);
}
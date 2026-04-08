package com.washplatform.repository;

import com.washplatform.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    @EntityGraph(attributePaths = {"shop", "orderDetails", "orderDetails.service"})
    List<Order> findByCustomerId(Integer customerId);
    
    @EntityGraph(attributePaths = {"shop", "orderDetails", "orderDetails.service"})
    List<Order> findByShopId(Integer shopId);
    
    @EntityGraph(attributePaths = {"shop", "orderDetails", "orderDetails.service"})
    List<Order> findByCustomerIdAndStatusContaining(Integer customerId, String status);
    
    @EntityGraph(attributePaths = {"shop", "orderDetails", "orderDetails.service"})
    List<Order> findByCustomerIdAndContactPhoneContaining(Integer customerId, String phone);
    
    @EntityGraph(attributePaths = {"shop", "orderDetails", "orderDetails.service"})
    List<Order> findByShopIdAndStatusContaining(Integer shopId, String status);
    
    @EntityGraph(attributePaths = {"shop", "orderDetails", "orderDetails.service"})
    List<Order> findByShopIdAndContactNameContaining(Integer shopId, String name);
    
    @EntityGraph(attributePaths = {"shop", "orderDetails", "orderDetails.service"})
    Optional<Order> findById(Integer id);
    
}

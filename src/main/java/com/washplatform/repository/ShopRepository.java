package com.washplatform.repository;

import com.washplatform.entity.Shop;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Integer> {
    @EntityGraph(attributePaths = {"user"})
    List<Shop> findAll();
    
    @EntityGraph(attributePaths = {"user"})
    List<Shop> findByUserId(Integer userId);
}
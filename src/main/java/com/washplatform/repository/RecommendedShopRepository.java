package com.washplatform.repository;

import com.washplatform.entity.RecommendedShop;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedShopRepository extends JpaRepository<RecommendedShop, Integer> {
    @EntityGraph(attributePaths = {"shop"})
    List<RecommendedShop> findByStatusOrderBySortAsc(Integer status);
}
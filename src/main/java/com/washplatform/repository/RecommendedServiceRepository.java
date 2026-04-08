package com.washplatform.repository;

import com.washplatform.entity.RecommendedService;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendedServiceRepository extends JpaRepository<RecommendedService, Integer> {
    @EntityGraph(attributePaths = {"service"})
    List<RecommendedService> findByStatusOrderBySortAsc(Integer status);
}
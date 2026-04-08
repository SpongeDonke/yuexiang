package com.washplatform.repository;

import com.washplatform.entity.WashService;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WashServiceRepository extends JpaRepository<WashService, Integer> {
    @EntityGraph(attributePaths = {"shop", "shop.user", "type"})
    List<WashService> findAll();
    
    @EntityGraph(attributePaths = {"shop", "shop.user", "type"})
    List<WashService> findByShopId(Integer shopId);
    
    @EntityGraph(attributePaths = {"shop", "shop.user", "type"})
    List<WashService> findByTypeId(Integer typeId);
    
    @EntityGraph(attributePaths = {"shop", "shop.user", "type"})
    Optional<WashService> findById(Integer id);
}
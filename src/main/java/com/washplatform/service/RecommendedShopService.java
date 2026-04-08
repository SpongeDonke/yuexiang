package com.washplatform.service;

import com.washplatform.entity.RecommendedShop;

import java.util.List;

public interface RecommendedShopService {
    List<RecommendedShop> findAll();
    List<RecommendedShop> findByStatus(Integer status);
    RecommendedShop findById(Integer id);
    RecommendedShop save(RecommendedShop recommendedShop);
    void deleteById(Integer id);
}
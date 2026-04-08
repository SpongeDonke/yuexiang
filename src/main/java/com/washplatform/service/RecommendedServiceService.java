package com.washplatform.service;

import com.washplatform.entity.RecommendedService;

import java.util.List;

public interface RecommendedServiceService {
    List<RecommendedService> findAll();
    List<RecommendedService> findByStatus(Integer status);
    RecommendedService findById(Integer id);
    RecommendedService save(RecommendedService recommendedService);
    void deleteById(Integer id);
}
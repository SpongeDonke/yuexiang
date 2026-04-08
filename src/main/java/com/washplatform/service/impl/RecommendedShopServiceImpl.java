package com.washplatform.service.impl;

import com.washplatform.entity.RecommendedShop;
import com.washplatform.repository.RecommendedShopRepository;
import com.washplatform.service.RecommendedShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RecommendedShopServiceImpl implements RecommendedShopService {
    @Autowired
    private RecommendedShopRepository recommendedShopRepository;

    @Override
    public List<RecommendedShop> findAll() {
        return recommendedShopRepository.findAll();
    }

    @Override
    public List<RecommendedShop> findByStatus(Integer status) {
        return recommendedShopRepository.findByStatusOrderBySortAsc(status);
    }

    @Override
    public RecommendedShop findById(Integer id) {
        return recommendedShopRepository.findById(id).orElse(null);
    }

    @Override
    public RecommendedShop save(RecommendedShop recommendedShop) {
        if (recommendedShop.getId() == null) {
            recommendedShop.setCreatedAt(new Date());
        }
        recommendedShop.setUpdatedAt(new Date());
        return recommendedShopRepository.save(recommendedShop);
    }

    @Override
    public void deleteById(Integer id) {
        recommendedShopRepository.deleteById(id);
    }
}
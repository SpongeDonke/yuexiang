package com.washplatform.service.impl;

import com.washplatform.entity.RecommendedService;
import com.washplatform.entity.Shop;
import com.washplatform.entity.WashService;
import com.washplatform.repository.RecommendedServiceRepository;
import com.washplatform.repository.ShopRepository;
import com.washplatform.service.RecommendedServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class RecommendedServiceServiceImpl implements RecommendedServiceService {
    @Autowired
    private RecommendedServiceRepository recommendedServiceRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Override
    public List<RecommendedService> findAll() {
        List<RecommendedService> recommendedServices = recommendedServiceRepository.findAll();
        setShopNames(recommendedServices);
        return recommendedServices;
    }

    @Override
    public List<RecommendedService> findByStatus(Integer status) {
        List<RecommendedService> recommendedServices = recommendedServiceRepository.findByStatusOrderBySortAsc(status);
        setShopNames(recommendedServices);
        return recommendedServices;
    }

    @Override
    public RecommendedService findById(Integer id) {
        RecommendedService recommendedService = recommendedServiceRepository.findById(id).orElse(null);
        if (recommendedService != null && recommendedService.getService() != null) {
            setShopName(recommendedService.getService());
        }
        return recommendedService;
    }

    @Override
    public RecommendedService save(RecommendedService recommendedService) {
        if (recommendedService.getId() == null) {
            recommendedService.setCreatedAt(new Date());
        }
        recommendedService.setUpdatedAt(new Date());
        RecommendedService saved = recommendedServiceRepository.save(recommendedService);
        if (saved.getService() != null) {
            setShopName(saved.getService());
        }
        return saved;
    }

    @Override
    public void deleteById(Integer id) {
        recommendedServiceRepository.deleteById(id);
    }

    private void setShopNames(List<RecommendedService> recommendedServices) {
        for (RecommendedService recommendedService : recommendedServices) {
            if (recommendedService.getService() != null) {
                setShopName(recommendedService.getService());
            }
        }
    }

    private void setShopName(WashService washService) {
        if (washService.getShopId() != null) {
            Shop shop = shopRepository.findById(washService.getShopId()).orElse(null);
            if (shop != null) {
                washService.setShopName(shop.getName());
            }
        }
    }
}
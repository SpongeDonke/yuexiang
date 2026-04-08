package com.washplatform.service;

import com.washplatform.entity.Shop;

import java.util.List;

public interface ShopService {
    Shop findById(Integer id);
    List<Shop> findByUserId(Integer userId);
    List<Shop> findAll();
    Shop save(Shop shop);
    void deleteById(Integer id);
    Shop getOrCreateShop(Integer userId, String userName);
}
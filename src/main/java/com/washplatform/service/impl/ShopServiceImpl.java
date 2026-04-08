package com.washplatform.service.impl;

import com.washplatform.entity.Shop;
import com.washplatform.repository.ShopRepository;
import com.washplatform.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopRepository shopRepository;

    @Override
    public Shop findById(Integer id) {
        return shopRepository.findById(id).orElse(null);
    }

    @Override
    public List<Shop> findByUserId(Integer userId) {
        return shopRepository.findByUserId(userId);
    }

    @Override
    public List<Shop> findAll() {
        return shopRepository.findAll();
    }

    @Override
    public Shop save(Shop shop) {
        return shopRepository.save(shop);
    }

    @Override
    public void deleteById(Integer id) {
        shopRepository.deleteById(id);
    }

    @Override
    public Shop getOrCreateShop(Integer userId, String userName) {
        List<Shop> shops = shopRepository.findByUserId(userId);
        if (shops != null && !shops.isEmpty()) {
            return shops.get(0);
        }
        Shop shop = new Shop();
        shop.setUserId(userId);
        shop.setName(userName != null ? userName + "的洗护店" : "我的洗护店");
        shop.setAddress("待填写地址");
        shop.setPhone("待填写电话");
        shop.setDescription("暂无描述");
        shop.setCreatedAt(new Date());
        return shopRepository.save(shop);
    }
}
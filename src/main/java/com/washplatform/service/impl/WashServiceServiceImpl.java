package com.washplatform.service.impl;

import com.washplatform.entity.Shop;
import com.washplatform.entity.WashService;
import com.washplatform.entity.WashType;
import com.washplatform.repository.ShopRepository;
import com.washplatform.repository.WashServiceRepository;
import com.washplatform.repository.WashTypeRepository;
import com.washplatform.service.WashServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Date;

@Service
public class WashServiceServiceImpl implements WashServiceService {

    @Autowired
    private WashServiceRepository washServiceRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private WashTypeRepository washTypeRepository;

    @Override
    public List<WashService> findAll() {
        List<WashService> washServices = washServiceRepository.findAll();
        setShopNamesAndTypeNames(washServices);
        return washServices;
    }

    @Override
    public List<WashService> findByShopId(Integer shopId) {
        List<WashService> washServices = washServiceRepository.findByShopId(shopId);
        setShopNamesAndTypeNames(washServices);
        return washServices;
    }

    @Override
    public List<WashService> findByTypeId(Integer typeId) {
        List<WashService> washServices = washServiceRepository.findByTypeId(typeId);
        setShopNamesAndTypeNames(washServices);
        return washServices;
    }

    @Override
    public WashService findById(Integer id) {
        WashService washService = washServiceRepository.findById(id).orElse(null);
        if (washService != null) {
            setShopNameAndTypeName(washService);
        }
        return washService;
    }

    @Override
    public WashService save(WashService washService) {
        if (washService.getCreatedAt() == null) {
            washService.setCreatedAt(new Date());
        }
        WashService saved = washServiceRepository.save(washService);
        setShopNameAndTypeName(saved);
        return saved;
    }

    @Override
    public void deleteById(Integer id) {
        washServiceRepository.deleteById(id);
    }

    private void setShopNamesAndTypeNames(List<WashService> washServices) {
        for (WashService washService : washServices) {
            setShopNameAndTypeName(washService);
        }
    }

    private void setShopNameAndTypeName(WashService washService) {
        // 设置店铺名称和店铺对象
        if (washService.getShopId() != null) {
            Shop shop = shopRepository.findById(washService.getShopId()).orElse(null);
            if (shop != null) {
                washService.setShopName(shop.getName());
                washService.setShop(shop);
            }
        }

        // 设置类型名称和类型对象
        if (washService.getTypeId() != null) {
            WashType washType = washTypeRepository.findById(washService.getTypeId()).orElse(null);
            if (washType != null) {
                washService.setTypeName(washType.getName());
                washService.setType(washType);
            }
        }
    }
}

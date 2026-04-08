package com.washplatform.service;

import com.washplatform.entity.WashService;

import java.util.List;

public interface WashServiceService {
    List<WashService> findAll();
    List<WashService> findByShopId(Integer shopId);
    List<WashService> findByTypeId(Integer typeId);
    WashService findById(Integer id);
    WashService save(WashService washService);
    void deleteById(Integer id);
}
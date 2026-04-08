package com.washplatform.service;

import com.washplatform.entity.WashType;

import java.util.List;

public interface WashTypeService {
    List<WashType> findAll();
    WashType findById(Integer id);
    WashType save(WashType washType);
    void deleteById(Integer id);
}
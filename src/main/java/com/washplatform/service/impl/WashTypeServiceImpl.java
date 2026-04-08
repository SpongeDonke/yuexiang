package com.washplatform.service.impl;

import com.washplatform.entity.WashType;
import com.washplatform.repository.WashTypeRepository;
import com.washplatform.service.WashTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WashTypeServiceImpl implements WashTypeService {

    @Autowired
    private WashTypeRepository washTypeRepository;

    @Override
    public List<WashType> findAll() {
        return washTypeRepository.findAll();
    }

    @Override
    public WashType findById(Integer id) {
        return washTypeRepository.findById(id).orElse(null);
    }

    @Override
    public WashType save(WashType washType) {
        return washTypeRepository.save(washType);
    }

    @Override
    public void deleteById(Integer id) {
        washTypeRepository.deleteById(id);
    }
}
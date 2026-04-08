package com.washplatform.controller;

import com.washplatform.entity.WashService;
import com.washplatform.service.WashServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wash-services")
public class WashServiceController {

    @Autowired
    private WashServiceService washServiceService;

    @GetMapping
    public List<WashService> findAll() {
        return washServiceService.findAll();
    }

    @GetMapping("/shop/{shopId}")
    public List<WashService> findByShopId(@PathVariable Integer shopId) {
        return washServiceService.findByShopId(shopId);
    }

    @GetMapping("/type/{typeId}")
    public List<WashService> findByTypeId(@PathVariable Integer typeId) {
        return washServiceService.findByTypeId(typeId);
    }

    @GetMapping("/{id}")
    public WashService findById(@PathVariable Integer id) {
        return washServiceService.findById(id);
    }

    @PostMapping
    public WashService save(@RequestBody WashService washService) {
        return washServiceService.save(washService);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        washServiceService.deleteById(id);
    }
}
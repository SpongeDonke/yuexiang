package com.washplatform.controller;

import com.washplatform.entity.RecommendedShop;
import com.washplatform.service.RecommendedShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommended-shops")
public class RecommendedShopController {

    @Autowired
    private RecommendedShopService recommendedShopService;

    @GetMapping
    public List<RecommendedShop> findAll() {
        return recommendedShopService.findAll();
    }

    @GetMapping("/status/{status}")
    public List<RecommendedShop> findByStatus(@PathVariable Integer status) {
        return recommendedShopService.findByStatus(status);
    }

    @GetMapping("/{id}")
    public RecommendedShop findById(@PathVariable Integer id) {
        return recommendedShopService.findById(id);
    }

    @PostMapping
    public RecommendedShop save(@RequestBody RecommendedShop recommendedShop) {
        return recommendedShopService.save(recommendedShop);
    }

    @PutMapping("/{id}")
    public RecommendedShop update(@PathVariable Integer id, @RequestBody RecommendedShop recommendedShop) {
        recommendedShop.setId(id);
        return recommendedShopService.save(recommendedShop);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        recommendedShopService.deleteById(id);
    }
}
package com.washplatform.controller;

import com.washplatform.entity.RecommendedService;
import com.washplatform.service.RecommendedServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommended-services")
public class RecommendedServiceController {

    @Autowired
    private RecommendedServiceService recommendedServiceService;

    @GetMapping
    public List<RecommendedService> findAll() {
        return recommendedServiceService.findAll();
    }

    @GetMapping("/status/{status}")
    public List<RecommendedService> findByStatus(@PathVariable Integer status) {
        return recommendedServiceService.findByStatus(status);
    }

    @GetMapping("/{id}")
    public RecommendedService findById(@PathVariable Integer id) {
        return recommendedServiceService.findById(id);
    }

    @PostMapping
    public RecommendedService save(@RequestBody RecommendedService recommendedService) {
        return recommendedServiceService.save(recommendedService);
    }

    @PutMapping("/{id}")
    public RecommendedService update(@PathVariable Integer id, @RequestBody RecommendedService recommendedService) {
        recommendedService.setId(id);
        return recommendedServiceService.save(recommendedService);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        recommendedServiceService.deleteById(id);
    }
}
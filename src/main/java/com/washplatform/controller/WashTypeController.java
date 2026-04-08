package com.washplatform.controller;

import com.washplatform.entity.WashType;
import com.washplatform.service.WashTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wash-types")
public class WashTypeController {

    @Autowired
    private WashTypeService washTypeService;

    @GetMapping
    public List<WashType> findAll() {
        return washTypeService.findAll();
    }

    @GetMapping("/{id}")
    public WashType findById(@PathVariable Integer id) {
        return washTypeService.findById(id);
    }

    @PostMapping
    public WashType save(@RequestBody WashType washType) {
        return washTypeService.save(washType);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        washTypeService.deleteById(id);
    }
}
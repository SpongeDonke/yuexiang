package com.washplatform.controller;

import com.washplatform.entity.Shop;
import com.washplatform.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shops")
public class ShopController {

    @Autowired
    private ShopService shopService;

    @GetMapping("/{id}")
    public Shop findById(@PathVariable Integer id) {
        return shopService.findById(id);
    }

    @GetMapping("/user/{userId}")
    public List<Shop> findByUserId(@PathVariable Integer userId) {
        return shopService.findByUserId(userId);
    }

    @GetMapping
    public List<Shop> findAll() {
        return shopService.findAll();
    }
    
    @GetMapping("/test")
    public String test() {
        return "Hello, World!";
    }

    @PostMapping("/get-or-create")
    public Shop getOrCreateShop(@RequestBody Map<String, Object> request) {
        Integer userId = (Integer) request.get("userId");
        String userName = (String) request.get("userName");
        return shopService.getOrCreateShop(userId, userName);
    }

    @PostMapping
    public Shop save(@RequestBody Shop shop) {
        return shopService.save(shop);
    }

    @PutMapping("/{id}")
    public Shop update(@PathVariable Integer id, @RequestBody Shop shop) {
        shop.setId(id);
        return shopService.save(shop);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        shopService.deleteById(id);
    }
}
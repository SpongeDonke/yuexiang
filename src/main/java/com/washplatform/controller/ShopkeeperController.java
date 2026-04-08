package com.washplatform.controller;

import com.washplatform.entity.User;
import com.washplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shopkeepers")
public class ShopkeeperController {

    @Autowired
    private UserService userService;

    // 获取所有商家账号
    @GetMapping
    public List<User> getShopkeepers() {
        List<User> allUsers = userService.findAll();
        return allUsers.stream()
                .filter(user -> user.getRoleId().equals(1))
                .toList();
    }

    // 更新商家账号状态
    @PutMapping("/{id}/status")
    public Map<String, Object> updateShopkeeperStatus(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        User user = userService.findById(id);
        Map<String, Object> response = new HashMap<>();

        if (user != null && user.getRoleId().equals(1)) {
            user.setStatus(status);
            User updatedUser = userService.save(user);
            if (updatedUser != null) {
                response.put("success", true);
                response.put("message", "商家账号状态更新成功");
            } else {
                response.put("success", false);
                response.put("message", "商家账号状态更新失败");
            }
        } else {
            response.put("success", false);
            response.put("message", "商家账号不存在");
        }

        return response;
    }
}

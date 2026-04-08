package com.washplatform.controller;

import com.washplatform.entity.User;
import com.washplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Date;

@RestController
@RequestMapping("/api")
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/check-username")
    public Map<String, Object> checkUsername(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        
        if (username == null || username.trim().isEmpty()) {
            response.put("exists", false);
            return response;
        }
        
        User existingUser = userService.findByUsername(username.trim());
        response.put("exists", existingUser != null);
        
        return response;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        Integer roleId = (Integer) request.get("roleId");
        String name = (String) request.get("name");
        String phone = (String) request.get("phone");

        Map<String, Object> response = new HashMap<>();

        // 检查用户名是否已存在
        User existingUser = userService.findByUsername(username);
        if (existingUser != null) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return response;
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoleId(roleId);
        user.setName(name);
        user.setPhone(phone);
        user.setCreatedAt(new Date());

        // 为商家设置初始状态为"pending"
        if (roleId.equals(1)) {
            user.setStatus("pending");
        } else {
            user.setStatus("active");
        }

        User savedUser = userService.save(user);

        if (savedUser != null) {
            response.put("success", true);
            if (roleId.equals(1)) {
                response.put("message", "请等待管理员审核");
            } else {
                response.put("message", "注册成功");
            }
        } else {
            response.put("success", false);
            response.put("message", "注册失败，请稍后重试");
        }

        return response;
    }
}

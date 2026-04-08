package com.washplatform.controller;

import com.washplatform.entity.User;
import com.washplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping
    public Map<String, Object> login(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        Integer roleId = (Integer) request.get("roleId");
        Map<String, Object> response = new HashMap<>();
        
        // 首先检查用户是否存在
        User user = userService.findByUsername(username);
        if (user == null) {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return response;
        }
        
        // 检查密码是否正确
        if (!user.getPassword().equals(password)) {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return response;
        }
        
        // 检查角色是否匹配
        if (!user.getRoleId().equals(roleId)) {
            response.put("success", false);
            response.put("message", "用户名、密码或角色错误");
            return response;
        }
        
        // 检查商家账号状态
        if (user.getRoleId().equals(1) && !"active".equals(user.getStatus())) {
            response.put("success", false);
            response.put("message", "当前未通过审核");
            return response;
        }
        
        // 登录成功
        response.put("success", true);
        response.put("user", user);
        return response;
    }
}
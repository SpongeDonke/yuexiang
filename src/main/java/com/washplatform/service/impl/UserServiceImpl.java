package com.washplatform.service.impl;

import com.washplatform.entity.User;
import com.washplatform.repository.UserRepository;
import com.washplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User login(String username, String password, Integer roleId) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(password) && user.getRoleId().equals(roleId)) {
            // 检查商家账号状态
            if (user.getRoleId().equals(1) && !"active".equals(user.getStatus())) {
                return null;
            }
            return user;
        }
        return null;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Integer id) {
        userRepository.deleteById(id);
    }
}
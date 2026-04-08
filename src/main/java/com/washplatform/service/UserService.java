package com.washplatform.service;

import com.washplatform.entity.User;

import java.util.List;

public interface UserService {
    User login(String username, String password, Integer roleId);
    User findById(Integer id);
    User findByUsername(String username);
    List<User> findAll();
    User save(User user);
    void deleteById(Integer id);
}
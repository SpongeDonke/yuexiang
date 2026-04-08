package com.washplatform.service;

import com.washplatform.entity.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> findAll();
    List<Comment> findByOrderId(Integer orderId);
    List<Comment> findByUserId(Integer userId);
    Comment save(Comment comment);
    void deleteById(Integer id);
    Comment reply(Integer id, String reply);

}
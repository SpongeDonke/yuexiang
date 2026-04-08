package com.washplatform.repository;

import com.washplatform.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findByOrderId(Integer orderId);
    List<Comment> findByUserId(Integer userId);
}
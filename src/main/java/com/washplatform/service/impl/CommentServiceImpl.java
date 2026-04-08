package com.washplatform.service.impl;

import com.washplatform.entity.Comment;
import com.washplatform.entity.Order;
import com.washplatform.repository.CommentRepository;
import com.washplatform.service.CommentService;
import com.washplatform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private OrderService orderService;

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findByOrderId(Integer orderId) {
        return commentRepository.findByOrderId(orderId);
    }

    @Override
    public List<Comment> findByUserId(Integer userId) {
        return commentRepository.findByUserId(userId);
    }

    @Override
    public Comment save(Comment comment) {
        // 检查订单状态是否为已完成
        Order order = orderService.findById(comment.getOrderId());
        if (order == null || (!"completed".equals(order.getStatus()) && !"已完成".equals(order.getStatus()))) {
            throw new IllegalArgumentException("只能对已完成的订单进行评论");
        }
        // 设置评论创建时间
        if (comment.getCreatedAt() == null) {
            comment.setCreatedAt(new Date());
        }
        return commentRepository.save(comment);
    }

    @Override
    public void deleteById(Integer id) {
        commentRepository.deleteById(id);
    }

    @Override
    public Comment reply(Integer id, String reply) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("评论不存在"));
        comment.setReply(reply);
        comment.setReplyTime(new Date());
        return commentRepository.save(comment);
    }

}
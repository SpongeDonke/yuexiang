package com.washplatform.controller;

import com.washplatform.entity.Comment;
import com.washplatform.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            return ResponseEntity.ok(commentService.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取评论失败");
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> findByOrderId(@PathVariable Integer orderId) {
        try {
            return ResponseEntity.ok(commentService.findByOrderId(orderId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取评论失败");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findByUserId(@PathVariable Integer userId) {
        try {
            System.out.println("User ID: " + userId);
            List<Comment> comments = commentService.findByUserId(userId);
            System.out.println("Comments: " + comments);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取评论失败");
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Comment comment) {
        try {
            System.out.println("Comment: " + comment);
            Comment savedComment = commentService.save(comment);
            System.out.println("Saved comment: " + savedComment);
            return ResponseEntity.ok(savedComment);
        } catch (IllegalArgumentException e) {
            System.out.println("IllegalArgumentException: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("添加评论失败");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        try {
            commentService.deleteById(id);
            return ResponseEntity.ok("删除评论成功");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除评论失败");
        }
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<?> reply(@PathVariable Integer id, @RequestBody Comment comment) {
        try {
            System.out.println("Comment ID: " + id);
            System.out.println("Reply: " + comment.getReply());
            Comment updatedComment = commentService.reply(id, comment.getReply());
            System.out.println("Updated comment: " + updatedComment);
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("回复评论失败");
        }
    }

}
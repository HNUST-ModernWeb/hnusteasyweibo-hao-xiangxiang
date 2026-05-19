package com.community.backend.controller;

import com.community.backend.entity.Post;
import com.community.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @GetMapping
    public List<Post> getPosts() {
        return postService.getAllPosts();
    }
    
    @PostMapping
    public Map<String, Object> createPost(@RequestBody Map<String, String> request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return response;
        }
        
        String content = request.get("content");
        String images = request.get("images");
        
        Post post = postService.createPost(content, userId, images);
        
        response.put("success", true);
        response.put("post", post);
        return response;
    }
    
    @PostMapping("/{id}/like")
    public Map<String, Object> likePost(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return response;
        }
        
        boolean success = postService.likePost(id, userId);
        response.put("success", success);
        response.put("message", success ? "点赞成功" : "已经点过赞了");
        return response;
    }
}
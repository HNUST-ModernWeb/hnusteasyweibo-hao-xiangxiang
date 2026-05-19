package com.community.backend.service;

import com.community.backend.entity.Post;
import com.community.backend.mapper.PostMapper;
import com.community.backend.mapper.LikeRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class PostService {
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    
    @Autowired
    private WebSocketService webSocketService;
    
    public List<Post> getAllPosts() {
        return postMapper.findAll();
    }
    
    @Transactional
    public Post createPost(String content, Long userId, String images) {
        Post post = new Post();
        post.setContent(content);
        post.setUserId(userId);
        post.setLikes(0);
        post.setImages(images);
        postMapper.insert(post);
        return post;
    }
    
    @Transactional
    public boolean likePost(Long postId, Long userId) {
        Long exists = likeRecordMapper.exists(userId, postId);
        if (exists != null) {
            return false;
        }
        
        likeRecordMapper.insert(userId, postId);
        postMapper.addLike(postId);
        
        Integer newLikeCount = postMapper.getLikes(postId);
        
        // 发送 WebSocket 实时通知
        webSocketService.sendLikeNotification(postId, userId, newLikeCount);
        
        return true;
    }
}
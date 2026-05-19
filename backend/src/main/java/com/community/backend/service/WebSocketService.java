package com.community.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public void sendLikeNotification(Long postId, Long userId, Integer newLikeCount) {
        String message = String.format("{\"postId\":%d,\"userId\":%d,\"likeCount\":%d}", 
            postId, userId, newLikeCount);
        messagingTemplate.convertAndSend("/topic/likes/" + postId, message);
    }
}
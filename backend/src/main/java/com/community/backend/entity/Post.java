package com.community.backend.entity;

import java.time.LocalDateTime;

public class Post {
    private Long id;
    private String content;
    private String images;
    private Integer likes;
    private Long userId;
    private LocalDateTime createdAt;
    
    // 非数据库字段
    private String username;
    private String nickname;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }
    
    public Integer getLikes() { return likes; }
    public void setLikes(Integer likes) { this.likes = likes; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
}
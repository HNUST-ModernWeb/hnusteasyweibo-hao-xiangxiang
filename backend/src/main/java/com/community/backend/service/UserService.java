package com.community.backend.service;

import com.community.backend.entity.User;
import com.community.backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    public User login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    public boolean register(String username, String password, String nickname) {
        User existUser = userMapper.findByUsername(username);
        if (existUser != null) {
            return false;
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setNickname(nickname != null ? nickname : username);
        
        userMapper.insert(user);
        return true;
    }
    
    public User getUserById(Long id) {
        return userMapper.findById(id);
    }
}
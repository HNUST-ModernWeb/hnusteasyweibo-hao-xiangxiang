package com.community.backend.controller;

import com.community.backend.entity.LoginRequest;
import com.community.backend.entity.RegisterRequest;
import com.community.backend.entity.User;
import com.community.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        boolean success = userService.register(
            request.getUsername(),
            request.getPassword(),
            request.getNickname()
        );
        
        response.put("success", success);
        response.put("message", success ? "注册成功" : "用户名已存在");
        return response;
    }
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User user = userService.login(request.getUsername(), request.getPassword());
        
        if (user != null) {
            session.setAttribute("userId", user.getId());
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname()
            ));
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
        }
        return response;
    }
    
    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }
    
    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId != null) {
            User user = userService.getUserById(userId);
            response.put("success", true);
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "nickname", user.getNickname()
            ));
        } else {
            response.put("success", false);
        }
        return response;
    }
}
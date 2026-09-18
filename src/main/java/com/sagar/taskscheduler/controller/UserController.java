package com.sagar.taskscheduler.controller;

import com.sagar.taskscheduler.dto.AuthRequest;
import com.sagar.taskscheduler.model.User;
import com.sagar.taskscheduler.service.UserService;
import com.sagar.taskscheduler.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public User register(@RequestBody AuthRequest request) {
        return userService.registerUser(request.getUsername(), request.getPassword());
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthRequest request) {
        User user = userService.validateUser(request.getUsername(), request.getPassword());
        String token = jwtUtil.generateToken(user.getUsername());
        return Map.of("token", token);
    }
}
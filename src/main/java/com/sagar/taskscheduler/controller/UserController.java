package com.sagar.taskscheduler.controller;

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
    public User register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        return userService.registerUser(username, password);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        User user = userService.validateUser(username, password);
        String token = jwtUtil.generateToken(user.getUsername());

        return Map.of("token", token);
    }
}
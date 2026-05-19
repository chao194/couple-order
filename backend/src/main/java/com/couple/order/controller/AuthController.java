package com.couple.order.controller;

import com.couple.order.entity.ApiResponse;
import com.couple.order.entity.User;
import com.couple.order.service.UserService;
import com.couple.order.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");

        if (username == null || password == null) {
            return ApiResponse.error("用户名和密码不能为空");
        }

        User user = userService.getUserByUsername(username);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }

        if (!userService.verifyPassword(password, user.getPassword())) {
            return ApiResponse.error("密码错误");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", buildUserInfo(user));

        return ApiResponse.ok(data);
    }

    @PostMapping("/register")
    public ApiResponse<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String nickname = body.get("nickname");

        if (username == null || password == null) {
            return ApiResponse.error("用户名和密码不能为空");
        }

        if (username.length() < 3 || username.length() > 20) {
            return ApiResponse.error("用户名长度需要在3-20个字符之间");
        }

        if (password.length() < 6) {
            return ApiResponse.error("密码长度不能少于6位");
        }

        User existing = userService.getUserByUsername(username);
        if (existing != null) {
            return ApiResponse.error("用户名已存在");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setNickname(nickname != null ? nickname : username);
        newUser.setRole("USER");

        User created = userService.createUser(newUser);

        String token = jwtUtil.generateToken(created.getId(), created.getUsername(), created.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", buildUserInfo(created));

        return ApiResponse.ok(data);
    }

    @GetMapping("/me")
    public ApiResponse<Map<String, Object>> getCurrentUser(@RequestAttribute Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        return ApiResponse.ok(buildUserInfo(user));
    }

    private Map<String, Object> buildUserInfo(User user) {
        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("nickname", user.getNickname());
        info.put("role", user.getRole());
        info.put("createdAt", user.getCreatedAt());
        return info;
    }
}

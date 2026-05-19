package com.couple.order.controller;

import com.couple.order.entity.ApiResponse;
import com.couple.order.entity.User;
import com.couple.order.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ApiResponse<List<User>> getAllUsers(@RequestAttribute String role) {
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error("无权限访问");
        }
        List<User> users = userService.getAllUsers();
        users.forEach(u -> u.setPassword(null));
        return ApiResponse.ok(users);
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUserById(@PathVariable Long id, @RequestAttribute String role) {
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error("无权限访问");
        }
        User user = userService.getUserById(id);
        if (user == null) return ApiResponse.error("用户不存在");
        user.setPassword(null);
        return ApiResponse.ok(user);
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user,
                                         @RequestAttribute String role) {
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error("无权限访问");
        }
        User updated = userService.updateUser(id, user);
        updated.setPassword(null);
        return ApiResponse.ok(updated);
    }

    @PutMapping("/{id}/password")
    public ApiResponse<Void> updatePassword(@PathVariable Long id, @RequestBody Map<String, String> body,
                                             @RequestAttribute String role) {
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error("无权限访问");
        }
        String newPassword = body.get("password");
        if (newPassword == null || newPassword.length() < 6) {
            return ApiResponse.error("密码长度不能少于6位");
        }
        userService.updatePassword(id, newPassword);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id, @RequestAttribute String role) {
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error("无权限访问");
        }
        userService.deleteUser(id);
        return ApiResponse.ok();
    }
}

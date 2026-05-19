package com.couple.order.service;

import com.couple.order.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserService {

    @Autowired
    private D1DatabaseService d1;

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<Map<String, Object>> rows = d1.query(sql);
        return rows.stream().map(this::mapToUser).toList();
    }

    public User getUserById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<Map<String, Object>> rows = d1.query(sql, id);
        if (rows.isEmpty()) return null;
        return mapToUser(rows.get(0));
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<Map<String, Object>> rows = d1.query(sql, username);
        if (rows.isEmpty()) return null;
        return mapToUser(rows.get(0));
    }

    public User createUser(User user) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String sql = "INSERT INTO users (username, password, nickname, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        d1.execute(sql, user.getUsername(), hashPassword(user.getPassword()),
                user.getNickname(), user.getRole() != null ? user.getRole() : "USER", now, now);

        List<Map<String, Object>> rows = d1.query("SELECT last_insert_rowid() as id");
        Long userId = ((Number) rows.get(0).get("id")).longValue();
        return getUserById(userId);
    }

    public User updateUser(Long id, User user) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String sql = "UPDATE users SET nickname = ?, role = ?, updated_at = ? WHERE id = ?";
        d1.execute(sql, user.getNickname(), user.getRole(), now, id);
        return getUserById(id);
    }

    public void updatePassword(Long id, String newPassword) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String sql = "UPDATE users SET password = ?, updated_at = ? WHERE id = ?";
        d1.execute(sql, hashPassword(newPassword), now, id);
    }

    public void deleteUser(Long id) {
        d1.execute("DELETE FROM users WHERE id = ?", id);
    }

    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    private User mapToUser(Map<String, Object> row) {
        User user = new User();
        user.setId(((Number) row.get("id")).longValue());
        user.setUsername((String) row.get("username"));
        user.setPassword((String) row.get("password"));
        user.setNickname((String) row.get("nickname"));
        user.setRole((String) row.get("role"));
        user.setCreatedAt((String) row.get("created_at"));
        user.setUpdatedAt((String) row.get("updated_at"));
        return user;
    }
}

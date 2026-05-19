package com.couple.order.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String role;
    private String createdAt;
    private String updatedAt;
}

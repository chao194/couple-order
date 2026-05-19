package com.couple.order.entity;

import lombok.Data;

@Data
public class MenuItem {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String category;
    private String imageUrl;
    private Boolean available;
    private String createdAt;
    private String updatedAt;
}

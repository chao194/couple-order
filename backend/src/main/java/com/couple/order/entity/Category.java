package com.couple.order.entity;

import lombok.Data;

@Data
public class Category {
    private Long id;
    private String name;
    private String icon;
    private Integer sortOrder;
    private String createdAt;
    private String updatedAt;
}

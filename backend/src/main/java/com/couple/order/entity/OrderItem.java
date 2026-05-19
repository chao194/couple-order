package com.couple.order.entity;

import lombok.Data;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long menuItemId;
    private String menuItemName;
    private Double price;
    private Integer quantity;
    private Double subtotal;
}

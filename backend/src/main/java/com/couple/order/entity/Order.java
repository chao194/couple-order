package com.couple.order.entity;

import lombok.Data;
import java.util.List;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private Long userId;
    private String customerName;
    private String status;
    private Double totalAmount;
    private String remark;
    private String createdAt;
    private String updatedAt;
    private List<OrderItem> items;
}

package com.couple.order.controller;

import com.couple.order.entity.ApiResponse;
import com.couple.order.entity.Order;
import com.couple.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ApiResponse<List<Order>> getAllOrders(@RequestAttribute Long userId,
                                                  @RequestAttribute String role) {
        if ("ADMIN".equals(role)) {
            return ApiResponse.ok(orderService.getAllOrders());
        }
        return ApiResponse.ok(orderService.getOrdersByUserId(userId));
    }

    @GetMapping("/{id}")
    public ApiResponse<Order> getById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null) return ApiResponse.error("订单不存在");
        return ApiResponse.ok(order);
    }

    @GetMapping("/no/{orderNo}")
    public ApiResponse<Order> getByOrderNo(@PathVariable String orderNo) {
        Order order = orderService.getOrderByNo(orderNo);
        if (order == null) return ApiResponse.error("订单不存在");
        return ApiResponse.ok(order);
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<Order>> getByStatus(@PathVariable String status) {
        return ApiResponse.ok(orderService.getOrdersByStatus(status));
    }

    @PostMapping
    public ApiResponse<Order> create(@RequestBody Order order, @RequestAttribute Long userId) {
        order.setUserId(userId);
        return ApiResponse.ok(orderService.createOrder(order));
    }

    @PutMapping("/{id}/status")
    public ApiResponse<Order> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ApiResponse.ok(orderService.updateOrderStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ApiResponse.ok();
    }
}

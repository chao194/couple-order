package com.couple.order.controller;

import com.couple.order.entity.ApiResponse;
import com.couple.order.entity.Order;
import com.couple.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ApiResponse<List<Order>> getAllOrders(@RequestAttribute(required = false) Long userId,
                                                  @RequestAttribute(required = false) String role) {
        if ("ADMIN".equals(role)) {
            return ApiResponse.ok(orderService.getAllOrders());
        }
        if (userId != null) {
            return ApiResponse.ok(orderService.getOrdersByUserId(userId));
        }
        return ApiResponse.ok(Collections.emptyList());
    }

    @GetMapping("/page")
    public ApiResponse<Map<String, Object>> getOrdersPaged(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestAttribute(required = false) Long userId,
            @RequestAttribute(required = false) String role) {
        List<Order> orders;
        int total;
        if ("ADMIN".equals(role)) {
            total = orderService.getOrderCount();
            orders = orderService.getOrdersPaged(page, size);
        } else if (userId != null) {
            total = orderService.getOrderCountByUserId(userId);
            orders = orderService.getOrdersByUserIdPaged(userId, page, size);
        } else {
            total = 0;
            orders = Collections.emptyList();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", orders);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);
        return ApiResponse.ok(result);
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
    public ApiResponse<Order> create(@RequestBody Order order, @RequestAttribute(required = false) Long userId) {
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

package com.couple.order.service;

import com.couple.order.entity.Order;
import com.couple.order.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    @Autowired
    private D1DatabaseService d1;

    public List<Order> getAllOrders() {
        String sql = "SELECT * FROM orders ORDER BY created_at DESC";
        List<Map<String, Object>> rows = d1.query(sql);
        return rows.stream().map(this::mapToOrder).toList();
    }

    public Order getOrderById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        List<Map<String, Object>> rows = d1.query(sql, id);
        if (rows.isEmpty()) return null;

        Order order = mapToOrder(rows.get(0));
        order.setItems(getOrderItems(id));
        return order;
    }

    public Order getOrderByNo(String orderNo) {
        String sql = "SELECT * FROM orders WHERE order_no = ?";
        List<Map<String, Object>> rows = d1.query(sql, orderNo);
        if (rows.isEmpty()) return null;

        Order order = mapToOrder(rows.get(0));
        order.setItems(getOrderItems(order.getId()));
        return order;
    }

    public Order createOrder(Order order) {
        String orderNo = generateOrderNo();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Calculate total
        double total = 0;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                total += item.getPrice() * item.getQuantity();
            }
        }

        String sql = "INSERT INTO orders (order_no, user_id, customer_name, status, total_amount, remark, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Long orderId = d1.executeInsert(sql, orderNo, order.getUserId(), order.getCustomerName(), "pending", total,
                order.getRemark(), now, now);

        // Insert order items
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                double subtotal = item.getPrice() * item.getQuantity();
                String itemSql = "INSERT INTO order_items (order_id, menu_item_id, menu_item_name, price, quantity, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
                d1.execute(itemSql, orderId, item.getMenuItemId(), item.getMenuItemName(),
                        item.getPrice(), item.getQuantity(), subtotal);
            }
        }

        return getOrderById(orderId);
    }

    public Order updateOrderStatus(Long id, String status) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String sql = "UPDATE orders SET status = ?, updated_at = ? WHERE id = ?";
        d1.execute(sql, status, now, id);
        return getOrderById(id);
    }

    public void deleteOrder(Long id) {
        d1.execute("DELETE FROM order_items WHERE order_id = ?", id);
        d1.execute("DELETE FROM orders WHERE id = ?", id);
    }

    public List<Order> getOrdersByStatus(String status) {
        String sql = "SELECT * FROM orders WHERE status = ? ORDER BY created_at DESC";
        List<Map<String, Object>> rows = d1.query(sql, status);
        return rows.stream().map(this::mapToOrder).toList();
    }

    public List<Order> getOrdersByUserId(Long userId) {
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY created_at DESC";
        List<Map<String, Object>> rows = d1.query(sql, userId);
        return rows.stream().map(this::mapToOrder).toList();
    }

    private List<OrderItem> getOrderItems(Long orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        List<Map<String, Object>> rows = d1.query(sql, orderId);
        return rows.stream().map(this::mapToOrderItem).toList();
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis();
    }

    private Order mapToOrder(Map<String, Object> row) {
        Order order = new Order();
        order.setId(((Number) row.get("id")).longValue());
        order.setOrderNo((String) row.get("order_no"));
        order.setUserId(row.get("user_id") != null ? ((Number) row.get("user_id")).longValue() : null);
        order.setCustomerName((String) row.get("customer_name"));
        order.setStatus((String) row.get("status"));
        order.setTotalAmount(toDouble(row.get("total_amount")));
        order.setRemark((String) row.get("remark"));
        order.setCreatedAt((String) row.get("created_at"));
        order.setUpdatedAt((String) row.get("updated_at"));
        return order;
    }

    private OrderItem mapToOrderItem(Map<String, Object> row) {
        OrderItem item = new OrderItem();
        item.setId(((Number) row.get("id")).longValue());
        item.setOrderId(((Number) row.get("order_id")).longValue());
        item.setMenuItemId(((Number) row.get("menu_item_id")).longValue());
        item.setMenuItemName((String) row.get("menu_item_name"));
        item.setPrice(toDouble(row.get("price")));
        item.setQuantity(((Number) row.get("quantity")).intValue());
        item.setSubtotal(toDouble(row.get("subtotal")));
        return item;
    }

    private Double toDouble(Object val) {
        if (val == null) return 0.0;
        return ((Number) val).doubleValue();
    }
}

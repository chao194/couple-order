package com.couple.order.service;

import com.couple.order.entity.MenuItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MenuService {

    @Autowired
    private D1DatabaseService d1;

    public List<MenuItem> getAllMenuItems() {
        String sql = "SELECT * FROM menu_items WHERE available = 1 ORDER BY category, id";
        List<Map<String, Object>> rows = d1.query(sql);
        return rows.stream().map(this::mapToMenuItem).toList();
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        String sql = "SELECT * FROM menu_items WHERE category = ? AND available = 1 ORDER BY id";
        List<Map<String, Object>> rows = d1.query(sql, category);
        return rows.stream().map(this::mapToMenuItem).toList();
    }

    public MenuItem getMenuItemById(Long id) {
        String sql = "SELECT * FROM menu_items WHERE id = ?";
        List<Map<String, Object>> rows = d1.query(sql, id);
        if (rows.isEmpty()) return null;
        return mapToMenuItem(rows.get(0));
    }

    public MenuItem createMenuItem(MenuItem item) {
        String sql = "INSERT INTO menu_items (name, description, price, category, image_url, available) VALUES (?, ?, ?, ?, ?, ?)";
        Long newId = d1.executeInsert(sql, item.getName(), item.getDescription(), item.getPrice(),
                item.getCategory(), item.getImageUrl(), item.getAvailable() != null ? item.getAvailable() : true);
        return getMenuItemById(newId);
    }

    public MenuItem updateMenuItem(Long id, MenuItem item) {
        String sql = "UPDATE menu_items SET name = ?, description = ?, price = ?, category = ?, image_url = ?, available = ? WHERE id = ?";
        d1.execute(sql, item.getName(), item.getDescription(), item.getPrice(),
                item.getCategory(), item.getImageUrl(), item.getAvailable(), id);
        return getMenuItemById(id);
    }

    public void deleteMenuItem(Long id) {
        d1.execute("DELETE FROM menu_items WHERE id = ?", id);
    }

    private MenuItem mapToMenuItem(Map<String, Object> row) {
        MenuItem item = new MenuItem();
        item.setId(((Number) row.get("id")).longValue());
        item.setName((String) row.get("name"));
        item.setDescription((String) row.get("description"));
        item.setPrice(toDouble(row.get("price")));
        item.setCategory((String) row.get("category"));
        item.setImageUrl((String) row.get("image_url"));
        item.setAvailable(toBool(row.get("available")));
        item.setCreatedAt((String) row.get("created_at"));
        item.setUpdatedAt((String) row.get("updated_at"));
        return item;
    }

    private Double toDouble(Object val) {
        if (val == null) return 0.0;
        return ((Number) val).doubleValue();
    }

    private Boolean toBool(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean) val;
        return ((Number) val).intValue() == 1;
    }
}

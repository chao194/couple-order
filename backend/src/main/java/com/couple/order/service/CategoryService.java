package com.couple.order.service;

import com.couple.order.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CategoryService {

    @Autowired
    private D1DatabaseService d1;

    public List<Category> getAllCategories() {
        String sql = "SELECT * FROM category ORDER BY sort_order, id";
        List<Map<String, Object>> rows = d1.query(sql);
        return rows.stream().map(this::mapToCategory).toList();
    }

    public Category getCategoryById(Long id) {
        String sql = "SELECT * FROM category WHERE id = ?";
        List<Map<String, Object>> rows = d1.query(sql, id);
        if (rows.isEmpty()) return null;
        return mapToCategory(rows.get(0));
    }

    public Category createCategory(Category category) {
        String sql = "INSERT INTO category (name, icon, sort_order) VALUES (?, ?, ?)";
        d1.execute(sql, category.getName(), category.getIcon(), category.getSortOrder() != null ? category.getSortOrder() : 0);

        List<Map<String, Object>> rows = d1.query("SELECT last_insert_rowid() as id");
        Long newId = ((Number) rows.get(0).get("id")).longValue();
        return getCategoryById(newId);
    }

    public Category updateCategory(Long id, Category category) {
        String sql = "UPDATE category SET name = ?, icon = ?, sort_order = ? WHERE id = ?";
        d1.execute(sql, category.getName(), category.getIcon(), category.getSortOrder(), id);
        return getCategoryById(id);
    }

    public void deleteCategory(Long id) {
        d1.execute("DELETE FROM category WHERE id = ?", id);
    }

    private Category mapToCategory(Map<String, Object> row) {
        Category category = new Category();
        category.setId(((Number) row.get("id")).longValue());
        category.setName((String) row.get("name"));
        category.setIcon((String) row.get("icon"));
        category.setSortOrder(((Number) row.get("sort_order")).intValue());
        category.setCreatedAt((String) row.get("created_at"));
        category.setUpdatedAt((String) row.get("updated_at"));
        return category;
    }
}

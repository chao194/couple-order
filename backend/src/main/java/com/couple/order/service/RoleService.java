package com.couple.order.service;

import com.couple.order.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RoleService {

    @Autowired
    private D1DatabaseService d1;

    public List<Role> getAllRoles() {
        String sql = "SELECT * FROM roles ORDER BY id ASC";
        List<Map<String, Object>> rows = d1.query(sql);
        return rows.stream().map(this::mapToRole).toList();
    }

    public Role getRoleById(Long id) {
        String sql = "SELECT * FROM roles WHERE id = ?";
        List<Map<String, Object>> rows = d1.query(sql, id);
        if (rows.isEmpty()) return null;
        return mapToRole(rows.get(0));
    }

    public Role getRoleByName(String name) {
        String sql = "SELECT * FROM roles WHERE name = ?";
        List<Map<String, Object>> rows = d1.query(sql, name);
        if (rows.isEmpty()) return null;
        return mapToRole(rows.get(0));
    }

    public Role createRole(Role role) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String sql = "INSERT INTO roles (name, description, created_at) VALUES (?, ?, ?)";
        d1.execute(sql, role.getName(), role.getDescription(), now);

        List<Map<String, Object>> rows = d1.query("SELECT last_insert_rowid() as id");
        Long roleId = ((Number) rows.get(0).get("id")).longValue();
        return getRoleById(roleId);
    }

    public Role updateRole(Long id, Role role) {
        String sql = "UPDATE roles SET name = ?, description = ? WHERE id = ?";
        d1.execute(sql, role.getName(), role.getDescription(), id);
        return getRoleById(id);
    }

    public void deleteRole(Long id) {
        d1.execute("DELETE FROM roles WHERE id = ?", id);
    }

    private Role mapToRole(Map<String, Object> row) {
        Role role = new Role();
        role.setId(((Number) row.get("id")).longValue());
        role.setName((String) row.get("name"));
        role.setDescription((String) row.get("description"));
        role.setCreatedAt((String) row.get("created_at"));
        return role;
    }
}

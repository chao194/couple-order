package com.couple.order.controller;

import com.couple.order.entity.ApiResponse;
import com.couple.order.entity.Role;
import com.couple.order.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public ApiResponse<List<Role>> getAllRoles(@RequestAttribute String role) {
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error("无权限访问");
        }
        return ApiResponse.ok(roleService.getAllRoles());
    }

    @GetMapping("/{id}")
    public ApiResponse<Role> getRoleById(@PathVariable Long id, @RequestAttribute String role) {
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error("无权限访问");
        }
        Role r = roleService.getRoleById(id);
        if (r == null) return ApiResponse.error("角色不存在");
        return ApiResponse.ok(r);
    }

    @PostMapping
    public ApiResponse<Role> createRole(@RequestBody Role role, @RequestAttribute String requestRole) {
        if (!"ADMIN".equals(requestRole)) {
            return ApiResponse.error("无权限访问");
        }
        if (role.getName() == null || role.getName().isEmpty()) {
            return ApiResponse.error("角色名称不能为空");
        }
        Role existing = roleService.getRoleByName(role.getName());
        if (existing != null) {
            return ApiResponse.error("角色名称已存在");
        }
        return ApiResponse.ok(roleService.createRole(role));
    }

    @PutMapping("/{id}")
    public ApiResponse<Role> updateRole(@PathVariable Long id, @RequestBody Role role,
                                         @RequestAttribute String requestRole) {
        if (!"ADMIN".equals(requestRole)) {
            return ApiResponse.error("无权限访问");
        }
        return ApiResponse.ok(roleService.updateRole(id, role));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id, @RequestAttribute String role) {
        if (!"ADMIN".equals(role)) {
            return ApiResponse.error("无权限访问");
        }
        roleService.deleteRole(id);
        return ApiResponse.ok();
    }
}

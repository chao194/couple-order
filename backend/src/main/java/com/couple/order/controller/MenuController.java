package com.couple.order.controller;

import com.couple.order.entity.ApiResponse;
import com.couple.order.entity.MenuItem;
import com.couple.order.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping
    public ApiResponse<List<MenuItem>> getAllMenuItems() {
        return ApiResponse.ok(menuService.getAllMenuItems());
    }

    @GetMapping("/category/{category}")
    public ApiResponse<List<MenuItem>> getByCategory(@PathVariable String category) {
        return ApiResponse.ok(menuService.getMenuItemsByCategory(category));
    }

    @GetMapping("/{id}")
    public ApiResponse<MenuItem> getById(@PathVariable Long id) {
        MenuItem item = menuService.getMenuItemById(id);
        if (item == null) return ApiResponse.error("菜品不存在");
        return ApiResponse.ok(item);
    }

    @PostMapping
    public ApiResponse<MenuItem> create(@RequestBody MenuItem item) {
        return ApiResponse.ok(menuService.createMenuItem(item));
    }

    @PutMapping("/{id}")
    public ApiResponse<MenuItem> update(@PathVariable Long id, @RequestBody MenuItem item) {
        return ApiResponse.ok(menuService.updateMenuItem(id, item));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ApiResponse.ok();
    }
}

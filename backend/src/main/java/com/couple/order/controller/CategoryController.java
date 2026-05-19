package com.couple.order.controller;

import com.couple.order.entity.ApiResponse;
import com.couple.order.entity.Category;
import com.couple.order.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> getAllCategories() {
        return ApiResponse.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ApiResponse<Category> getById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        if (category == null) return ApiResponse.error("分类不存在");
        return ApiResponse.ok(category);
    }

    @PostMapping
    public ApiResponse<Category> create(@RequestBody Category category) {
        return ApiResponse.ok(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> update(@PathVariable Long id, @RequestBody Category category) {
        return ApiResponse.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.ok();
    }
}

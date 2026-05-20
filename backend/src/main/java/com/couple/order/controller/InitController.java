package com.couple.order.controller;

import com.couple.order.entity.ApiResponse;
import com.couple.order.service.D1DatabaseService;
import com.couple.order.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/init")
public class InitController {

    @Autowired
    private D1DatabaseService d1;

    @Autowired
    private UserService userService;

    @PostMapping("/tables")
    public ApiResponse<String> initTables() {
        try {
            // Create roles table
            d1.execute("""
                CREATE TABLE IF NOT EXISTS roles (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    description TEXT,
                    created_at TEXT DEFAULT (datetime('now', 'localtime'))
                )
            """);

            // Create users table
            d1.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    nickname TEXT,
                    role TEXT DEFAULT 'USER',
                    created_at TEXT DEFAULT (datetime('now', 'localtime')),
                    updated_at TEXT DEFAULT (datetime('now', 'localtime'))
                )
            """);

            // Create category table
            d1.execute("""
                CREATE TABLE IF NOT EXISTS category (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    icon TEXT,
                    sort_order INTEGER DEFAULT 0,
                    created_at TEXT DEFAULT (datetime('now', 'localtime')),
                    updated_at TEXT DEFAULT (datetime('now', 'localtime'))
                )
            """);

            // Migration: add icon column if not exists (for existing databases)
            try {
                d1.execute("ALTER TABLE category ADD COLUMN icon TEXT");
            } catch (Exception ignored) {
                // Column already exists, ignore
            }

            // Create menu_items table
            d1.execute("""
                CREATE TABLE IF NOT EXISTS menu_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    description TEXT,
                    price REAL NOT NULL,
                    category TEXT,
                    image_url TEXT,
                    available INTEGER DEFAULT 1,
                    created_at TEXT DEFAULT (datetime('now', 'localtime')),
                    updated_at TEXT DEFAULT (datetime('now', 'localtime'))
                )
            """);

            // Create orders table
            d1.execute("""
                CREATE TABLE IF NOT EXISTS orders (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_no TEXT NOT NULL UNIQUE,
                    user_id INTEGER,
                    customer_name TEXT,
                    status TEXT DEFAULT 'pending',
                    total_amount REAL DEFAULT 0,
                    remark TEXT,
                    created_at TEXT DEFAULT (datetime('now', 'localtime')),
                    updated_at TEXT DEFAULT (datetime('now', 'localtime')),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // Migration: add user_id column if not exists (for existing databases)
            try {
                d1.execute("ALTER TABLE orders ADD COLUMN user_id INTEGER");
            } catch (Exception ignored) {
                // Column already exists, ignore
            }

            // Create order_items table
            d1.execute("""
                CREATE TABLE IF NOT EXISTS order_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    order_id INTEGER NOT NULL,
                    menu_item_id INTEGER NOT NULL,
                    menu_item_name TEXT,
                    price REAL NOT NULL,
                    quantity INTEGER DEFAULT 1,
                    subtotal REAL DEFAULT 0,
                    FOREIGN KEY (order_id) REFERENCES orders(id),
                    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)
                )
            """);

            return ApiResponse.ok("数据库表创建成功");
        } catch (Exception e) {
            return ApiResponse.error("创建表失败: " + e.getMessage());
        }
    }

    @PostMapping("/seed")
    public ApiResponse<String> seedData() {
        try {
            // Seed roles
            List<Map<String, Object>> existingRoles = d1.query("SELECT COUNT(*) as cnt FROM roles");
            if (((Number) existingRoles.get(0).get("cnt")).intValue() == 0) {
                d1.execute("INSERT INTO roles (name, description) VALUES (?, ?)", "ADMIN", "管理员，拥有所有权限");
                d1.execute("INSERT INTO roles (name, description) VALUES (?, ?)", "USER", "普通用户，只能查看自己的订单");
            }

            // Seed admin user
            List<Map<String, Object>> existingUsers = d1.query("SELECT COUNT(*) as cnt FROM users");
            if (((Number) existingUsers.get(0).get("cnt")).intValue() == 0) {
                String adminPwd = userService.hashPassword("admin123");
                d1.execute("INSERT INTO users (username, password, nickname, role) VALUES (?, ?, ?, ?)",
                        "admin", adminPwd, "管理员", "ADMIN");
            }

            // Seed categories
            List<Map<String, Object>> existingCategories = d1.query("SELECT COUNT(*) as cnt FROM category");
            if (((Number) existingCategories.get(0).get("cnt")).intValue() == 0) {
                String[][] cats = {
                    {"热菜", "🍳", "1"},
                    {"主食", "🍚", "2"},
                    {"汤类", "🥣", "3"},
                    {"饮品", "🥤", "4"}
                };
                for (String[] cat : cats) {
                    d1.execute("INSERT INTO category (name, icon, sort_order) VALUES (?, ?, ?)",
                            cat[0], cat[1], Integer.parseInt(cat[2]));
                }
            } else {
                // Update existing categories without icon
                String[][] defaultIcons = {
                    {"热菜", "🍳"},
                    {"主食", "🍚"},
                    {"汤类", "🥣"},
                    {"饮品", "🥤"},
                    {"甜点", "🍰"},
                    {"冷菜", "🥗"}
                };
                for (String[] cat : defaultIcons) {
                    d1.execute("UPDATE category SET icon = ? WHERE name = ? AND (icon IS NULL OR icon = '')",
                            cat[1], cat[0]);
                }
            }

            // Check if menu data already exists
            List<Map<String, Object>> existing = d1.query("SELECT COUNT(*) as cnt FROM menu_items");
            if (((Number) existing.get(0).get("cnt")).intValue() > 0) {
                return ApiResponse.ok("数据已存在，跳过菜品初始化");
            }

            // Insert sample menu items for a couple ordering system
            String[][] items = {
                    {"红烧肉", "经典家常菜，肥而不腻", "38.00", "热菜"},
                    {"番茄炒蛋", "营养美味的家常菜", "18.00", "热菜"},
                    {"糖醋排骨", "酸甜可口，外酥里嫩", "42.00", "热菜"},
                    {"清炒时蔬", "新鲜时令蔬菜", "16.00", "热菜"},
                    {"麻婆豆腐", "麻辣鲜香，下饭神器", "22.00", "热菜"},
                    {"可乐鸡翅", "甜蜜入味，人人爱吃", "28.00", "热菜"},
                    {"蛋炒饭", "粒粒分明，香气扑鼻", "15.00", "主食"},
                    {"阳春面", "简单清爽的传统面食", "12.00", "主食"},
                    {"酸辣汤", "开胃暖身", "15.00", "汤类"},
                    {"紫菜蛋花汤", "清淡鲜美", "10.00", "汤类"},
                    {"冰可乐", "冰爽解渴", "5.00", "饮品"},
                    {"柠檬水", "清新解腻", "8.00", "饮品"},
            };

            for (String[] item : items) {
                d1.execute("INSERT INTO menu_items (name, description, price, category) VALUES (?, ?, ?, ?)",
                        item[0], item[1], Double.parseDouble(item[2]), item[3]);
            }

            return ApiResponse.ok("示例数据初始化成功，共插入 " + items.length + " 条菜品");
        } catch (Exception e) {
            return ApiResponse.error("初始化数据失败: " + e.getMessage());
        }
    }
}

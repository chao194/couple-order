# 点餐系统

一个温馨的情侣点餐系统，前端使用 Vue 3 + Element Plus，后端使用 Java Spring Boot，数据库使用 Cloudflare D1。

## 技术栈

### 前端
- Vue 3 (Composition API)
- Element Plus UI 组件库
- Vue Router 路由管理
- Pinia 状态管理
- Axios HTTP 客户端
- Vite 构建工具

### 后端
- Java 17
- Spring Boot 3.2
- Cloudflare D1 数据库 (SQLite-compatible)

## 项目结构

```
couple-order/
├── frontend/              # 前端项目
│   ├── src/
│   │   ├── api/           # API 接口
│   │   ├── router/        # 路由配置
│   │   ├── stores/        # Pinia 状态管理
│   │   ├── views/         # 页面组件
│   │   ├── App.vue        # 根组件
│   │   └── main.js        # 入口文件
│   ├── package.json
│   └── vite.config.js
├── backend/               # 后端项目
│   ├── src/main/java/com/couple/order/
│   │   ├── config/        # 配置类
│   │   ├── controller/    # 控制器
│   │   ├── entity/        # 实体类
│   │   └── service/       # 服务层
│   └── pom.xml
└── README.md
```

## 快速开始

### 1. 初始化数据库

首次使用需要在管理后台初始化数据库：

1. 启动后端服务
2. 打开前端页面，进入「管理」页面
3. 点击「创建数据表」
4. 点击「插入示例数据」

### 2. 启动后端

```bash
cd backend

# 设置 Cloudflare API Token
export CLOUDFLARE_API_TOKEN=your-api-token

# 使用 Maven 启动
./mvnw spring-boot:run
```

后端将在 http://localhost:8080 启动

### 3. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端将在 http://localhost:5173 启动

## 功能特性

- **菜单浏览**：按分类查看菜品，支持筛选
- **购物车**：添加/删除菜品，调整数量
- **下单**：选择点餐人（布布/一一），添加备注
- **订单管理**：查看订单状态，更新订单状态
- **管理后台**：菜品增删改查，数据库初始化

## API 接口

### 菜单接口
- `GET /api/menu` - 获取所有菜品
- `GET /api/menu/category/{category}` - 按分类筛选
- `POST /api/menu` - 添加菜品
- `PUT /api/menu/{id}` - 更新菜品
- `DELETE /api/menu/{id}` - 删除菜品

### 订单接口
- `GET /api/orders` - 获取所有订单
- `GET /api/orders/{id}` - 获取订单详情
- `POST /api/orders` - 创建订单
- `PUT /api/orders/{id}/status?status=xxx` - 更新订单状态

### 初始化接口
- `POST /api/init/tables` - 创建数据库表
- `POST /api/init/seed` - 插入示例数据

## Cloudflare D1 数据库

本项目使用 Cloudflare D1 作为数据库，需要：

1. 在 Cloudflare Dashboard 创建 D1 数据库
2. 获取 Account ID 和 Database ID
3. 创建 API Token（需要 D1 编辑权限）
4. 配置到 `application.yml` 或环境变量

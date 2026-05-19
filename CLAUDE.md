# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Full-stack "couple ordering system" (情侣点餐系统) — a monorepo with a Java Spring Boot backend and Vue 3 frontend.

## Commands

### Frontend (pnpm, from `frontend/`)
- `pnpm dev` — start Vite dev server (port 5173, proxies `/api` to `localhost:8080`)
- `pnpm build` — production build
- `pnpm preview` — preview production build

### Backend (Maven, from `backend/`)
- `./mvnw spring-boot:run` — run Spring Boot server (port 8080)
- `./mvnw package` — build JAR
- No tests exist; `spring-boot-starter-test` is declared but unused

### Full Stack Dev
- Start backend first (`./mvnw spring-boot:run`), then frontend (`pnpm dev`)
- Vite proxies `/api` requests to `http://localhost:8080`

## Architecture

### Backend (`backend/src/main/java/com/couple/order/`)

Classic Spring Boot layered architecture:
- **controller/** — REST controllers, return `ApiResponse<T>` wrapper
- **service/** — business logic; all DB access through `D1DatabaseService`
- **entity/** — Lombok `@Data` POJOs
- **filter/** — `JwtAuthFilter` (servlet filter for JWT auth)
- **config/** — `AuthConfig` (registers JWT filter), `CorsConfig`
- **util/** — `JwtUtil` (JWT token generation/parsing)

Key design decision: **Cloudflare D1 as database** via REST API, not local SQLite. `D1DatabaseService` is the single database gateway — all services inject it and pass raw SQL with parameterized queries via `query()` (returns `List<Map>`) and `execute()` (returns affected rows). There is no JPA/Hibernate; everything is manual SQL.

Auth: `JwtAuthFilter` intercepts `/api/*`, validates Bearer tokens, sets `userId`/`username`/`role` as request attributes. Public paths include `/api/auth/login`, `/api/auth/register`, `/api/init/*`, `/api/menu/*`, `/api/categories/*`, `/api/files/*`.

`InitController` contains raw SQL DDL for table creation and seed data (roles, admin user, categories, sample menu items).

### Frontend (`frontend/src/`)

- **api/index.js** — single Axios instance with all API modules (`menuApi`, `orderApi`, `authApi`, etc.). Request interceptor adds Bearer token; response interceptor redirects to `/login` on 401.
- **stores/** — Pinia stores: `auth` (token/user, persisted to localStorage), `cart` (items/totals, in-memory only)
- **router/index.js** — lazy-loaded routes. Public: `/login`, `/menu`. Protected: `/cart`, `/orders`. Admin: `/admin/*` (requires ADMIN role).
- **views/** — Vue 3 components using Composition API (`<script setup>`)

UI library is Element Plus (Chinese locale). All `@element-plus/icons-vue` icons are globally registered in `main.js`.

### Database Tables

`roles`, `users`, `category`, `menu_items`, `orders`, `order_items`

### Configuration

- `application.yml` — server port, Cloudflare D1 credentials, JWT secret/expiry, Jackson settings, multipart limits
- `vite.config.js` — Vue plugin, dev proxy config

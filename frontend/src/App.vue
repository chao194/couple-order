<template>
  <div class="app-container">
    <!-- Header -->
    <header class="app-header">
      <div class="header-content">
        <div class="logo" @click="router.push('/menu')">
          <div class="logo-icon">
            <span>🍽️</span>
          </div>
          <div class="logo-text">
            <span class="title">点餐</span>
          </div>
        </div>
        <div class="nav-links">
          <router-link to="/menu" class="nav-item" :class="{ active: route.path === '/menu' }">
            <el-icon><Menu /></el-icon>
            <span>菜品</span>
          </router-link>
          <router-link v-if="authStore.isLoggedIn" to="/cart" class="nav-item cart-link" :class="{ active: route.path === '/cart' }">
            <el-icon><ShoppingCart /></el-icon>
            <span>购物车</span>
            <el-badge :value="cartStore.totalItems" :hidden="cartStore.totalItems === 0" class="cart-badge" />
          </router-link>
          <router-link v-if="authStore.isLoggedIn" to="/orders" class="nav-item" :class="{ active: route.path === '/orders' }">
            <el-icon><List /></el-icon>
            <span>订单</span>
          </router-link>
          <!-- User info -->
          <div v-if="authStore.isLoggedIn" class="user-info">
            <el-dropdown trigger="click">
              <span class="user-dropdown">
                <div class="avatar-circle">
                  <el-icon><User /></el-icon>
                </div>
                <span class="nickname">{{ authStore.nickname }}</span>
                <el-icon class="arrow"><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="authStore.isAdmin" @click="router.push('/admin')">
                    <el-icon><Setting /></el-icon>
                    管理后台
                  </el-dropdown-item>
                  <el-dropdown-item @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
          <router-link v-else to="/login" class="nav-item login-btn">
            <el-icon><User /></el-icon>
            <span>登录</span>
          </router-link>
        </div>
      </div>
    </header>

    <!-- Main Content -->
    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from './stores/cart'
import { useAuthStore } from './stores/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style>
/* ===== CSS Variables / Design Tokens ===== */
:root {
  /* Brand Colors */
  --color-primary: #ff6b8a;
  --color-primary-light: #ff8fa3;
  --color-primary-lighter: #ffb3c1;
  --color-primary-deep: #d63384;
  --color-primary-bg: #fff5f7;

  /* Gradients */
  --gradient-primary: linear-gradient(135deg, #ff6b8a 0%, #ff8fa3 50%, #ffb3c1 100%);
  --gradient-warm: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  --gradient-bg: linear-gradient(135deg, #fff5f5 0%, #ffe4e8 50%, #fff0f5 100%);
  --gradient-button: linear-gradient(135deg, #ff6b8a 0%, #ff5277 100%);

  /* Text Colors */
  --text-primary: #2d2d2d;
  --text-secondary: #666;
  --text-muted: #999;

  /* Shadows */
  --shadow-sm: 0 2px 8px rgba(255, 107, 138, 0.06);
  --shadow-md: 0 4px 16px rgba(255, 107, 138, 0.08);
  --shadow-lg: 0 8px 32px rgba(255, 107, 138, 0.12);
  --shadow-xl: 0 12px 40px rgba(255, 107, 138, 0.16);
  --shadow-glow: 0 0 20px rgba(255, 107, 138, 0.3);

  /* Border Radius */
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 16px;
  --radius-xl: 20px;
  --radius-full: 9999px;

  /* Transitions */
  --transition-fast: 0.2s ease;
  --transition-normal: 0.3s ease;
  --transition-bounce: 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);

  /* Glass Effect */
  --glass-bg: rgba(255, 255, 255, 0.72);
  --glass-border: 1px solid rgba(255, 255, 255, 0.4);
  --glass-blur: blur(20px);
}

/* ===== Global Reset ===== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  background: var(--gradient-bg);
  background-attachment: fixed;
  min-height: 100vh;
  color: var(--text-primary);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* ===== Custom Scrollbar ===== */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: transparent;
}

::-webkit-scrollbar-thumb {
  background: rgba(255, 107, 138, 0.25);
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 107, 138, 0.45);
}

/* ===== Animations ===== */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
}

@keyframes shimmer {
  0% { background-position: -200% 0; }
  100% { background-position: 200% 0; }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-6px); }
}

.app-container {
  min-height: 100vh;
}

/* ===== Header ===== */
.app-header {
  background: linear-gradient(135deg, rgba(255, 107, 138, 0.95) 0%, rgba(255, 143, 163, 0.95) 50%, rgba(255, 179, 193, 0.95) 100%);
  backdrop-filter: blur(20px) saturate(1.8);
  -webkit-backdrop-filter: blur(20px) saturate(1.8);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 1px solid rgba(255, 255, 255, 0.25);
  box-shadow: 0 4px 30px rgba(255, 107, 138, 0.25), 0 1px 0 rgba(255, 255, 255, 0.15) inset;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
}

/* ===== Logo ===== */
.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  transition: transform var(--transition-normal);
}

.logo:hover {
  transform: scale(1.05);
}

.logo:active {
  transform: scale(0.97);
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.22);
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06), inset 0 1px 0 rgba(255, 255, 255, 0.3);
  transition: transform var(--transition-bounce);
}

.logo:hover .logo-icon {
  transform: rotate(-8deg) scale(1.1);
}

.logo-text {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}

.logo-text .title {
  font-size: 18px;
  font-weight: 700;
  color: white;
  text-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  letter-spacing: 2px;
}

/* ===== Nav Links ===== */
.nav-links {
  display: flex;
  align-items: center;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  border-radius: var(--radius-md);
  transition: all var(--transition-normal);
  position: relative;
}

.nav-item:hover {
  color: white;
  background: rgba(255, 255, 255, 0.18);
}

.nav-item.active {
  color: white;
  background: rgba(255, 255, 255, 0.28);
  font-weight: 600;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.nav-item .el-icon {
  font-size: 17px;
  transition: transform var(--transition-bounce);
}

.nav-item:hover .el-icon {
  transform: scale(1.15);
}

/* ===== Cart Badge ===== */
.cart-link {
  position: relative;
}

.cart-badge {
  position: absolute;
  top: -2px;
  right: -2px;
}

.cart-badge .el-badge__content {
  background: #fff;
  color: var(--color-primary);
  font-weight: bold;
  border: none;
  box-shadow: 0 2px 8px rgba(255, 107, 138, 0.35);
  animation: pulse 2s ease-in-out infinite;
}

/* ===== User Dropdown ===== */
.user-info {
  margin-left: 8px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  color: white;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: var(--radius-md);
  transition: all var(--transition-normal);
}

.user-dropdown:hover {
  background: rgba(255, 255, 255, 0.18);
}

.avatar-circle {
  width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  backdrop-filter: blur(10px);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.3);
  transition: transform var(--transition-bounce);
}

.user-dropdown:hover .avatar-circle {
  transform: scale(1.1);
}

.nickname {
  font-size: 14px;
  font-weight: 500;
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow {
  font-size: 12px;
  opacity: 0.7;
  transition: transform var(--transition-normal);
}

.user-dropdown:hover .arrow {
  transform: rotate(180deg);
}

/* ===== Login Button ===== */
.login-btn {
  background: rgba(255, 255, 255, 0.2) !important;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.35) !important;
}

.login-btn:hover {
  background: rgba(255, 255, 255, 0.38) !important;
  box-shadow: 0 0 16px rgba(255, 255, 255, 0.2);
}

/* ===== Main Content ===== */
.app-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 24px 40px;
  min-height: calc(100vh - 64px);
  animation: fadeIn 0.4s ease;
}

/* ===== Global Card Styles ===== */
.page-card {
  border-radius: var(--radius-lg);
  border: none;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  box-shadow: var(--shadow-md), var(--glass-border);
  overflow: hidden;
  transition: transform var(--transition-normal), box-shadow var(--transition-normal);
}

.page-card:hover {
  box-shadow: var(--shadow-lg);
}

/* ===== Global Page Title ===== */
.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-primary-deep);
  margin-bottom: 24px;
  text-align: center;
  text-shadow: 0 2px 4px rgba(214, 51, 132, 0.1);
  animation: fadeInUp 0.5s ease;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.page-title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 48px;
  height: 3px;
  background: var(--gradient-primary);
  border-radius: 2px;
}

.page-title .el-icon {
  font-size: 26px;
  color: var(--color-primary);
}

/* ===== Element Plus Overrides (Global) ===== */
.el-button--danger {
  background: var(--gradient-button) !important;
  border: none !important;
  box-shadow: 0 2px 8px rgba(255, 107, 138, 0.3);
  transition: all var(--transition-normal) !important;
}

.el-button--danger:hover {
  box-shadow: 0 4px 16px rgba(255, 107, 138, 0.45) !important;
  transform: translateY(-1px);
}

.el-button--danger:active {
  transform: translateY(0) scale(0.98);
}

.el-button--danger.is-circle {
  box-shadow: 0 2px 8px rgba(255, 107, 138, 0.35) !important;
}

.el-button--danger.is-circle:hover {
  box-shadow: var(--shadow-glow) !important;
}

.el-card {
  border-radius: var(--radius-lg) !important;
  border: none !important;
}

.el-dialog {
  border-radius: var(--radius-xl) !important;
}

.el-input__wrapper {
  border-radius: var(--radius-sm) !important;
  transition: box-shadow var(--transition-fast) !important;
}

.el-input__wrapper:hover {
  box-shadow: 0 0 0 1px rgba(255, 107, 138, 0.3) !important;
}

.el-input.is-focus .el-input__wrapper {
  box-shadow: 0 0 0 1px var(--color-primary) !important;
}

.el-table {
  border-radius: var(--radius-md) !important;
}

.el-table tr {
  transition: background-color var(--transition-fast);
}

.el-tag {
  border-radius: var(--radius-full) !important;
  border: none !important;
}

.el-radio-button__inner {
  transition: all var(--transition-fast) !important;
}

.el-pagination {
  --el-color-primary: var(--color-primary) !important;
}

/* ===== Responsive: Mobile ===== */
@media (max-width: 768px) {
  .header-content {
    padding: 0 16px;
    height: 56px;
  }

  .logo-icon {
    width: 34px;
    height: 34px;
    font-size: 18px;
    border-radius: var(--radius-sm);
  }

  .logo-text .title {
    font-size: 16px;
  }

  .nav-item {
    padding: 6px 10px;
    font-size: 13px;
  }

  .nav-item span:not(.el-icon) {
    display: none;
  }

  .nav-item .el-icon {
    font-size: 19px;
  }

  .user-info {
    margin-left: 4px;
  }

  .user-dropdown {
    padding: 4px 8px;
  }

  .nickname {
    display: none;
  }

  .arrow {
    display: none;
  }

  .app-main {
    padding: 16px 16px 32px;
  }

  .page-title {
    font-size: 22px;
    margin-bottom: 20px;
  }
}
</style>

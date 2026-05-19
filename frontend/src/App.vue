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
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', Arial, sans-serif;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe4e8 50%, #fff0f5 100%);
  min-height: 100vh;
}

.app-container {
  min-height: 100vh;
}

/* ===== Header ===== */
.app-header {
  background: linear-gradient(135deg, #ff6b8a 0%, #ff8fa3 50%, #ffb3c1 100%);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 30px rgba(255, 107, 138, 0.2);
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
  transition: transform 0.2s;
}

.logo:hover {
  transform: scale(1.03);
}

.logo:active {
  transform: scale(0.98);
}

.logo-icon {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
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
  letter-spacing: 1px;
}

.logo-text .subtitle {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.85);
  font-weight: 500;
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
  border-radius: 10px;
  transition: all 0.25s ease;
  position: relative;
}

.nav-item:hover {
  color: white;
  background: rgba(255, 255, 255, 0.15);
}

.nav-item.active {
  color: white;
  background: rgba(255, 255, 255, 0.25);
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.nav-item .el-icon {
  font-size: 17px;
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
  color: #ff6b8a;
  font-weight: bold;
  border: none;
  box-shadow: 0 2px 6px rgba(255, 107, 138, 0.3);
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
  border-radius: 12px;
  transition: all 0.25s ease;
}

.user-dropdown:hover {
  background: rgba(255, 255, 255, 0.15);
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
  transition: transform 0.2s;
}

.user-dropdown:hover .arrow {
  transform: rotate(180deg);
}

/* ===== Login Button ===== */
.login-btn {
  background: rgba(255, 255, 255, 0.2) !important;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.login-btn:hover {
  background: rgba(255, 255, 255, 0.35) !important;
}

/* ===== Main Content ===== */
.app-main {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 24px 40px;
  min-height: calc(100vh - 64px);
}

/* ===== Global Card Styles ===== */
.page-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 8px 32px rgba(255, 107, 138, 0.08);
  overflow: hidden;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #d63384;
  margin-bottom: 24px;
  text-align: center;
  text-shadow: 0 2px 4px rgba(214, 51, 132, 0.1);
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
    border-radius: 10px;
  }

  .logo-text .title {
    font-size: 16px;
  }

  .logo-text .subtitle {
    font-size: 10px;
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
}
</style>

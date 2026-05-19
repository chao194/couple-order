<template>
  <div class="admin-view">
    <el-container class="admin-container">
      <!-- 左侧菜单 -->
      <el-aside width="200px" class="admin-aside">
        <div class="admin-logo">
          <el-icon><Setting /></el-icon>
          <span>管理后台</span>
        </div>
        <el-menu
          :default-active="currentRoute"
          class="admin-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="category">
            <el-icon><Menu /></el-icon>
            <span>分类管理</span>
          </el-menu-item>
          <el-menu-item index="dish">
            <el-icon><Dish /></el-icon>
            <span>菜品管理</span>
          </el-menu-item>
          <el-menu-item index="order">
            <el-icon><List /></el-icon>
            <span>订单管理</span>
          </el-menu-item>
          <el-menu-item index="user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="role">
            <el-icon><Key /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧内容 -->
      <el-main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const currentRoute = computed(() => {
  const path = route.path
  if (path.includes('/admin/category')) return 'category'
  if (path.includes('/admin/dish')) return 'dish'
  if (path.includes('/admin/order')) return 'order'
  if (path.includes('/admin/user')) return 'user'
  if (path.includes('/admin/role')) return 'role'
  return 'category'
})

function handleMenuSelect(index) {
  router.push(`/admin/${index}`)
}
</script>

<style scoped>
.admin-view {
  height: 100%;
}

.admin-container {
  height: 100%;
}

.admin-aside {
  background: linear-gradient(180deg, #ff6b8a 0%, #ff8e9e 100%);
  overflow: hidden;
  box-shadow: 4px 0 20px rgba(255, 107, 138, 0.2);
}

.admin-logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.admin-menu {
  border-right: none;
  background: transparent;
}

.admin-menu .el-menu-item {
  color: rgba(255, 255, 255, 0.85);
  height: 50px;
  line-height: 50px;
}

.admin-menu .el-menu-item:hover,
.admin-menu .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.2) !important;
  color: #fff !important;
}

.admin-menu .el-menu-item .el-icon {
  color: inherit;
}

.admin-main {
  padding: 0 0 0 20px;
  overflow-y: auto;
  background: transparent;
}

/* 路由切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

<template>
  <div class="order-list-view">
    <div class="page-title">
      <el-icon><List /></el-icon>
      我的订单
    </div>

    <!-- Filter -->
    <div class="filter-bar">
      <el-radio-group v-model="statusFilter" @change="loadOrders" size="large">
        <el-radio-button label="">全部</el-radio-button>
        <el-radio-button label="pending">待确认</el-radio-button>
        <el-radio-button label="preparing">准备中</el-radio-button>
        <el-radio-button label="ready">已完成</el-radio-button>
      </el-radio-group>
    </div>

    <!-- Loading -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="4" animated />
    </div>

    <!-- Order List -->
    <div v-else class="order-list">
      <div v-for="order in orders" :key="order.id" class="order-card" @click="router.push(`/orders/${order.id}`)">
        <div class="order-header">
          <div class="order-info">
            <span class="order-no">{{ order.orderNo }}</span>
            <el-tag :type="getStatusType(order.status)" size="small">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
          <span class="order-time">{{ order.createdAt }}</span>
        </div>

        <div class="order-body">
          <div class="customer-name">
            <el-icon><User /></el-icon>
            {{ order.customerName }}
          </div>
          <span class="order-amount">¥{{ order.totalAmount?.toFixed(2) }}</span>
        </div>

        <div v-if="order.remark" class="order-remark">
          <el-icon><ChatLineRound /></el-icon>
          {{ order.remark }}
        </div>
      </div>
    </div>

    <!-- Empty -->
    <el-empty v-if="!loading && orders.length === 0" description="还没有订单哦~">
      <el-button type="danger" @click="router.push('/menu')">
        <el-icon><Dish /></el-icon>
        去点餐
      </el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const orders = ref([])
const loading = ref(true)
const statusFilter = ref('')

const statusMap = {
  pending: { text: '待确认', type: 'warning' },
  preparing: { text: '准备中', type: 'primary' },
  ready: { text: '已完成', type: 'success' },
  cancelled: { text: '已取消', type: 'info' }
}

function getStatusText(status) {
  return statusMap[status]?.text || status
}

function getStatusType(status) {
  return statusMap[status]?.type || 'info'
}

async function loadOrders() {
  loading.value = true
  try {
    let res
    if (statusFilter.value) {
      res = await orderApi.getByStatus(statusFilter.value)
    } else {
      res = await orderApi.getAll()
    }
    if (res.data.success) {
      orders.value = res.data.data || []
    }
  } catch (e) {
    ElMessage.error('加载订单失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(loadOrders)
</script>

<style scoped>
.order-list-view {
  max-width: 700px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease;
}

/* ===== Filter Bar ===== */
.filter-bar {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.filter-bar .el-radio-button {
  --el-radio-button-checked-bg-color: var(--color-primary);
  --el-radio-button-checked-border-color: var(--color-primary);
}

.filter-bar :deep(.el-radio-button__inner) {
  border-radius: var(--radius-full) !important;
  padding: 8px 20px;
  font-weight: 500;
  border: none !important;
  box-shadow: none !important;
  background: var(--glass-bg);
  backdrop-filter: blur(8px);
  -webkit-backdrop-filter: blur(8px);
}

.filter-bar :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  box-shadow: 0 2px 12px rgba(255, 107, 138, 0.3) !important;
}

/* ===== Order List ===== */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* ===== Order Card ===== */
.order-card {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: var(--glass-border);
  border-radius: var(--radius-lg);
  padding: 20px 20px 20px 24px;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.order-card::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: var(--gradient-primary);
  border-radius: 2px 0 0 2px;
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.order-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.order-card:hover::before {
  opacity: 1;
}

.order-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.order-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.order-no {
  font-size: 13px;
  color: var(--text-secondary);
  font-family: 'SF Mono', 'Fira Code', monospace;
  background: rgba(255, 107, 138, 0.06);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
}

.order-time {
  font-size: 13px;
  color: var(--text-muted);
}

.order-body {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.customer-name {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
}

.order-amount {
  font-size: 22px;
  font-weight: 800;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.order-remark {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed rgba(255, 107, 138, 0.15);
  font-size: 13px;
  color: var(--text-muted);
}

.loading-container {
  padding: 20px;
}
</style>

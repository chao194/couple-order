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
}

.filter-bar {
  display: flex;
  justify-content: center;
  margin-bottom: 24px;
}

.filter-bar .el-radio-button {
  --el-radio-button-checked-bg-color: #ff6b8a;
  --el-radio-button-checked-border-color: #ff6b8a;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.order-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(255, 107, 138, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(255, 107, 138, 0.15);
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
  font-size: 14px;
  color: #666;
  font-family: monospace;
}

.order-time {
  font-size: 13px;
  color: #999;
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
  font-weight: bold;
  color: #333;
}

.order-amount {
  font-size: 20px;
  font-weight: bold;
  color: #ff6b8a;
}

.order-remark {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #eee;
  font-size: 13px;
  color: #999;
}

.loading-container {
  padding: 20px;
}
</style>

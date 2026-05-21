<template>
  <div class="order-detail-view">
    <div class="page-title">
      <el-icon><Document /></el-icon>
      订单详情
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <template v-else-if="order">
      <el-card class="page-card order-card">
        <!-- Status Banner -->
        <div class="status-banner" :class="order.status">
          <el-icon size="48"><component :is="getStatusIcon(order.status)" /></el-icon>
          <h2>{{ getStatusText(order.status) }}</h2>
        </div>

        <!-- Order Info -->
        <div class="info-section">
          <h3>订单信息</h3>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="订单号">
              <span class="order-no">{{ order.orderNo }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="点餐人">
              <el-icon><User /></el-icon>
              {{ order.customerName }}
            </el-descriptions-item>
            <el-descriptions-item label="下单时间">
              {{ order.createdAt }}
            </el-descriptions-item>
            <el-descriptions-item v-if="order.remark" label="备注">
              {{ order.remark }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- Order Items -->
        <div class="items-section">
          <h3>菜品明细</h3>
          <div v-for="item in order.items" :key="item.id" class="detail-item">
            <div class="item-info">
              <span class="item-name">{{ item.menuItemName }}</span>
              <span class="item-qty">x{{ item.quantity }}</span>
            </div>
            <span class="item-price">¥{{ item.subtotal?.toFixed(2) }}</span>
          </div>
        </div>

        <!-- Total -->
        <div class="total-section">
          <span>合计</span>
          <span class="total-price">¥{{ order.totalAmount?.toFixed(2) }}</span>
        </div>

        <!-- Actions -->
        <div class="actions">
          <el-button @click="router.push('/orders')">
            <el-icon><Back /></el-icon>
            返回订单列表
          </el-button>
          <el-button v-if="order.status === 'pending'" type="warning" @click="updateStatus('preparing')">
            开始准备
          </el-button>
          <el-button v-if="order.status === 'preparing'" type="success" @click="updateStatus('ready')">
            完成出餐
          </el-button>
          <el-button v-if="order.status !== 'cancelled' && order.status !== 'ready'" type="danger" @click="cancelOrder">
            取消订单
          </el-button>
        </div>
      </el-card>
    </template>

    <el-empty v-else description="订单不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Check, Clock, Loading, CircleClose } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const order = ref(null)
const loading = ref(true)

const statusMap = {
  pending: { text: '待确认', icon: 'Clock' },
  preparing: { text: '准备中', icon: 'Loading' },
  ready: { text: '已完成', icon: 'Check' },
  cancelled: { text: '已取消', icon: 'CircleClose' }
}

function getStatusText(status) {
  return statusMap[status]?.text || status
}

function getStatusIcon(status) {
  return statusMap[status]?.icon || 'Clock'
}

async function loadOrder() {
  loading.value = true
  try {
    const res = await orderApi.getById(route.params.id)
    if (res.data.success) {
      order.value = res.data.data
    }
  } catch (e) {
    ElMessage.error('加载订单失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function updateStatus(status) {
  try {
    const res = await orderApi.updateStatus(order.value.id, status)
    if (res.data.success) {
      order.value = res.data.data
      ElMessage.success('状态更新成功')
    }
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

async function cancelOrder() {
  try {
    await ElMessageBox.confirm('确定要取消这个订单吗？', '提示', { type: 'warning' })
    await updateStatus('cancelled')
  } catch (e) {
    // User cancelled
  }
}

onMounted(loadOrder)
</script>

<style scoped>
.order-detail-view {
  max-width: 620px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease;
}

.order-card {
  border-radius: var(--radius-xl) !important;
}

/* ===== Status Banner ===== */
.status-banner {
  text-align: center;
  padding: 36px;
  border-radius: var(--radius-lg);
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;
}

.status-banner::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.3), transparent 60%);
}

.status-banner .el-icon {
  position: relative;
  z-index: 1;
  animation: float 3s ease-in-out infinite;
}

.status-banner.pending {
  background: linear-gradient(135deg, #fff3cd 0%, #ffeaa7 100%);
  color: #856404;
}

.status-banner.preparing {
  background: linear-gradient(135deg, #d1ecf1 0%, #a8d8ea 100%);
  color: #0c5460;
}

.status-banner.ready {
  background: linear-gradient(135deg, #d4edda 0%, #a8e6cf 100%);
  color: #155724;
}

.status-banner.cancelled {
  background: linear-gradient(135deg, #f8d7da 0%, #f5b7b1 100%);
  color: #721c24;
}

.status-banner h2 {
  margin-top: 12px;
  font-size: 24px;
  font-weight: 700;
  position: relative;
  z-index: 1;
}

/* ===== Sections ===== */
.info-section,
.items-section {
  margin-bottom: 24px;
}

.info-section h3,
.items-section h3 {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 2px solid var(--color-primary);
  display: inline-block;
}

.info-section :deep(.el-descriptions) {
  border-radius: var(--radius-md);
  overflow: hidden;
}

.info-section :deep(.el-descriptions__label) {
  font-weight: 600;
}

.order-no {
  font-family: 'SF Mono', 'Fira Code', monospace;
  color: var(--color-primary);
  font-weight: 700;
  background: rgba(255, 107, 138, 0.08);
  padding: 2px 8px;
  border-radius: var(--radius-sm);
}

/* ===== Detail Items ===== */
.detail-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid rgba(255, 107, 138, 0.06);
  transition: all var(--transition-fast);
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item:hover {
  background: rgba(255, 107, 138, 0.03);
  padding-left: 8px;
  padding-right: 8px;
  border-radius: var(--radius-sm);
}

.item-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-name {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-primary);
}

.item-qty {
  font-size: 13px;
  color: var(--text-muted);
  background: rgba(0, 0, 0, 0.04);
  padding: 1px 8px;
  border-radius: var(--radius-full);
}

.item-price {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-primary);
}

/* ===== Total ===== */
.total-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 0;
  border-top: 2px solid rgba(255, 107, 138, 0.1);
  margin-top: 16px;
}

.total-section span:first-child {
  font-size: 18px;
  font-weight: 700;
}

.total-price {
  font-size: 28px;
  font-weight: 800;
  background: var(--gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* ===== Actions ===== */
.actions {
  display: flex;
  gap: 12px;
  margin-top: 28px;
  flex-wrap: wrap;
}

.actions .el-button {
  flex: 1;
  min-width: 120px;
  border-radius: var(--radius-md) !important;
  font-weight: 600;
}

.loading-container {
  padding: 20px;
}
</style>

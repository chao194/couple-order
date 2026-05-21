<template>
  <div class="order-manage">
    <el-card class="section-card" shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <el-icon><List /></el-icon>
          <span>订单管理</span>
        </div>
      </template>

      <el-table :data="orders" stripe style="width: 100%">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column prop="customerName" label="点餐人" width="100" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="金额" width="100">
          <template #default="{ row }">
            <span class="amount">¥{{ row.totalAmount?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="下单时间" width="170" />
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-select
              :model-value="row.status"
              size="small"
              style="width: 110px; margin-right: 8px;"
              @change="(val) => updateStatus(row.id, val)"
            >
              <el-option label="待处理" value="pending" />
              <el-option label="制作中" value="preparing" />
              <el-option label="已完成" value="ready" />
              <el-option label="已关闭" value="completed" />
            </el-select>
            <el-button type="danger" size="small" text @click="deleteOrder(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="loadOrders"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { orderApi } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)

const statusMap = {
  pending: { label: '待处理', type: 'warning' },
  preparing: { label: '制作中', type: '' },
  ready: { label: '已完成', type: 'success' },
  completed: { label: '已关闭', type: 'info' }
}

function statusLabel(status) {
  return statusMap[status]?.label || status
}

function statusType(status) {
  return statusMap[status]?.type || 'info'
}

async function loadOrders() {
  loading.value = true
  try {
    const res = await orderApi.getPage(currentPage.value, pageSize)
    if (res.data.success) {
      orders.value = res.data.data.list || []
      total.value = res.data.data.total || 0
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

async function updateStatus(id, status) {
  try {
    const res = await orderApi.updateStatus(id, status)
    if (res.data.success) {
      ElMessage.success('状态更新成功')
      loadOrders()
    }
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

async function deleteOrder(id) {
  try {
    await ElMessageBox.confirm('确定要删除这个订单吗？', '提示', { type: 'warning' })
    const res = await orderApi.delete(id)
    if (res.data.success) {
      ElMessage.success('删除成功')
      loadOrders()
    }
  } catch (e) {
    // Cancelled
  }
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 700;
}

.card-header .el-icon {
  width: 32px;
  height: 32px;
  background: var(--gradient-primary);
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
}

.amount {
  color: var(--color-primary);
  font-weight: 700;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 107, 138, 0.06);
}
</style>

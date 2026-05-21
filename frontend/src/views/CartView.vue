<template>
  <div class="cart-view">
    <div class="page-title">
      <el-icon><ShoppingCart /></el-icon>
      购物车
    </div>

    <div v-if="cartStore.items.length === 0" class="empty-cart">
      <el-empty description="购物车是空的哦~">
        <el-button type="danger" @click="router.push('/menu')">
          <el-icon><Dish /></el-icon>
          去点餐
        </el-button>
      </el-empty>
    </div>

    <template v-else>
      <el-card class="page-card cart-card">
        <!-- Cart Items -->
        <div v-for="item in cartStore.items" :key="item.menuItemId" class="cart-item">
          <div class="item-info">
            <h4>{{ item.name }}</h4>
            <span class="item-price">¥{{ item.price.toFixed(2) }}</span>
          </div>
          <div class="item-actions">
            <div class="quantity-control">
              <el-button type="danger" circle size="small" @click="cartStore.updateQuantity(item.menuItemId, item.quantity - 1)">
                <el-icon><Minus /></el-icon>
              </el-button>
              <span class="qty-num">{{ item.quantity }}</span>
              <el-button type="danger" circle size="small" @click="cartStore.updateQuantity(item.menuItemId, item.quantity + 1)">
                <el-icon><Plus /></el-icon>
              </el-button>
            </div>
            <span class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>
        </div>

        <!-- Divider -->
        <el-divider />

        <!-- Total -->
        <div class="cart-total">
          <span>合计</span>
          <span class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
        </div>

        <!-- Order Form -->
        <el-divider>订单信息</el-divider>

        <el-form :model="orderForm" label-width="80px" class="order-form">
          <el-form-item label="点餐人">
            <el-input v-model="orderForm.customerName" placeholder="请输入点餐人姓名" maxlength="20" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="orderForm.remark" type="textarea" placeholder="有什么特殊要求吗？" :rows="2" />
          </el-form-item>
        </el-form>

        <!-- Actions -->
        <div class="cart-actions">
          <el-button size="large" @click="clearCart">
            <el-icon><Delete /></el-icon>
            清空
          </el-button>
          <el-button type="danger" size="large" @click="submitOrder" :loading="submitting">
            <el-icon><Check /></el-icon>
            确认下单
          </el-button>
        </div>
      </el-card>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'
import { orderApi } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()
const submitting = ref(false)

const orderForm = reactive({
  customerName: '',
  remark: ''
})

function clearCart() {
  ElMessageBox.confirm('确定要清空购物车吗？', '提示', {
    type: 'warning'
  }).then(() => {
    cartStore.clearCart()
    ElMessage.success('已清空')
  }).catch(() => {})
}

async function submitOrder() {
  if (!orderForm.customerName.trim()) {
    ElMessage.warning('请输入点餐人姓名')
    return
  }

  submitting.value = true
  try {
    const orderData = {
      customerName: orderForm.customerName,
      remark: orderForm.remark,
      items: cartStore.items.map(item => ({
        menuItemId: item.menuItemId,
        menuItemName: item.name,
        price: item.price,
        quantity: item.quantity
      }))
    }

    const res = await orderApi.create(orderData)
    if (res.data.success) {
      ElMessage.success('下单成功！')
      cartStore.clearCart()
      router.push(`/orders/${res.data.data.id}`)
    } else {
      ElMessage.error(res.data.message || '下单失败')
    }
  } catch (e) {
    ElMessage.error('下单失败，请稍后重试')
    console.error(e)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.cart-view {
  max-width: 620px;
  margin: 0 auto;
  animation: fadeInUp 0.5s ease;
}

.cart-card {
  border-radius: var(--radius-xl) !important;
}

/* ===== Cart Items ===== */
.cart-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 0;
  border-bottom: 1px solid rgba(255, 107, 138, 0.06);
  transition: all var(--transition-fast);
}

.cart-item:hover {
  background: rgba(255, 107, 138, 0.03);
  padding-left: 8px;
  padding-right: 8px;
  border-radius: var(--radius-sm);
}

.cart-item:last-of-type {
  border-bottom: none;
}

.item-info h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
}

.item-price {
  font-size: 13px;
  color: var(--text-muted);
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.qty-num {
  font-size: 17px;
  font-weight: 700;
  color: var(--color-primary);
  min-width: 24px;
  text-align: center;
}

.item-subtotal {
  font-size: 17px;
  font-weight: 800;
  color: var(--color-primary);
  min-width: 72px;
  text-align: right;
}

/* ===== Total ===== */
.cart-total {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 0;
}

.cart-total span:first-child {
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

/* ===== Form ===== */
.order-form {
  margin-top: 16px;
}

.order-form :deep(.el-input__wrapper) {
  border-radius: var(--radius-md) !important;
}

.order-form :deep(.el-textarea__inner) {
  border-radius: var(--radius-md) !important;
}

/* ===== Actions ===== */
.cart-actions {
  display: flex;
  gap: 16px;
  margin-top: 28px;
}

.cart-actions .el-button {
  border-radius: var(--radius-md) !important;
  padding: 12px 24px;
  font-weight: 600;
}

.cart-actions .el-button:last-child {
  flex: 1;
  height: 48px;
  font-size: 16px;
  letter-spacing: 2px;
}

/* ===== Empty State ===== */
.empty-cart {
  padding: 60px 0;
  animation: fadeIn 0.5s ease;
}
</style>

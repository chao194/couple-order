<template>
  <div class="menu-view">
    <div class="menu-layout">
      <!-- Left Sidebar: Categories -->
      <aside class="category-sidebar" ref="sidebarRef">
        <div class="category-list">
          <div
            v-for="cat in categories"
            :key="cat.value"
            class="category-item"
            :class="{ active: selectedCategory === cat.value }"
            @click="selectCategory(cat.value)"
            :ref="el => { if (el) catRefs[cat.value] = el }"
          >
            <span class="cat-icon">{{ cat.icon }}</span>
            <span class="cat-label">{{ cat.label }}</span>
          </div>
        </div>
      </aside>

      <!-- Right Main: Dishes -->
      <main class="dish-main" ref="mainRef">
        <div class="dish-section" v-for="cat in categories" :key="cat.value" :ref="el => { if (el) sectionRefs[cat.value] = el }">
          <div class="section-header">
            <span class="section-icon">{{ cat.icon }}</span>
            <h2 class="section-title">{{ cat.label }}</h2>
            <span class="section-count">{{ getCategoryCount(cat.value) }}道</span>
          </div>
          <div class="dish-grid">
            <div v-for="item in getItemsByCategory(cat.value)" :key="item.id" class="dish-card">
              <div class="card-image">
                <el-image v-if="item.imageUrl" :src="item.imageUrl" :alt="item.name" fit="cover" class="food-image" @error="handleImageError(item)">
                  <template #error>
                    <div class="food-emoji">{{ getFoodEmoji(item.category) }}</div>
                  </template>
                </el-image>
                <div v-else class="food-emoji">{{ getFoodEmoji(item.category) }}</div>
              </div>
              <div class="card-content">
                <div class="card-top">
                  <h3 class="food-name">{{ item.name }}</h3>
                </div>
                <p class="food-desc">{{ item.description }}</p>
                <div class="card-footer">
                  <span class="price">¥{{ item.price.toFixed(2) }}</span>
                  <div class="quantity-control">
                    <el-button
                      v-if="getItemQty(item.id) > 0"
                      type="danger"
                      circle
                      size="small"
                      @click="cartStore.updateQuantity(item.id, getItemQty(item.id) - 1)"
                    >
                      <el-icon><Minus /></el-icon>
                    </el-button>
                    <span v-if="getItemQty(item.id) > 0" class="qty-num">{{ getItemQty(item.id) }}</span>
                    <el-button
                      type="danger"
                      circle
                      size="small"
                      @click="cartStore.addItem(item)"
                    >
                      <el-icon><Plus /></el-icon>
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="6" animated />
        </div>

        <!-- Empty State -->
        <el-empty v-if="!loading && allMenuItems.length === 0" description="暂无菜品" />
      </main>
    </div>

    <!-- Floating Cart Button -->
    <div v-if="cartStore.totalItems > 0" class="floating-cart" @click="router.push('/cart')">
      <el-badge :value="cartStore.totalItems" type="danger">
        <el-button type="danger" circle size="large">
          <el-icon size="24"><ShoppingCart /></el-icon>
        </el-button>
      </el-badge>
      <span class="cart-total">¥{{ cartStore.totalPrice.toFixed(2) }}</span>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { menuApi, categoryApi } from '../api'
import { useCartStore } from '../stores/cart'
import { ElMessage } from 'element-plus'

const router = useRouter()
const cartStore = useCartStore()
const allMenuItems = ref([])
const categories = ref([])
const loading = ref(true)
const selectedCategory = ref('')

const sidebarRef = ref(null)
const mainRef = ref(null)
const catRefs = reactive({})
const sectionRefs = reactive({})

const foodEmojis = ref({})

function getFoodEmoji(category) {
  return foodEmojis.value[category] || '🍽️'
}

function handleImageError(item) {
  console.warn('图片加载失败:', item.imageUrl)
}

function getItemQty(id) {
  return cartStore.getItemQuantity(id)
}

function getCategoryCount(category) {
  if (!category) return allMenuItems.value.length
  return allMenuItems.value.filter(item => item.category === category).length
}

function getItemsByCategory(category) {
  if (!category) return allMenuItems.value
  return allMenuItems.value.filter(item => item.category === category)
}

function selectCategory(category) {
  selectedCategory.value = category
  // Scroll to the corresponding section
  const targetRef = sectionRefs[category]
  if (targetRef) {
    targetRef.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// Intersection Observer for auto-highlighting category on scroll
let observer = null

function setupScrollObserver() {
  if (observer) observer.disconnect()

  // Use dish-main as root on mobile (fixed layout), viewport on desktop
  const isMobile = window.innerWidth <= 768
  const root = isMobile && mainRef.value ? mainRef.value : null

  observer = new IntersectionObserver(
    (entries) => {
      for (const entry of entries) {
        if (entry.isIntersecting) {
          const catValue = entry.target.dataset.category
          if (catValue !== undefined) {
            selectedCategory.value = catValue
            // Scroll sidebar to show active item
            const catEl = catRefs[catValue]
            if (catEl && sidebarRef.value) {
              catEl.scrollIntoView({ behavior: 'smooth', block: 'nearest' })
            }
          }
        }
      }
    },
    {
      root: root,
      rootMargin: '-10px 0px -60% 0px',
      threshold: 0
    }
  )

  nextTick(() => {
    for (const cat of categories.value) {
      const el = sectionRefs[cat.value]
      if (el) {
        el.dataset.category = cat.value
        observer.observe(el)
      }
    }
  })
}

async function loadCategories() {
  try {
    const res = await categoryApi.getAll()
    if (res.data.success) {
      const cats = res.data.data || []
      // 构建 emoji 映射
      const emojiMap = {}
      cats.forEach(c => {
        if (c.icon) emojiMap[c.name] = c.icon
      })
      foodEmojis.value = emojiMap
      // 构建分类列表，第一个是"全部"
      categories.value = [
        { value: '', label: '全部', icon: '🍽️' },
        ...cats.map(c => ({ value: c.name, label: c.name, icon: c.icon || '🍽️' }))
      ]
    }
  } catch (e) {
    console.error('加载分类失败', e)
    // 回退默认分类
    categories.value = [{ value: '', label: '全部', icon: '🍽️' }]
  }
}

async function loadMenu() {
  loading.value = true
  try {
    await loadCategories()
    const res = await menuApi.getAll()
    if (res.data.success) {
      allMenuItems.value = res.data.data || []
      nextTick(setupScrollObserver)
    }
  } catch (e) {
    ElMessage.error('加载菜单失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

onMounted(loadMenu)
onBeforeUnmount(() => {
  if (observer) observer.disconnect()
})
</script>

<style scoped>
.menu-view {
  position: relative;
}

/* ===== Layout ===== */
.menu-layout {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  animation: fadeIn 0.4s ease;
}

/* ===== Left Sidebar ===== */
.category-sidebar {
  width: 160px;
  flex-shrink: 0;
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border-radius: var(--radius-lg);
  border: var(--glass-border);
  padding: 12px 0;
  box-shadow: var(--shadow-md);
  position: sticky;
  top: 80px;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
  scrollbar-width: none;
}

.category-sidebar::-webkit-scrollbar {
  display: none;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding: 0 6px;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 12px;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all var(--transition-normal);
  white-space: nowrap;
}

.category-item:hover {
  background: rgba(255, 107, 138, 0.08);
}

.category-item.active {
  background: var(--gradient-primary);
  color: white;
  box-shadow: 0 4px 16px rgba(255, 107, 138, 0.35);
  font-weight: 600;
  transform: scale(1.02);
}

.category-item .cat-icon {
  font-size: 20px;
  flex-shrink: 0;
  transition: transform var(--transition-bounce);
}

.category-item.active .cat-icon {
  transform: scale(1.15);
}

.category-item .cat-label {
  flex: 1;
  font-size: 14px;
  font-weight: 500;
}

.category-item.active .cat-label {
  font-weight: 700;
}

/* ===== Right Main ===== */
.dish-main {
  flex: 1;
  min-width: 0;
  scroll-behavior: smooth;
}

/* ===== Section ===== */
.dish-section {
  margin-bottom: 28px;
  scroll-margin-top: 80px;
  animation: fadeInUp 0.5s ease;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
  padding-bottom: 10px;
  border-bottom: 2px solid rgba(255, 107, 138, 0.1);
}

.section-icon {
  font-size: 24px;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 107, 138, 0.08);
  border-radius: var(--radius-sm);
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.section-count {
  font-size: 12px;
  color: var(--text-muted);
  background: rgba(255, 107, 138, 0.08);
  color: var(--color-primary);
  padding: 2px 10px;
  border-radius: var(--radius-full);
  font-weight: 600;
}

/* ===== Dish Grid ===== */
.dish-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}

/* ===== Dish Card ===== */
.dish-card {
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  border: var(--glass-border);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
  cursor: pointer;
}

.dish-card:hover {
  transform: translateY(-6px) scale(1.01);
  box-shadow: var(--shadow-lg);
}

.card-image {
  height: 140px;
  background: var(--gradient-warm);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.card-image::after {
  content: '';
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 60%, rgba(0, 0, 0, 0.05) 100%);
}

.food-emoji {
  font-size: 52px;
  transition: transform var(--transition-bounce);
  position: relative;
  z-index: 1;
}

.dish-card:hover .food-emoji {
  transform: scale(1.2) rotate(-5deg);
}

.food-image {
  width: 100%;
  height: 100%;
  transition: transform var(--transition-slow);
}

.dish-card:hover .food-image {
  transform: scale(1.08);
}

.food-image :deep(.el-image__error) {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: var(--gradient-warm);
}

.card-content {
  padding: 14px 16px;
}

.card-top {
  margin-bottom: 6px;
}

.food-name {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-primary);
  margin: 0;
}

.food-desc {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 12px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price {
  font-size: 18px;
  font-weight: 800;
  color: var(--color-primary);
  position: relative;
}

.price::before {
  content: '¥';
  font-size: 13px;
  font-weight: 600;
  margin-right: 1px;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 6px;
}

.qty-num {
  font-size: 15px;
  font-weight: 700;
  color: var(--color-primary);
  min-width: 20px;
  text-align: center;
}

/* ===== Floating Cart ===== */
.floating-cart {
  position: fixed;
  bottom: 40px;
  right: 40px;
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
  z-index: 1000;
  animation: fadeInUp 0.5s ease;
}

.floating-cart .el-button {
  width: 58px;
  height: 58px;
  box-shadow: 0 6px 24px rgba(255, 107, 138, 0.4) !important;
  animation: pulse 2s ease-in-out infinite;
}

.floating-cart:hover .el-button {
  animation: none;
  box-shadow: var(--shadow-glow) !important;
}

.cart-total {
  font-size: 18px;
  font-weight: 800;
  color: var(--color-primary);
  background: var(--glass-bg);
  backdrop-filter: var(--glass-blur);
  -webkit-backdrop-filter: var(--glass-blur);
  padding: 10px 18px;
  border-radius: var(--radius-full);
  box-shadow: var(--shadow-md);
  border: var(--glass-border);
  transition: all var(--transition-normal);
}

.floating-cart:hover .cart-total {
  transform: scale(1.05);
  box-shadow: var(--shadow-lg);
}

.loading-container {
  padding: 20px;
}

/* ===== Responsive: Tablet ===== */
@media (max-width: 1024px) {
  .category-sidebar {
    width: 130px;
  }

  .dish-grid {
    grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  }
}

/* ===== Responsive: Mobile (Meituan Style) ===== */
@media (max-width: 768px) {
  .menu-layout {
    gap: 0;
    position: fixed;
    top: 56px;
    left: 0;
    right: 0;
    bottom: 0;
    overflow: hidden;
  }

  .category-sidebar {
    width: 85px;
    border-radius: 0;
    padding: 0;
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    max-height: none;
    background: rgba(248, 248, 248, 0.95);
    backdrop-filter: blur(10px);
    -webkit-backdrop-filter: blur(10px);
    border: none;
    box-shadow: 1px 0 0 rgba(0, 0, 0, 0.05);
    flex-shrink: 0;
    overflow-y: auto;
  }

  .category-list {
    padding: 0;
    gap: 0;
  }

  .category-item {
    flex-direction: column;
    gap: 4px;
    padding: 14px 4px;
    border-radius: 0;
    text-align: center;
    position: relative;
  }

  .category-item:hover {
    background: transparent;
  }

  .category-item.active {
    background: white;
    color: var(--color-primary);
    box-shadow: none;
    font-weight: 600;
    transform: none;
  }

  .category-item.active::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 3px;
    height: 24px;
    background: var(--color-primary);
    border-radius: 0 3px 3px 0;
  }

  .category-item .cat-icon {
    font-size: 22px;
  }

  .category-item .cat-label {
    font-size: 12px;
    font-weight: 500;
  }

  .category-item.active .cat-label {
    font-weight: 700;
  }

  .dish-main {
    position: absolute;
    top: 0;
    left: 85px;
    right: 0;
    bottom: 0;
    overflow-y: auto;
    padding: 12px;
    -webkit-overflow-scrolling: touch;
  }

  .dish-section {
    margin-bottom: 20px;
    scroll-margin-top: 10px;
  }

  .section-header {
    margin-bottom: 10px;
    padding-bottom: 8px;
  }

  .section-title {
    font-size: 16px;
  }

  .dish-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .dish-card {
    display: flex;
    flex-direction: row;
    border-radius: var(--radius-md);
  }

  .card-image {
    width: 100px;
    height: auto;
    min-height: 100px;
    flex-shrink: 0;
    border-radius: var(--radius-md) 0 0 var(--radius-md);
  }

  .food-emoji {
    font-size: 36px;
  }

  .card-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 10px 12px;
    min-width: 0;
  }

  .food-name {
    font-size: 14px;
  }

  .food-desc {
    margin-bottom: 6px;
    -webkit-line-clamp: 1;
    line-clamp: 1;
    font-size: 11px;
  }

  .price {
    font-size: 15px;
  }

  .floating-cart {
    bottom: 20px;
    right: 16px;
  }

  .floating-cart .el-button {
    width: 48px;
    height: 48px;
  }

  .cart-total {
    font-size: 14px;
    padding: 5px 10px;
  }
}
</style>

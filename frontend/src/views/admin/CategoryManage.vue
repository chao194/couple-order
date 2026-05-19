<template>
  <div class="category-manage">
    <el-card class="section-card" shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <el-icon><Menu /></el-icon>
          <span>分类管理</span>
          <el-button type="danger" size="small" @click="showDialog = true; editingCategory = null; resetForm()">
            <el-icon><Plus /></el-icon>
            添加分类
          </el-button>
        </div>
      </template>

      <el-table :data="categories" stripe style="width: 100%">
        <el-table-column label="图标" width="80" align="center">
          <template #default="{ row }">
            <span style="font-size: 24px;">{{ row.icon || '🍽️' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="分类名称" align="center" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="editCategory(row)">编辑</el-button>
            <el-button type="danger" size="small" text @click="deleteCategory(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Category Dialog -->
    <el-dialog v-model="showDialog" :title="editingCategory ? '编辑分类' : '添加分类'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称" required>
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="图标">
          <div class="icon-picker">
            <span class="icon-preview">{{ form.icon || '🍽️' }}</span>
            <div class="emoji-list">
              <span
                v-for="emoji in emojiList"
                :key="emoji"
                class="emoji-item"
                :class="{ active: form.icon === emoji }"
                @click="form.icon = emoji"
              >{{ emoji }}</span>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="danger" @click="saveCategory" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { categoryApi } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const categories = ref([])
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const editingCategory = ref(null)

const form = reactive({
  name: '',
  icon: '🍽️',
  sortOrder: 0
})

const emojiList = [
  '🍽️', '🍳', '🍚', '🥣', '🥤', '🍜', '🍕', '🍔', '🍟', '🥗',
  '🍰', '🍮', '🍩', '🍪', '🎂', '🍫', '🍬', '🍭', '🍯', '🥛',
  '☕', '🍵', '🧃', '🍺', '🍷', '🥂', '🍹', '🧋', '🫖', '🧊',
  '🥩', '🍗', '🥓', '🌭', '🥪', '🌮', '🌯', '🫔', '🥙', '🧆',
  '🥘', '🍲', '🫕', '🍱', '🍣', '🍤', '🥟', '🍥', '🥮', '🍡',
  '🍧', '🍨', '🍦', '🥧', '🧁', '🥯', '🧀', '🥖', '🍞', '🥯'
]

function resetForm() {
  form.name = ''
  form.icon = '🍽️'
  form.sortOrder = 0
}

async function loadCategories() {
  loading.value = true
  try {
    const res = await categoryApi.getAll()
    if (res.data.success) {
      categories.value = res.data.data || []
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function editCategory(cat) {
  editingCategory.value = cat
  Object.assign(form, {
    name: cat.name,
    icon: cat.icon || '🍽️',
    sortOrder: cat.sortOrder
  })
  showDialog.value = true
}

async function saveCategory() {
  if (!form.name) {
    ElMessage.warning('请输入分类名称')
    return
  }

  saving.value = true
  try {
    let res
    if (editingCategory.value) {
      res = await categoryApi.update(editingCategory.value.id, form)
    } else {
      res = await categoryApi.create(form)
    }

    if (res.data.success) {
      ElMessage.success(editingCategory.value ? '更新成功' : '添加成功')
      showDialog.value = false
      editingCategory.value = null
      resetForm()
      loadCategories()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

async function deleteCategory(id) {
  try {
    await ElMessageBox.confirm('确定要删除这个分类吗？', '提示', { type: 'warning' })
    const res = await categoryApi.delete(id)
    if (res.data.success) {
      ElMessage.success('删除成功')
      loadCategories()
    }
  } catch (e) {
    // Cancelled
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}

.card-header .el-button {
  margin-left: auto;
}

.icon-picker {
  width: 100%;
}

.icon-preview {
  display: inline-block;
  font-size: 32px;
  margin-bottom: 8px;
  padding: 6px 12px;
  background: #fff5f7;
  border-radius: 8px;
}

.emoji-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  max-height: 160px;
  overflow-y: auto;
  padding: 8px;
  background: #fafafa;
  border-radius: 8px;
  border: 1px solid #eee;
}

.emoji-item {
  font-size: 22px;
  padding: 4px 6px;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.2s;
}

.emoji-item:hover {
  background: #fff0f3;
  transform: scale(1.15);
}

.emoji-item.active {
  background: #ffe0e6;
  box-shadow: 0 0 0 2px #ff6b8a;
}
</style>

<template>
  <div class="dish-manage">
    <el-card class="section-card" shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <el-icon><Dish /></el-icon>
          <span>菜品管理</span>
          <el-button type="danger" size="small" @click="showDialog = true; editingItem = null; resetForm()">
            <el-icon><Plus /></el-icon>
            添加菜品
          </el-button>
        </div>
      </template>

      <el-table :data="menuItems" stripe style="width: 100%">
        <el-table-column label="图片" width="80" align="center">
          <template #default="{ row }">
            <el-image
              v-if="row.imageUrl"
              :src="row.imageUrl"
              style="width: 50px; height: 50px; border-radius: 8px;"
              fit="cover"
            />
            <div v-else class="no-image">🍽️</div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="菜品名称" width="120" align="center" />
        <el-table-column prop="category" label="分类" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="80" align="center">
          <template #default="{ row }">
            <span style="color: #ff6b8a; font-weight: bold;">¥{{ row.price?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.available ? 'success' : 'info'" size="small">
              {{ row.available ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="editItem(row)">编辑</el-button>
            <el-button type="danger" size="small" text @click="deleteItem(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Add/Edit Menu Item Dialog -->
    <el-dialog v-model="showDialog" :title="editingItem ? '编辑菜品' : '添加菜品'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="菜品名称" required>
          <el-input v-model="form.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="分类" required>
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" required>
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="图片">
          <div class="upload-container">
            <el-upload
              class="image-uploader"
              :show-file-list="false"
              :before-upload="beforeImageUpload"
              :http-request="handleImageUpload"
              accept="image/*"
            >
              <img v-if="form.imageUrl" :src="form.imageUrl" class="uploaded-image" />
              <el-icon v-else class="upload-icon"><Plus /></el-icon>
            </el-upload>
            <div class="upload-tip">支持 jpg/png 格式，大小不超过 2MB</div>
          </div>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.available" active-text="上架" inactive-text="下架" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="danger" @click="saveItem" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { menuApi, categoryApi, fileApi } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const menuItems = ref([])
const categories = ref([])
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const editingItem = ref(null)

const form = reactive({
  name: '',
  category: '',
  price: 0,
  description: '',
  imageUrl: '',
  available: true
})

function resetForm() {
  form.name = ''
  form.category = ''
  form.price = 0
  form.description = ''
  form.imageUrl = ''
  form.available = true
}

async function loadData() {
  loading.value = true
  try {
    const [menuRes, catRes] = await Promise.all([menuApi.getAll(), categoryApi.getAll()])
    if (menuRes.data.success) {
      menuItems.value = menuRes.data.data || []
    }
    if (catRes.data.success) {
      categories.value = catRes.data.data || []
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function editItem(item) {
  editingItem.value = item
  Object.assign(form, {
    name: item.name,
    category: item.category,
    price: item.price,
    description: item.description,
    imageUrl: item.imageUrl || '',
    available: item.available
  })
  showDialog.value = true
}

async function saveItem() {
  if (!form.name || !form.category) {
    ElMessage.warning('请填写必填项')
    return
  }

  saving.value = true
  try {
    let res
    if (editingItem.value) {
      res = await menuApi.update(editingItem.value.id, form)
    } else {
      res = await menuApi.create(form)
    }

    if (res.data.success) {
      ElMessage.success(editingItem.value ? '更新成功' : '添加成功')
      showDialog.value = false
      editingItem.value = null
      resetForm()
      loadData()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

async function deleteItem(id) {
  try {
    await ElMessageBox.confirm('确定要删除这个菜品吗？', '提示', { type: 'warning' })
    const res = await menuApi.delete(id)
    if (res.data.success) {
      ElMessage.success('删除成功')
      loadData()
    }
  } catch (e) {
    // Cancelled
  }
}

// Image upload
function beforeImageUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

async function handleImageUpload(options) {
  const formData = new FormData()
  formData.append('file', options.file)

  try {
    const res = await fileApi.upload(formData)
    if (res.data.success) {
      form.imageUrl = res.data.data.url
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(res.data.message || '上传失败')
    }
  } catch (e) {
    ElMessage.error('图片上传失败')
  }
}

onMounted(() => {
  loadData()
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

.no-image {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 24px;
}

.image-uploader {
  width: 120px;
  height: 120px;
  border: 2px dashed #ddd;
  border-radius: 12px;
  cursor: pointer;
  overflow: hidden;
  transition: border-color 0.3s;
}

.image-uploader :deep(.el-upload) {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-uploader:hover {
  border-color: #ff6b8a;
}

.uploaded-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  pointer-events: none;
}

.upload-icon {
  font-size: 28px;
  color: #999;
}

.upload-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
</style>

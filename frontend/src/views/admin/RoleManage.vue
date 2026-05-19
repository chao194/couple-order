<template>
  <div class="role-manage">
    <el-card class="section-card" shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <el-icon><Key /></el-icon>
          <span>角色管理</span>
          <el-button type="danger" size="small" @click="showDialog = true; editingRole = null; resetForm()">
            <el-icon><Plus /></el-icon>
            添加角色
          </el-button>
        </div>
      </template>

      <el-table :data="roles" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="name" label="角色名称" width="120" align="center" />
        <el-table-column prop="description" label="描述" align="center" />
        <el-table-column prop="createdAt" label="创建时间" align="center" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="editRole(row)">编辑</el-button>
            <el-button type="danger" size="small" text @click="deleteRole(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Role Dialog -->
    <el-dialog v-model="showDialog" :title="editingRole ? '编辑角色' : '添加角色'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="角色名称" required>
          <el-input v-model="form.name" placeholder="请输入角色名称" :disabled="editingRole !== null" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入角色描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="danger" @click="saveRole" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { roleApi } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const roles = ref([])
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const editingRole = ref(null)

const form = reactive({
  name: '',
  description: ''
})

function resetForm() {
  form.name = ''
  form.description = ''
}

async function loadRoles() {
  loading.value = true
  try {
    const res = await roleApi.getAll()
    if (res.data.success) {
      roles.value = res.data.data || []
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function editRole(role) {
  editingRole.value = role
  Object.assign(form, {
    name: role.name,
    description: role.description
  })
  showDialog.value = true
}

async function saveRole() {
  if (!form.name) {
    ElMessage.warning('请输入角色名称')
    return
  }

  saving.value = true
  try {
    let res
    if (editingRole.value) {
      res = await roleApi.update(editingRole.value.id, form)
    } else {
      res = await roleApi.create(form)
    }

    if (res.data.success) {
      ElMessage.success(editingRole.value ? '更新成功' : '添加成功')
      showDialog.value = false
      editingRole.value = null
      resetForm()
      loadRoles()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

async function deleteRole(id) {
  try {
    await ElMessageBox.confirm('确定要删除这个角色吗？', '提示', { type: 'warning' })
    const res = await roleApi.delete(id)
    if (res.data.success) {
      ElMessage.success('删除成功')
      loadRoles()
    }
  } catch (e) {
    // Cancelled
  }
}

onMounted(() => {
  loadRoles()
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
</style>

<template>
  <div class="user-manage">
    <el-card class="section-card" shadow="never" v-loading="loading">
      <template #header>
        <div class="card-header">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </div>
      </template>

      <el-table :data="users" stripe style="width: 100%">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column prop="username" label="用户名" width="120" align="center" />
        <el-table-column prop="nickname" label="昵称" width="120" align="center" />
        <el-table-column prop="role" label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 'ADMIN' ? 'danger' : 'info'" size="small">
              {{ row.role === 'ADMIN' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="注册时间" align="center" />
        <el-table-column label="操作" width="250" align="center">
          <template #default="{ row }">
            <el-button type="primary" size="small" text @click="editUser(row)">编辑</el-button>
            <el-button type="warning" size="small" text @click="resetUserPassword(row)">重置密码</el-button>
            <el-button type="danger" size="small" text @click="deleteUser(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- User Dialog -->
    <el-dialog v-model="showDialog" :title="'编辑用户'" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="昵称" required>
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="角色" required>
          <el-select v-model="form.role" placeholder="请选择角色">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="普通用户" value="USER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="danger" @click="saveUser" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '../../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const loading = ref(false)
const saving = ref(false)
const showDialog = ref(false)
const editingUser = ref(null)

const form = reactive({
  username: '',
  nickname: '',
  role: 'USER'
})

async function loadUsers() {
  loading.value = true
  try {
    const res = await userApi.getAll()
    if (res.data.success) {
      users.value = res.data.data || []
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function editUser(user) {
  editingUser.value = user
  Object.assign(form, {
    username: user.username,
    nickname: user.nickname,
    role: user.role
  })
  showDialog.value = true
}

async function saveUser() {
  if (!form.nickname) {
    ElMessage.warning('请输入昵称')
    return
  }

  saving.value = true
  try {
    const res = await userApi.update(editingUser.value.id, {
      nickname: form.nickname,
      role: form.role
    })

    if (res.data.success) {
      ElMessage.success('更新成功')
      showDialog.value = false
      editingUser.value = null
      loadUsers()
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    saving.value = false
  }
}

async function resetUserPassword(user) {
  try {
    await ElMessageBox.confirm(`确定要重置用户 "${user.username}" 的密码为 "123456" 吗？`, '提示', { type: 'warning' })
    const res = await userApi.updatePassword(user.id, '123456')
    if (res.data.success) {
      ElMessage.success('密码已重置为 123456')
    }
  } catch (e) {
    // Cancelled
  }
}

async function deleteUser(id) {
  try {
    await ElMessageBox.confirm('确定要删除这个用户吗？', '提示', { type: 'warning' })
    const res = await userApi.delete(id)
    if (res.data.success) {
      ElMessage.success('删除成功')
      loadUsers()
    }
  } catch (e) {
    // Cancelled
  }
}

onMounted(() => {
  loadUsers()
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
</style>

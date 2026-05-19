<template>
  <div class="login-view">
    <el-card class="login-card">
      <div class="login-header">
        <el-icon size="48" color="#ff6b8a"><User /></el-icon>
        <h2>{{ isRegister ? '注册账号' : '用户登录' }}</h2>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="0" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleSubmit"
          />
        </el-form-item>

        <el-form-item v-if="isRegister" prop="nickname">
          <el-input
            v-model="form.nickname"
            placeholder="请输入昵称（选填）"
            prefix-icon="UserFilled"
            size="large"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="danger"
            size="large"
            class="submit-btn"
            :loading="loading"
            @click="handleSubmit"
          >
            {{ isRegister ? '注册' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <span>{{ isRegister ? '已有账号？' : '没有账号？' }}</span>
        <el-link type="danger" @click="isRegister = !isRegister">
          {{ isRegister ? '去登录' : '去注册' }}
        </el-link>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { authApi } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const isRegister = ref(false)

const form = reactive({
  username: '',
  password: '',
  nickname: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

async function handleSubmit() {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
  } catch {
    return
  }

  loading.value = true
  try {
    let res
    if (isRegister.value) {
      res = await authApi.register({
        username: form.username,
        password: form.password,
        nickname: form.nickname || form.username
      })
    } else {
      res = await authApi.login({
        username: form.username,
        password: form.password
      })
    }

    if (res.data.success) {
      const { token, user } = res.data.data
      authStore.setAuth(token, user)
      ElMessage.success(isRegister.value ? '注册成功！' : '登录成功！')
      router.push('/menu')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '操作失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-view {
  min-height: calc(100vh - 120px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  border-radius: 20px;
  padding: 20px;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header h2 {
  margin-top: 12px;
  color: #333;
  font-size: 24px;
}

.login-form {
  margin-top: 20px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.submit-btn {
  width: 100%;
  border-radius: 12px;
  font-size: 16px;
  height: 48px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #999;
}

.login-tip {
  text-align: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed #eee;
}
</style>

<template>
  <div class="login-view">
    <!-- Decorative Background -->
    <div class="bg-decorations">
      <div class="bubble bubble-1"></div>
      <div class="bubble bubble-2"></div>
      <div class="bubble bubble-3"></div>
      <div class="bubble bubble-4"></div>
    </div>

    <el-card class="login-card" shadow="always">
      <div class="login-header">
        <div class="header-icon">
          <el-icon size="40" color="#fff"><User /></el-icon>
        </div>
        <h2>{{ isRegister ? '注册账号' : '用户登录' }}</h2>
        <p class="header-subtitle">
          {{ isRegister ? '创建一个新账号' : '欢迎回来，请登录' }}
        </p>
      </div>

      <el-form
        :model="form"
        :rules="rules"
        ref="formRef"
        label-width="0"
        class="login-form"
      >
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
  nickname: '',
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3-20个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
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
        nickname: form.nickname || form.username,
      })
    } else {
      res = await authApi.login({
        username: form.username,
        password: form.password,
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
  position: relative;
  overflow: hidden;
}

/* ===== Decorative Background Bubbles ===== */
.bg-decorations {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;
}

.bubble {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
}

.bubble-1 {
  width: 300px;
  height: 300px;
  background: var(--gradient-primary);
  top: -80px;
  right: -60px;
  animation: float 8s ease-in-out infinite;
}

.bubble-2 {
  width: 200px;
  height: 200px;
  background: var(--gradient-warm);
  bottom: -50px;
  left: -40px;
  animation: float 10s ease-in-out infinite 1s;
}

.bubble-3 {
  width: 150px;
  height: 150px;
  background: var(--color-primary-lighter);
  top: 40%;
  left: 10%;
  animation: float 7s ease-in-out infinite 2s;
}

.bubble-4 {
  width: 100px;
  height: 100px;
  background: var(--color-primary);
  bottom: 20%;
  right: 15%;
  animation: float 9s ease-in-out infinite 0.5s;
}

/* ===== Login Card ===== */
.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: var(--radius-xl) !important;
  padding: 28px;
  background: var(--glass-bg) !important;
  backdrop-filter: var(--glass-blur) !important;
  -webkit-backdrop-filter: var(--glass-blur) !important;
  border: var(--glass-border) !important;
  box-shadow: var(--shadow-xl), inset 0 1px 0 rgba(255, 255, 255, 0.5) !important;
  animation: fadeInUp 0.6s ease;
  position: relative;
  z-index: 1;
}

/* ===== Header ===== */
.login-header {
  text-align: center;
  margin-bottom: 28px;
}

.header-icon {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  background: var(--gradient-primary);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 24px rgba(255, 107, 138, 0.35);
  animation: float 4s ease-in-out infinite;
}

.login-header h2 {
  margin-top: 0;
  color: var(--text-primary);
  font-size: 24px;
  font-weight: 700;
}

.header-subtitle {
  color: var(--text-muted);
  font-size: 14px;
  margin-top: 6px;
}

/* ===== Form ===== */
.login-form {
  margin-top: 8px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: var(--radius-md) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04) !important;
  transition: all var(--transition-fast) !important;
  padding: 4px 12px;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 2px 12px rgba(255, 107, 138, 0.15) !important;
}

.login-form :deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 0 0 2px rgba(255, 107, 138, 0.3) !important;
}

/* ===== Submit Button ===== */
.submit-btn {
  width: 100%;
  border-radius: var(--radius-md) !important;
  font-size: 16px;
  font-weight: 600;
  height: 50px;
  letter-spacing: 2px;
  box-shadow: 0 4px 16px rgba(255, 107, 138, 0.35) !important;
  transition: all var(--transition-normal) !important;
}

.submit-btn:hover {
  box-shadow: 0 6px 24px rgba(255, 107, 138, 0.5) !important;
  transform: translateY(-2px);
}

.submit-btn:active {
  transform: translateY(0) scale(0.99);
}

/* ===== Footer ===== */
.login-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: var(--text-muted);
}
</style>

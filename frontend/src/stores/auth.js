import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '../api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')
  const nickname = computed(() => user.value?.nickname || user.value?.username || '')

  function setAuth(tokenVal, userVal) {
    token.value = tokenVal
    user.value = userVal
    localStorage.setItem('token', tokenVal)
    localStorage.setItem('user', JSON.stringify(userVal))
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  async function fetchUser() {
    try {
      const res = await authApi.getMe()
      if (res.data.success) {
        user.value = res.data.data
        localStorage.setItem('user', JSON.stringify(res.data.data))
      }
    } catch (e) {
      logout()
    }
  }

  return {
    token,
    user,
    isLoggedIn,
    isAdmin,
    nickname,
    setAuth,
    logout,
    fetchUser
  }
})

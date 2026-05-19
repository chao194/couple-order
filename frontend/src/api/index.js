import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor to add auth token
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// Response interceptor to handle auth errors
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

// Menu API
export const menuApi = {
  getAll: () => api.get('/menu'),
  getByCategory: (category) => api.get(`/menu/category/${category}`),
  getById: (id) => api.get(`/menu/${id}`),
  create: (data) => api.post('/menu', data),
  update: (id, data) => api.put(`/menu/${id}`, data),
  delete: (id) => api.delete(`/menu/${id}`)
}

// Order API
export const orderApi = {
  getAll: () => api.get('/orders'),
  getById: (id) => api.get(`/orders/${id}`),
  getByNo: (orderNo) => api.get(`/orders/no/${orderNo}`),
  getByStatus: (status) => api.get(`/orders/status/${status}`),
  create: (data) => api.post('/orders', data),
  updateStatus: (id, status) => api.put(`/orders/${id}/status?status=${status}`),
  delete: (id) => api.delete(`/orders/${id}`)
}

// Init API
export const initApi = {
  createTables: () => api.post('/init/tables'),
  seedData: () => api.post('/init/seed')
}

// Category API
export const categoryApi = {
  getAll: () => api.get('/categories'),
  getById: (id) => api.get(`/categories/${id}`),
  create: (data) => api.post('/categories', data),
  update: (id, data) => api.put(`/categories/${id}`, data),
  delete: (id) => api.delete(`/categories/${id}`)
}

// File API
export const fileApi = {
  upload: (formData) => api.post('/files/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 60000
  }),
  getAll: () => api.get('/files'),
  getById: (id) => api.get(`/files/${id}`),
  delete: (id) => api.delete(`/files/${id}`)
}

// Auth API
export const authApi = {
  login: (data) => api.post('/auth/login', data),
  register: (data) => api.post('/auth/register', data),
  getMe: () => api.get('/auth/me')
}

// User API (admin only)
export const userApi = {
  getAll: () => api.get('/users'),
  getById: (id) => api.get(`/users/${id}`),
  update: (id, data) => api.put(`/users/${id}`, data),
  updatePassword: (id, password) => api.put(`/users/${id}/password`, { password }),
  delete: (id) => api.delete(`/users/${id}`)
}

// Role API (admin only)
export const roleApi = {
  getAll: () => api.get('/roles'),
  getById: (id) => api.get(`/roles/${id}`),
  create: (data) => api.post('/roles', data),
  update: (id, data) => api.put(`/roles/${id}`, data),
  delete: (id) => api.delete(`/roles/${id}`)
}

export default api

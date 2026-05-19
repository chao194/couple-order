import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/menu'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue'),
    meta: { public: true }
  },
  {
    path: '/menu',
    name: 'Menu',
    component: () => import('../views/MenuView.vue'),
    meta: { public: true }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('../views/CartView.vue')
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('../views/OrderListView.vue')
  },
  {
    path: '/orders/:id',
    name: 'OrderDetail',
    component: () => import('../views/OrderDetailView.vue')
  },
  {
    path: '/admin',
    component: () => import('../views/AdminView.vue'),
    meta: { requiresAdmin: true },
    children: [
      {
        path: '',
        redirect: '/admin/category'
      },
      {
        path: 'category',
        name: 'AdminCategory',
        component: () => import('../views/admin/CategoryManage.vue')
      },
      {
        path: 'dish',
        name: 'AdminDish',
        component: () => import('../views/admin/DishManage.vue')
      },
      {
        path: 'user',
        name: 'AdminUser',
        component: () => import('../views/admin/UserManage.vue')
      },
      {
        path: 'role',
        name: 'AdminRole',
        component: () => import('../views/admin/RoleManage.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// Navigation guard
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')

  // Public pages
  if (to.meta.public) {
    next()
    return
  }

  // Need login
  if (!token) {
    next('/login')
    return
  }

  // Need admin role
  if (to.meta.requiresAdmin && user?.role !== 'ADMIN') {
    next('/menu')
    return
  }

  next()
})

export default router

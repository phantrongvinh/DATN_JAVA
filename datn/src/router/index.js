import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import { useAuthStore } from '@/stores/useAuthStore.js'
import { storeToRefs } from 'pinia'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
    },
    {
      path: '/oauth2/callback',
      name: 'callbackGoogleLogin',
      component: () => import('../config/RedirectGoogle.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
      meta: {
        guestOnly: true,
      },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
      meta: {
        guestOnly: true,
      },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/Profile.vue'),
      meta: {
        requiresAuth: true,
        roles: ['USER'],
      },
    },
    {
      path: '/product',
      name: 'product',
      component: () => import('../views/Product.vue'),
    },
    {
      path: '/cart',
      name: 'cart',
      component: () => import('../views/Cart.vue'),
    },
    {
      path: '/accesories',
      name: 'accesories',
      component: () => import('../views/Accessory.vue'),
    },
    {
      path: '/productDetail/:productId',
      name: 'productDetail',
      component: () => import('../views/ProductDetail.vue'),
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/Admin.vue'),
      meta: {
        requiresAuth: true,
        roles: ['ADMIN'],
      },
    },
    {
      path: '/activate-success',
      name: 'activate',
      component: () => import('../views/ActivateSuccess.vue'),
    },
    {
      path: '/forgot-password',
      name: 'forgot-password',
      component: () => import('../views/ForgotPassword.vue'),
    },
    {
      path: '/reset-password',
      name: 'reset-password',
      component: () => import('../views/ResetPassword.vue'),
    },
    {
      path: '/admin-dashboard',
      name: 'admindasboard',
      component: () => import('../views/AdminDashboard.vue'),
    },
  ],
})

router.beforeEach((to) => {
  const authStore = useAuthStore()

  const { user } = storeToRefs(authStore)

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    return '/login'
  }

  if (to.meta.guestOnly && authStore.isAuthenticated) {
    return '/'
  }

  if (to.meta.roles) {
    const hasRole = user.value?.roles?.some((r) => to.meta.roles.includes(r))

    if (!hasRole) {
      return '/'
    }
  }
  return
})

export default router

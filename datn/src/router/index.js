import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import { useAuthStore } from '@/stores/useAuthStore.js'
import { storeToRefs } from 'pinia'
import { useCartStore } from '@/stores/useCartStore.js'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // ─── No Layout ───────────────────────────────────────────
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/Login.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
      meta: { guestOnly: true },
    },
    {
      path: '/oauth2/callback',
      name: 'callbackGoogleLogin',
      component: () => import('../config/RedirectGoogle.vue'),
    },
    {
      path: '/activate-success',
      name: 'activate',
      component: () => import('../views/ActivateSuccess.vue'),
    },

    // ─── Guest Layout ─────────────────────────────────────────
    {
      path: '/',
      component: () => import('../views/layout/GuestLayout.vue'),
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/Home.vue'),
        },
        {
          path: 'products',
          name: 'products',
          component: () => import('../views/Product.vue'),
        },
        {
          path: 'accesories',
          name: 'accesories',
          component: () => import('../views/Accessory.vue'),
        },
        {
          path: 'productDetail/:productId',
          name: 'productDetail',
          component: () => import('../views/ProductDetail.vue'),
        },
        {
          path: 'cart',
          name: 'cart',
          component: () => import('../views/Cart.vue'),
        },
        {
          path: 'payment/vnpay-return',
          name: 'VNPayReturn',
          component: () => import('../views/PaymentReturn.vue'),
        },
        {
          path: 'checkout',
          name: 'checkout',
          component: () => import('../views/CheckOut.vue'),
          meta: { requiresAuth: true },
          beforeEnter: (to, from, next) => {
            const cartStore = useCartStore()
            if (!cartStore.items || cartStore.items.length === 0) {
              next('/cart')
            } else {
              next()
            }
          },
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

        // ─── Account Layout (cần đăng nhập) ──────────────────
        {
          path: 'profile',
          component: () => import('../views/layout/AccountLayout.vue'),
          meta: { requiresAuth: true, roles: ['USER', 'ADMIN'] },
          children: [
            {
              path: '',
              name: 'profile',
              component: () => import('../views/Profile.vue'),
            },
            {
              path: 'orders',
              name: 'my-orders',
              component: () => import('../views/MyOrderPage.vue'),
            },
          ],
        },
        {
          path: 'orders/:id',
          name: 'orderDetail',
          component: () => import('../views/OrderDetail.vue'),
          meta: { requiresAuth: true },
        },
      ],
    },

    // ─── Admin Layout ─────────────────────────────────────────
    {
      path: '/admin',
      component: () => import('../views/layout/AdminLayout.vue'),
      meta: { requiresAuth: true, roles: ['ADMIN'] },
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('../views/Dashboard.vue'),
        },
        {
          path: 'products',
          name: 'product-dashboard',
          component: () => import('../views/ProductDashboard.vue'),
        },
        {
          path: 'promotion',
          name: 'promotion',
          component: () => import('../views/Promotion.vue'),
        },
        {
          path: 'time-promotion',
          name: 'time-promotion',
          component: () => import('../views/TimePromotion.vue'),
        },
        {
          path: 'orders',
          name: 'orders',
          component: () => import('../views/OrderDashboard.vue'),
        },
        {
          path: 'landing',
          name: 'landing',
          component: () => import('../views/AdminLandingPage.vue'),
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('../views/UserDashboard.vue'),
        },
        // Thêm các route admin khác ở đây
        // { path: 'orders', name: 'admin-orders', component: () => import('../views/admin/Orders.vue') },
        // { path: 'products', name: 'admin-products', component: () => import('../views/admin/Products.vue') },
      ],
    },

    // ─── Test ─────────────────────────────────────────────────
    {
      path: '/test',
      name: 'test',
      component: () => import('../views/Test.vue'),
    },

    // ─── 404 ──────────────────────────────────────────────────
    // {
    //   path: '/:pathMatch(.*)*',
    //   name: 'not-found',
    //   component: () => import('../views/NotFound.vue'),
    // },
  ],
  scrollBehavior(to, from) {
    if (to.path !== from.path) {
      return { top: 0 }
    }
  },
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

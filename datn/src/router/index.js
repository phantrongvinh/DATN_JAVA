import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import { useAuthStore } from '@/stores/useAuthStore.js'
import { storeToRefs } from 'pinia'
import { useCartStore } from '@/stores/useCartStore.js'

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
      component: () => import('../views/layout/AccountLayout.vue'),
      children: [
        {
          path: '',
          component: () => import('../views/Profile.vue'),
        },
        {
          path: 'orders',
          component: () => import('../views/MyOrderPage.vue'),
        },
        // {
        //   path: 'wishlist',
        //   component: WishlistPage,
        // },
        // {
        //   path: 'addresses',
        //   component: AddressPage,
        // },
      ],
      meta: {
        requiresAuth: true,
        roles: ['USER', 'ADMIN'],
      },
    },
    {
      path: '/orders/:id',
      name: 'orderDetaik',
      component: () => import('../views/OrderDetail.vue'),
      meta: {
        requiresAuth: true,
      },
    },
    {
      path: '/products',
      name: 'products',
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
      path: '/checkout',
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
      path: '/payment/vnpay-return',
      name: 'VNPayReturn',
      component: () => import('@/views/PaymentReturn.vue'),
      meta: { requiresAuth: false },
    },
    {
      path: '/test',
      name: 'test',
      component: () => import('../views/Test.vue'),
    },
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

import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'

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
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/Register.vue'),
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/Profile.vue'),
    },
    {
      path: '/:audience',
      name: 'audience',
      component: () => import('../views/Audience.vue'),
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
      path: '/productDetail',
      name: 'productDetail',
      component: () => import('../views/ProductDetail.vue'),
    },
    // {
    //   path: '/register',
    //   name: 'register',
    //   component: () => import('../views/Register.vue'),
    // },
  ],
})

export default router

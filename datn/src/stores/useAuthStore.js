import authAPI from '@/api/authAPI'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { useCartStore } from './useCartStore'
import { useNotificationStore } from './useNotificationStore'
import { useWishlistStore } from './useWishlistStore'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(null)
  const loadding = ref(false)
  const error = ref(null)
  const resend = ref(false)
  const message = ref('')
  const isCheckingAuth = ref(false)

  const isAdmin = computed(() => user.value?.roles?.some((r) => r === 'ADMIN') ?? false)
  const isAuthenticated = computed(() => !!token.value)

  const cartStore = useCartStore()
  const wishlistStore = useWishlistStore()
  const notification = useNotificationStore()

  async function login(data) {
    loadding.value = true
    error.value = null
    try {
      const res = await authAPI.login(data)
      token.value = res.token
      resend.value = false
      localStorage.setItem('token', res.token)

      await me()
      await cartStore.mergeCartToServer()
      cartStore.clearLocal()
      await cartStore.fetchCart()
      await wishlistStore.fetchIds()
    } catch (err) {
      error.value = err.response?.data?.message
      if (error.value === 'Tài khoản chưa được kích hoạt, hãy gửi lại mã kích hoạt') {
        resend.value = true
      }
      throw err
    } finally {
      loadding.value = false
    }
  }

  async function me() {
    if (!isAuthenticated.value) return
    try {
      user.value = await authAPI.me()
    } catch (err) {
      forceLogout()
    }
  }

  async function checkAuth() {
    if (!token.value) {
      user.value = null
      return false
    }

    isCheckingAuth.value = true
    try {
      user.value = await authAPI.me()
      await wishlistStore.fetchIds()
      return true
    } catch (err) {
      forceLogout()
      return false
    } finally {
      isCheckingAuth.value = false
    }
  }

  function forceLogout() {
    user.value = null
    token.value = null
    wishlistStore.clear()
    localStorage.removeItem('token')
  }

  async function register(data) {
    loadding.value = true
    error.value = null
    try {
      const res = await authAPI.register(data)
      message.value = res.message
    } catch (err) {
      error.value = err.response?.data?.message
      throw err
    } finally {
      loadding.value = false
    }
  }

  async function logout() {
    try {
      await authAPI.logout()
      wishlistStore.ids.values = []
      notification.notify('Đã đăng xuất', 'success')
    } catch (err) {
      error.value = err
      return { success: false, errorMessages: err.message }
    } finally {
      forceLogout()
    }
  }

  async function resendMail(email) {
    loadding.value = true
    error.value = null
    try {
      const res = await authAPI.resend(email)
      message.value = res.message
      resend.value = ''
    } catch (err) {
      error.value = err
      return { success: false, errorMessages: err.message }
    } finally {
      loadding.value = false
    }
  }

  async function forgotPassword(email) {
    loadding.value = true
    error.value = null
    try {
      const res = await authAPI.forgotPassword(email)
      message.value = res
      resend.value = false
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: err.message }
    } finally {
      loadding.value = false
    }
  }

  async function resetPassword(token, password) {
    loadding.value = true
    error.value = null
    try {
      const res = await authAPI.resetPassword(token, password)
      message.value = res
      resend.value = false
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: err.message }
    } finally {
      loadding.value = false
    }
  }

  async function updateProfile(form) {
    loadding.value = true
    error.value = null
    try {
      const res = await authAPI.updateProfile(form)
      await me()
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: err.message }
    } finally {
      loadding.value = false
    }
  }

  function clearMessages() {
    error.value = null
    message.value = null
  }

  const clear = () => {
    ids.value = []
    wishlists.value = []
  }

  return {
    user,
    token,
    isAdmin,
    loadding,
    error,
    resend,
    message,
    isCheckingAuth,
    isAuthenticated,
    login,
    logout,
    me,
    checkAuth,
    forceLogout,
    resendMail,
    register,
    forgotPassword,
    resetPassword,
    clearMessages,
    updateProfile,
    clear,
  }
})

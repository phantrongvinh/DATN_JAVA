import authAPI from '@/api/authAPI'
import { defineStore } from 'pinia'
import { computed, ref, watch } from 'vue'
import { useCartStore } from './useCartStore'

export const useAuthStore = defineStore('auth', () => {
  // states
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(null)
  const loadding = ref(false)
  const error = ref(null)
  const resend = ref(false)
  const message = ref('')

  const isAuthenticated = computed(() => !!token.value)

  const cartStore = useCartStore()

  // actions
  async function login(data) {
    loadding.value = true
    error.value = null

    try {
      const res = await authAPI.login(data)

      token.value = res.token
      resend.value = false

      localStorage.setItem('token', res.token)

      await me()
      console.log(user.value)

      await cartStore.mergeCartToServer()
      cartStore.clearLocal()
      await cartStore.fetchCart()
    } catch (err) {
      error.value = err.response?.data?.message
      if (error.value === 'Account not activated') {
        resend.value = true
      }
      throw err
    } finally {
      loadding.value = false
    }
  }

  async function me() {
    if (isAuthenticated.value) {
      user.value = await authAPI.me()
    }
  }

  async function register(data) {
    loadding.value = true
    error.value = null
    try {
      const res = await authAPI.register(data)
      message.value = res
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
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      user.value = null
      token.value = null
      localStorage.removeItem('token')
    }
  }

  async function resendMail(email) {
    loadding.value = true
    error.value = null
    try {
      const res = await authAPI.resend(email)
      message.value = res.message
      resend.value = false
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
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
  function clearMessages() {
    error.value = null
    message.value = null
  }

  return {
    user,
    loadding,
    error,
    login,
    logout,
    isAuthenticated,
    resend,
    resendMail,
    message,
    register,
    me,
    forgotPassword,
    resetPassword,
    clearMessages,
  }
})

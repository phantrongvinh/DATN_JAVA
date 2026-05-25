import authAPI from '@/api/authAPI'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { errorMessages } from 'vue/compiler-sfc'

export const useAuthStore = defineStore('auth', () => {
  // states
  const token = ref(localStorage.getItem('token') || null)
  const user = ref(null)
  const loadding = ref(false)
  const error = ref(null)

  const isAuthenticated = computed(() => !!token.value)

  // actions
  async function login(data) {
    loadding.value = true
    error.value = null

    try {
      const res = await authAPI.login(data)
      token.value = res.token
      localStorage.setItem('token', res.token)
      user.value = res
      return { success: true }
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }
  return { user, loadding, error, login, logout, isAuthenticated }
})

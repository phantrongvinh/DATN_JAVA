import adminAPI from '@/api/adminAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const users = ref([])
  const loading = ref(false)
  const error = ref(null)
  const message = ref(null)

  const size = ref(5)
  const totalElements = ref(0)
  const totalPages = ref(0)
  const currentPage = ref(1)

  const fetchAllUsers = async (params) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.fetchAlluser(params)
      users.value = res.content
      totalPages.value = res.page.totalPages
      totalElements.value = res.page.totalElements
      currentPage.value = res.page.number + 1
      size.value = res.page.size
    } catch (err) {
      error.value = err.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  const softDeleteUser = async (id) => {
    loading.value = true
    try {
      await adminAPI.deleteUser(id)
    } catch (err) {
      error.value = err.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  const restoreUser = async (id) => {
    loading.value = true
    try {
      await adminAPI.restoreUser(id)
    } catch (err) {
      error.value = err.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    message,
    users,
    size,
    totalElements,
    totalPages,
    currentPage,
    fetchAllUsers,
    softDeleteUser,
    restoreUser,
  }
})

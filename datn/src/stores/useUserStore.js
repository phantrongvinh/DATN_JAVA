import adminAPI from '@/api/adminAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  //states
  const users = ref([])
  const loading = ref(false)
  const error = ref(null)
  const message = ref(null)

  const size = ref(5)
  const totalElements = ref(0)
  const totalPages = ref(0)
  const page = ref(1)
  const currentPage = ref(1)

  //action
  async function fetchAllUsers(params) {
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
      return { success: false, errorMessages: error.message }
    } finally {
      loading.value = false
    }
  }
  return {
    loading,
    error,
    users,
    size,
    page,
    totalElements,
    totalPages,
    page,
    currentPage,
    fetchAllUsers,
  }
})

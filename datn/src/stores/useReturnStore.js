import adminAPI from '@/api/adminAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useReturnStore = defineStore('return', () => {
  const returns = ref([])
  const loading = ref(false)
  const error = ref(null)
  const page = ref(1)
  const size = ref(5)
  const totalPages = ref(0)
  const totalElements = ref(0)
  const currentPage = ref(1)

  const fetchReturns = async (status) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.fetchReturns(page.value, size.value, status)
      returns.value = res.content
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loading.value = false
    }
  }

  const resolveReturn = async (returnId, action, reason) => {
    loading.value = true
    error.value = null
    try {
      await adminAPI.resolveReturn(returnId, action, reason)
      return { success: true }
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loading.value = false
    }
  }

  const manualComplete = async (id) => {
    loading.value = true
    error.value = null
    try {
      await adminAPI.manualComplete(id)
      return { success: true }
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loading.value = false
    }
  }

  return {
    returns,
    loading,
    error,
    page,
    size,
    totalPages,
    totalElements,
    currentPage,
    fetchReturns,
    resolveReturn,
    manualComplete,
  }
})

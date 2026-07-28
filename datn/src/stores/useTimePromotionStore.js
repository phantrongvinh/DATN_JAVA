import adminAPI from '@/api/adminAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useTimePromotionStore = defineStore('time-promotion', () => {
  // state
  const loading = ref(false)
  const error = ref(null)
  const message = ref(null)

  const timePromotions = ref([])
  const size = ref(5)
  const page = ref(1)
  const totalPages = ref(0)
  const totalElements = ref(0)
  const currentPage = ref(1)

  const keyword = ref('')
  const activeFilter = ref(null)

  // action
  // lấy tất cả timepromotion
  const fetchAllTimePromotion = async ({
    newPage = page.value,
    newSize = size.value,
    search = keyword.value,
    isActive = activeFilter.value,
  }) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.fetchAllTimePromotion({
        page: newPage,
        size: newSize,
        search: search || undefined,
        isActive: isActive === null ? undefined : isActive,
      })

      timePromotions.value = res.content

      totalPages.value = res.page.totalPages
      totalElements.value = res.page.totalElements
      page.value = res.page.number + 1
      size.value = res.page.size
      currentPage.value = newPage
    } catch (err) {
      error.value = err?.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  const resetFilters = () => {
    keyword.value = ''
    activeFilter.value = null
    return fetchAllTimePromotion({ newPage: 1 })
  }

  const createTimePromotion = async (data) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.createTimePromotion(data)
      message.value = res
    } catch (err) {
      error.value = err?.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateTimePromotion = async (id, data) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.updateTimePromotion(id, data)
      message.value = res
    } catch (err) {
      error.value = err?.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  const toggleTimePromotion = async (id) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.toggleTimePromotion(id)
      message.value = res
    } catch (err) {
      error.value = err?.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  const deleteTimePromotion = async (id) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.deleteTimePromotion(id)
      message.value = res
    } catch (err) {
      error.value = err?.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    message,
    timePromotions,
    size,
    page,
    totalPages,
    totalElements,
    currentPage,
    keyword,
    activeFilter,
    fetchAllTimePromotion,
    resetFilters,
    createTimePromotion,
    updateTimePromotion,
    toggleTimePromotion,
    deleteTimePromotion,
  }
})

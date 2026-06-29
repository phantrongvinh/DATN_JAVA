import adminAPI from '@/api/adminAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const usePromotionStore = defineStore('promotion', () => {
  // state
  const loading = ref(false)
  const error = ref(null)

  const activePromotions = ref([])

  const promotions = ref([])
  const size = ref(10)
  const page = ref(1)
  const totalPages = ref(0)
  const totalElements = ref(0)
  const currentPage = ref(1)

  //action

  // lấy các promotion còn active
  const fetchActivePromotion = async () => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.fetchAllActivePromotion()
      activePromotions.value = res
    } catch (err) {
      error.value = err.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  // lấy tất promotions
  const fetchAllPromotion = async ({ newPage = page.value, newSize = size.value }) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.fetchAllPromotion({ page: newPage, size: newSize })

      promotions.value = res.content

      totalPages.value = res.page.totalPages
      totalElements.value = res.page.totalElements
      page.value = res.page.number + 1
      size.value = res.page.size
      currentPage.value = newPage
    } catch (err) {
      error.value = err.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  // add promotion to product
  const addPromotionToProduct = async (promotionId, productIds) => {
    loading.value = true
    error.value = null
    try {
      await adminAPI.addPromotionToProduct(promotionId, productIds)
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
    activePromotions,
    promotions,
    page,
    size,
    totalElements,
    totalPages,
    currentPage,
    fetchActivePromotion,
    addPromotionToProduct,
    fetchAllPromotion,
  }
})

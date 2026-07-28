import productAPI from '@/api/productAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useReviewStore = defineStore('review', () => {
  const summary = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const submitting = ref(false)

  const fetchReviews = async (productId) => {
    loading.value = true
    error.value = null
    try {
      summary.value = await productAPI.fetchReviews(productId)
    } catch (err) {
      error.value = err.response?.data?.message
    } finally {
      loading.value = false
    }
  }

  const createReview = async (productId, data) => {
    submitting.value = true
    error.value = null
    try {
      await productAPI.createReview(productId, data)
      await fetchReviews(productId)
      return { success: true }
    } catch (err) {
      error.value = err.response?.data?.message
      throw err
    } finally {
      submitting.value = false
    }
  }

  return { summary, loading, error, submitting, fetchReviews, createReview }
})

import productAPI from '@/api/productAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useProductStore = defineStore('product', () => {
  // states
  const spotlightProducts = ref([])
  const loadding = ref(false)
  const error = ref(null)

  // actions
  async function fetchSpotlightProducts() {
    loadding.value = true
    error.value = null

    try {
      spotlightProducts.value = await productAPI.fetchSpotlightProducts()
      return { success: true }
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }
  return { spotlightProducts, loadding, error, fetchSpotlightProducts }
})

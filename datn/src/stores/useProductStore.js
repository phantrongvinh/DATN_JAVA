import productAPI from '@/api/productAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useProductStore = defineStore('product', () => {
  // states
  const spotlightProducts = ref([])
  const filterProducts = ref([])
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

  async function fetchFilterProducts(data) {
    loadding.value = true
    error.value = null

    try {
      filterProducts.value = await productAPI.fetchFilterProducts(data)
      return { success: true }
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }
  return {
    spotlightProducts,
    filterProducts,
    loadding,
    error,
    fetchSpotlightProducts,
    fetchFilterProducts,
  }
})

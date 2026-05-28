import aminAPI from '@/api/adminAPI'
import productAPI from '@/api/productAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useProductStore = defineStore('product', () => {
  // states
  const spotlightProducts = ref([])
  const filterProducts = ref([])
  const loadding = ref(false)
  const error = ref(null)
  const productOverview = ref([])

  // actions
  async function fetchSpotlightProducts() {
    loadding.value = true
    error.value = null

    try {
      const res = await productAPI.fetchSpotlightProducts()
      spotlightProducts.value = res
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
      const res = await productAPI.fetchFilterProducts(data)
      filterProducts.value = res
      return { success: true }
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  async function fetchProductOVerview() {
    loadding.value = true
    error.value = null

    try {
      const res = await aminAPI.fetchProductOverview()
      productOverview.value = res
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
    productOverview,
    fetchSpotlightProducts,
    fetchFilterProducts,
    fetchProductOVerview,
  }
})

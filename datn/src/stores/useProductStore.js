import adminAPI from '@/api/adminAPI'
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

  const products = ref([])
  const page = ref(1)
  const size = ref(5)
  const totalPages = ref(0)
  const totalElements = ref(0)

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
      const res = await adminAPI.fetchProductOverview()
      productOverview.value = res
      return { success: true }
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  async function fetchAllProducts({ newPage = page.value, newSize = size.value }) {
    loadding.value = true
    error.value = null

    try {
      const res = await adminAPI.fetchAllProducts({ page: newPage, size: newSize })
      products.value = res.content
      totalPages.value = res.totalPages
      totalElements.value = res.totalElements
      page.value = res.number + 1
      size.value = res.size
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  async function deleteProductById(id) {
    loadding.value = true
    error.value = null

    try {
      const res = await adminAPI.deleteProductById(id)

      await fetchAllProducts({ newPage: page.value, newSize: size.value })
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }
  async function updateProductById(data) {
    loadding.value = true
    error.value = null

    try {
      console.log(data)

      const res = await adminAPI.updateProductById(data)

      await fetchAllProducts({ newPage: page.value, newSize: size.value })
    } catch (error) {
      error.value = error
      throw error
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
    products,
    page,
    size,
    totalPages,
    totalElements,
    fetchSpotlightProducts,
    fetchFilterProducts,
    fetchProductOVerview,
    fetchAllProducts,
    deleteProductById,
    updateProductById,
  }
})

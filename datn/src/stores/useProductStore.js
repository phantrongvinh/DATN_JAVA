import adminAPI from '@/api/adminAPI'
import productAPI from '@/api/productAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useProductStore = defineStore('product', () => {
  // states

  const loadding = ref(false)
  const error = ref(null)
  const message = ref('')

  const productOverview = ref([])
  const spotlightProducts = ref([])
  const filterProducts = ref([])
  const products = ref([])
  const product = ref()
  const productOnSale = ref([])

  const page = ref(1)
  const size = ref(5)
  const totalPages = ref(0)
  const totalElements = ref(0)

  // actions
  // fetch top 10 product mới nhất hiện ở home
  async function fetchSpotlightProducts() {
    loadding.value = true
    error.value = null

    try {
      const res = await productAPI.fetchSpotlightProducts()
      spotlightProducts.value = res

      return { success: true }
    } catch (error) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  // fetch product on sale ở home
  async function fetchProductOnSale() {
    loadding.value = true
    error.value = null

    try {
      const res = await productAPI.fetchProductOnSale()
      productOnSale.value = res

      return { success: true }
    } catch (error) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  // fetch product list theo filter điều kiện ở trang product
  async function fetchFilterProducts(data) {
    loadding.value = true
    error.value = null

    try {
      const res = await productAPI.fetchFilterProducts(data)

      filterProducts.value = res.content

      return { success: true }
    } catch (error) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  // fetch 5 product view ở thống kê của admin
  async function fetchProductOVerview() {
    loadding.value = true
    error.value = null

    try {
      const res = await adminAPI.fetchProductOverview()
      productOverview.value = res
      return { success: true }
    } catch (error) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  // fetch tất cả product kể cả bị vô hiệu hóa ở trang admin để quản lý
  async function fetchAllProducts({
    newPage = page.value,
    newSize = size.value,
    audienceIds,
    brandIds,
    categoryIds,
  }) {
    loadding.value = true
    error.value = null
    try {
      const res = await adminAPI.fetchAllProducts({
        page: newPage,
        size: newSize,
        audienceIds,
        brandIds,
        categoryIds,
      })

      message.value = res.message

      products.value = Array.isArray(res.content) ? res.content : []

      totalPages.value = res.page.totalPages
      totalElements.value = res.page.totalElements
      page.value = res.page.number + 1
      size.value = res.page.size
    } catch (error) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  // vô hiện hóa hoặc khôi phục product theo id
  async function deleteProductById(id) {
    loadding.value = true
    error.value = null

    try {
      const res = await adminAPI.deleteProductById(id)

      await fetchAllProducts({ newPage: page.value, newSize: size.value })
    } catch (error) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  // cập nhật product theo id
  async function updateProductById(data) {
    loadding.value = true
    error.value = null

    try {
      const res = await adminAPI.updateProductById(data)

      await fetchAllProducts({ newPage: page.value, newSize: size.value })
    } catch (error) {
      error.value = err.response?.data?.message
      throw error
    } finally {
      loadding.value = false
    }
  }

  // fetch product theo id có list images và list variants
  async function fetchProductByID(id) {
    loadding.value = true
    error.value = null

    try {
      const res = await productAPI.fetchProductById(id)
      product.value = res
    } catch (error) {
      error.value = err.response?.data?.message
      throw error
    } finally {
      loadding.value = false
    }
  }

  return {
    loadding,
    error,
    spotlightProducts,
    productOnSale,
    filterProducts,
    productOverview,
    products,
    product,
    page,
    size,
    message,
    totalPages,
    totalElements,
    fetchSpotlightProducts,
    fetchFilterProducts,
    fetchProductOVerview,
    fetchAllProducts,
    deleteProductById,
    updateProductById,
    fetchProductByID,
    fetchProductOnSale,
  }
})

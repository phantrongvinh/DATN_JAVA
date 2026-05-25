import brandAPI from '@/api/brandAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { errorMessages } from 'vue/compiler-sfc'

export const useBrandStore = defineStore('brand', () => {
  // states
  const brands = ref([])
  const loading = ref(false)
  const error = ref(null)

  // actions
  async function fetchBrand() {
    loading.value = true
    error.value = false

    try {
      brands.value = await brandAPI.fetchBrand()
      return { success: true }
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loading.value = false
    }
  }
  return { brands, loading, error, fetchBrand }
})

import categoryAPI from '@/api/categoryAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { errorMessages } from 'vue/compiler-sfc'

export const useCategoryStore = defineStore('category', () => {
  // states
  const categories = ref([])
  const accessories = ref([])
  const loadding = ref(false)
  const error = ref(null)

  // actions
  async function fetchCategory() {
    loadding.value = true
    error.value = false

    try {
      categories.value = await categoryAPI.fetchCategory()
      return { success: true }
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  async function fetchAccessory() {
    loadding.value = true
    error.value = false

    try {
      accessories.value = await categoryAPI.fetchAccessory()
      return { success: true }
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loadding.value = false
    }
  }

  return { categories, accessories, loadding, error, fetchCategory, fetchAccessory }
})

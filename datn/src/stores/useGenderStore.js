import genderAPI from '@/api/genderAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useGenderStore = defineStore('gender', () => {
  // các state
  const genders = ref([])
  const loading = ref(false)
  const error = ref(null)

  // actions / hành động gọi api
  async function getAllGender() {
    loading.value = true
    error.value = null

    try {
      genders.value = await genderAPI.getAllGender()
      return { success: true }
    } catch (error) {
      error.value = error
      return { success: false, error: error.message }
    } finally {
      loading.value = false
    }
  }

  return { genders, loading, error, getAllGender }
})

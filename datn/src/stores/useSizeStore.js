import sizeAPI from '@/api/sizeAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useSizeStore = defineStore('size', () => {
  // state
  const loading = ref(false)
  const error = ref(null)
  const message = ref('')

  const sizes = ref([])

  // action
  const fetchSize = async () => {
    loading.value = true
    error.value = null
    try {
      const res = await sizeAPI.fetchAllSize()

      sizes.value = res
    } catch (err) {
      error.value = err.reponse?.data?.message
      return { success: false, errorMessages: err.message }
    } finally {
      loading.value = false
    }
  }

  return { loading, error, message, sizes, fetchSize }
})

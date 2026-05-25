import audienceAPI from '@/api/audienceAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { errorMessages } from 'vue/compiler-sfc'

export const useAudienceStore = defineStore('audience', () => {
  // các state
  const audiences = ref([])
  const loading = ref(false)
  const error = ref(null)

  // actions / hành động gọi api
  async function fetchAudiences() {
    loading.value = true
    error.value = null

    try {
      audiences.value = await audienceAPI.fetchAudiences()
      return { success: true }
    } catch (error) {
      error.value = error
      return { success: false, errorMessages: error.message }
    } finally {
      loading.value = false
    }
  }

  return { audiences, loading, error, fetchAudiences }
})

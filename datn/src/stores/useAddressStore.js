import addressAPI from '@/api/addressAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAddressStore = defineStore('address', () => {
  // state
  const loading = ref(false)
  const error = ref(null)
  const message = ref('')

  const addresses = ref([])

  // action
  const fetchAddresses = async () => {
    loading.value = true
    error.value = null
    try {
      const res = await addressAPI.fetchAddress()
      addresses.value = res
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loading.value = false
    }
  }

  const addAddress = async (data) => {
    try {
      const res = await addressAPI.addAddress(data)
      message.value = res.message
      await fetchAddresses()
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loading.value = false
    }
  }

  const deleteAddress = async (id) => {
    try {
      const res = await addressAPI.deleteAddress(id)
      message.value = res.message
      await fetchAddresses()
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loading.value = false
    }
  }

  const setPrimary = async (id) => {
    try {
      const res = await addressAPI.setPrimary(id)
      message.value = res.message
      await fetchAddresses()
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: error.message }
    } finally {
      loading.value = false
    }
  }

  return { addresses, loading, fetchAddresses, addAddress, deleteAddress, setPrimary }
})

import orderAPI from '@/api/orderAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { errorMessages } from 'vue/compiler-sfc'

export const useOrderStore = defineStore('order', () => {
  // state
  const loading = ref(false)
  const error = ref(null)
  const myOrders = ref([])
  const order = ref(null)

  //action
  const fetchMyOrder = async () => {
    loading.value = true
    error.value = null

    try {
      const res = await orderAPI.fetchMyOrder()

      myOrders.value = res
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: err.response?.data?.message }
    } finally {
      loading.value = false
    }
  }

  const fetchOrderById = async(id) =>{
    loading.value = true
    error.value = null

    try {
      const res = await orderAPI.fetchOrderById(id)

      order.value = res
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: err.response?.data?.message }
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    myOrders,
    order,
    fetchMyOrder,
    fetchOrderById
  }
})

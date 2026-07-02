import adminAPI from '@/api/adminAPI'
import orderAPI from '@/api/orderAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useOrderStore = defineStore('order', () => {
  // state
  const loading = ref(false)
  const error = ref(null)
  const myOrders = ref([])
  const order = ref(null)
  const message = ref(null)

  const orders = ref([])
  const totalPages = ref(0)
  const totalElements = ref(0)
  const currentPage = ref(1)
  const size = ref(5)

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

  const fetchOrderById = async (id) => {
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

  const cancelOrder = async (id) => {
    loading.value = true
    error.value = null
    try {
      const res = await orderAPI.cancelOrder(id)
      message.value = res.message
      await fetchMyOrder()
    } catch (err) {
      error.value = err.response?.data?.message
    } finally {
      loading.value = false
    }
  }

  // fetch order for admin
  const fetchAllOrder = async (params) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.fetchAllOrders(params)

      orders.value = res.content
      totalPages.value = res.totalPages
      totalElements.value = res.totalElements
      currentPage.value = res.page
      size.value = res.size
    } catch (err) {
      error.value = err.response?.data?.message
    } finally {
      loading.value = false
    }
  }

  // update status order
  const updateStatusOrder = async (id, status) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.updateStatusOrder(id, status)
      message.value = res
    } catch (err) {
      error.value = err.response?.data?.message
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    myOrders,
    order,
    orders,
    totalPages,
    totalElements,
    currentPage,
    size,
    fetchMyOrder,
    fetchOrderById,
    cancelOrder,
    fetchAllOrder,
    updateStatusOrder,
  }
})

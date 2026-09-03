import { defineStore } from 'pinia'
import { ref } from 'vue'
import adminAPI from '@/api/adminAPI'
import shippingAPI from '@/api/shippingAPI'

export const useShippingSimulatorStore = defineStore('shippingSimulator', () => {
  const orders = ref([])
  const orderLoading = ref(false)
  const orderError = ref(null)

  const orderFilter = ref({
    status: '',
    orderId: '',
  })

  const orderPage = ref(1)
  const orderSize = ref(10)
  const orderTotalPages = ref(0)

  const fetchOrders = async () => {
    orderLoading.value = true

    try {
      orderLoading.value = true

      const params = {
        page: orderPage.value,
        size: orderSize.value,
      }

      if (orderFilter.value.status) {
        params.status = orderFilter.value.status
      }

      if (orderFilter.value.orderId) {
        params.search = orderFilter.value.orderId
      }

      const response = await adminAPI.fetchAllOrders(params)

      const content = response.content || []

      orders.value = content.filter((order) =>
        ['CONFIRMED', 'PICKED', 'SHIPPING'].includes(order.status),
      )
    } catch (error) {
      orderError.value = error
      throw error
    } finally {
      orderLoading.value = false
    }
  }

  const updateStatus = async (orderId, action) => {
    try {
      await shippingAPI.updateOrderStatus(orderId, action)

      await fetchOrders()
    } catch (err) {
      throw err
    }
  }

  const resetFilter = async () => {
    orderFilter.value = {
      status: '',
      search: '',
    }

    await fetchOrders()
  }

  // returns
  const returns = ref([])
  const returnLoading = ref(false)
  const returnError = ref(null)

  const returnPage = ref(1)
  const returnSize = ref(10)
  const returnTotalPages = ref(0)
  const fetchReturns = async (status = null) => {
    try {
      returnLoading.value = true
      returnError.value = null

      const response = await adminAPI.fetchReturns(returnPage.value, returnSize.value, status)

      returns.value = (response.content || []).filter((item) =>
        ['APPROVED', 'PICKED', 'DELIVERING'].includes(item.status),
      )

      returnPage.value = response.page.number + 1
      returnTotalPages.value = response.page.totalPages
    } catch (err) {
      returnError.value = err
      throw err
    } finally {
      returnLoading.value = false
    }
  }

  const updateReturnStatus = async (returnId, action) => {
    try {
      await shippingAPI.updateReturnStatus(returnId, action)
      await fetchReturns()
    } catch (err) {
      returnError.value = err
      throw err
    }
  }

  return {
    // Orders
    orders,
    orderLoading,
    orderError,
    orderFilter,
    orderPage,
    orderSize,
    orderTotalPages,

    fetchOrders,
    updateStatus,

    // Returns
    returns,
    returnLoading,
    returnError,
    returnPage,
    returnSize,
    returnTotalPages,

    fetchReturns,
    updateReturnStatus,

    resetFilter,
  }
})

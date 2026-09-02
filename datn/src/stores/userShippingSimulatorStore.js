import { defineStore } from 'pinia'
import { ref } from 'vue'
import adminAPI from '@/api/adminAPI'
import shippingAPI from '@/api/shippingAPI'

export const useShippingSimulatorStore = defineStore('shippingSimulator', () => {
  const orders = ref([])
  const orderLoading = ref(false)

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
      console.log(error)
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
      console.log(err)
    }
  }

  const resetFilter = async () => {
    orderFilter.value = {
      status: '',
      search: '',
    }

    await fetchOrders()
  }

  const returns = ref([])
  const returnLoading = ref(false)

  const returnFilter = ref({
    status: '',
  })

  const returnPage = ref(1)
  const returnSize = ref(10)
  const returnTotalPages = ref(0)
  const fetchReturns = async (page = 1) => {
    try {
      returnLoading.value = true

      const params = {
        page: returnPage.value,
        size: returnSize.value,
      }

      if (returnFilter.value.status) {
        params.status = returnFilter.value.status
      }

      const response = await adminAPI.fetchReturns(params.page, params.size, params.status)
      console.log(response)

      returns.value = (response.content || []).filter((item) =>
        ['APPROVED', 'PICKED', 'DELIVERING'].includes(item.status),
      )

      returnPage.value = response.number + 1
      returnTotalPages.value = response.totalPages
    } finally {
      returnLoading.value = false
    }
  }

  return {
    // Orders
    orders,
    orderLoading,
    orderFilter,
    orderPage,
    orderSize,
    orderTotalPages,

    fetchOrders,
    updateStatus,

    // Returns
    returns,
    returnLoading,
    returnFilter,
    returnPage,
    returnSize,
    returnTotalPages,

    fetchReturns,
    resetFilter,
  }
})

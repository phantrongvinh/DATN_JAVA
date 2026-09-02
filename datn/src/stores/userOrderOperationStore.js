import { defineStore } from 'pinia'
import { ref } from 'vue'
import adminAPI from '@/api/adminAPI'

export const useOrderOperationsStore = defineStore('orderOperations', () => {
  const orders = ref([])
  const loading = ref(false)
  const loadingMore = ref(false)

  const currentPage = ref(1)
  const size = ref(10)

  const totalPages = ref(0)
  const totalElements = ref(0)

  const fetchPendingOrders = async (loadMore = false) => {
    if (loadMore) {
      if (loadingMore.value || currentPage.value >= totalPages.value) return
      loadingMore.value = true
    } else {
      loading.value = true
      currentPage.value = 1
      orders.value = []
    }

    try {
      const res = await adminAPI.fetchAllOrders({
        page: currentPage.value,
        size: size.value,
        status: 'PENDING',
      })

      const content = res.content || []

      if (loadMore) {
        orders.value.push(...content)
      } else {
        orders.value = content
      }

      totalPages.value = res.totalPages || 0
      totalElements.value = res.totalElements || 0

      if (loadMore) {
        currentPage.value++
      } else {
        currentPage.value = 2
      }
    } finally {
      loading.value = false
      loadingMore.value = false
    }
  }

  const confirmOrder = (id) => adminAPI.confirmOrder(id)

  return {
    orders,
    loading,
    loadingMore,

    currentPage,
    size,
    totalPages,
    totalElements,

    fetchPendingOrders,
    confirmOrder,
  }
})

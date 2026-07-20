// stores/useDashboardStore.js
import adminAPI from '@/api/adminAPI'
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

function toISODate(d) {
  return d.toISOString().slice(0, 10)
}

export const useDashboardStore = defineStore('dashboard', () => {
  const data = ref({
    revenueToday: 0,
    revenueInRange: 0,
    revenuePreviousRange: 0,
    revenueGrowthPercent: 0,
    ordersInRange: 0,
    ordersPreviousRange: 0,
    ordersGrowthPercent: 0,
    newCustomersInRange: 0,
    newCustomersPreviousRange: 0,
    customersGrowthPercent: 0,
    monthlyRevenue: [],
    monthlyOrders: [],
    categoryStats: [],
    orderStatusToday: [],
    recentOrdersToday: [],
    topSellingProducts: [],
    topInteractedProducts: [],
  })

  const loading = ref(false)
  const error = ref(null)

  const rangeType = ref('THIS_MONTH')
  const customStart = ref(null)
  const customEnd = ref(null)
  const chartYear = ref(new Date().getFullYear())

  const resolvedRange = computed(() => {
    const today = new Date()
    let start
    let end

    switch (rangeType.value) {
      case 'TODAY':
        start = new Date(today)
        end = new Date(today)
        break
      case 'LAST_7_DAYS':
        end = new Date(today)
        start = new Date(today)
        start.setDate(start.getDate() - 6)
        break
      case 'LAST_30_DAYS':
        end = new Date(today)
        start = new Date(today)
        start.setDate(start.getDate() - 29)
        break
      case 'THIS_MONTH':
        start = new Date(today.getFullYear(), today.getMonth(), 1)
        end = new Date(today)
        break
      case 'CUSTOM':
        start = customStart.value ? new Date(customStart.value) : today
        end = customEnd.value ? new Date(customEnd.value) : today
        break
      default:
        start = today
        end = today
    }

    return { start: toISODate(start), end: toISODate(end) }
  })

  const canFetch = computed(() => {
    if (rangeType.value === 'CUSTOM') return !!customStart.value && !!customEnd.value
    return true
  })

  const fetchDashboard = async () => {
    if (!canFetch.value) return
    loading.value = true
    error.value = null
    try {
      const { start, end } = resolvedRange.value
      data.value = await adminAPI.fetchDashboard({ start, end, chartYear: chartYear.value })
    } catch (err) {
      error.value = err.response?.data?.message ?? err.message ?? 'Có lỗi xảy ra'
    } finally {
      loading.value = false
    }
  }

  return {
    data,
    loading,
    error,
    rangeType,
    customStart,
    customEnd,
    chartYear,
    resolvedRange,
    fetchDashboard,
  }
})

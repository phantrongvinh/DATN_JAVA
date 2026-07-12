import adminAPI from '@/api/adminAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useDashboardStore = defineStore('dashboard', () => {
  const data = ref({
    categoryStats: [],
    customersGrowthPercent: 0,
    monthlyOrders: [],
    monthlyRevenue: [],
    newCustomersLastMonth: 0,
    newCustomersThisMonth: 0,
    orderStatusToday: [],
    ordersGrowthPercent: 0,
    ordersLastMonth: 0,
    ordersThisMonth: 0,
    recentOrdersToday: [],
    revenueGrowthPercent: 0,
    revenueLastMonth: 0,
    revenueThisMonth: 0,
  })

  const loading = ref(false)
  const error = ref(null)

  const selectedMonth = ref(new Date().getMonth() + 1)
  const selectedYear = ref(new Date().getFullYear())

  const fetchDashboard = async () => {
    loading.value = true
    error.value = null

    try {
      const res = await adminAPI.fetchDashboard({
        month: selectedMonth.value,
        year: selectedYear.value,
      })

      data.value = res
      console.log(data.value)
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
    selectedMonth,
    selectedYear,
    fetchDashboard,
  }
})

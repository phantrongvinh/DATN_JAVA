<template>
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Thống kê</h1>
    </header>

    <div class="space-y-6 p-6">
      <!-- ================= Header ================= -->
      <div class="flex items-center justify-between">
        <!-- Filter tháng/năm -->
        <div class="flex items-center gap-3">
          <select
            v-model="selectedMonth"
            class="rounded-lg border border-border bg-background px-3 py-2 text-sm outline-none focus:border-black"
          >
            <option v-for="m in MONTHS" :key="m.value" :value="m.value">
              {{ m.label }}
            </option>
          </select>

          <select
            v-model="selectedYear"
            class="rounded-lg border border-border bg-background px-3 py-2 text-sm outline-none focus:border-black"
          >
            <option v-for="y in YEARS" :key="y" :value="y">{{ y }}</option>
          </select>

          <span class="text-sm text-muted-foreground">
            Đang xem: Tháng {{ selectedMonth }}/{{ selectedYear }}
          </span>
        </div>

        <div class="flex items-center gap-2">
          <button
            class="flex items-center gap-2 rounded-lg border border-border px-4 py-2 text-sm hover:bg-secondary disabled:opacity-50"
            :disabled="loading"
            @click="fetchDashboard"
          >
            <svg
              class="h-4 w-4"
              :class="loading ? 'animate-spin' : ''"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path d="M21 12a9 9 0 11-6.219-8.56" />
            </svg>
            {{ loading ? 'Đang tải...' : 'Làm mới' }}
          </button>

          <button
            class="rounded-lg border border-border px-4 py-2 text-sm hover:bg-secondary"
            @click="resetToToday"
          >
            Tháng hiện tại
          </button>
        </div>
      </div>

      <!-- ================= Cards ================= -->
      <div v-if="report" class="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Doanh thu tháng {{ selectedMonth }}</p>
          <h2 class="mt-3 text-3xl font-bold">{{ ulti.formatVND(report.revenueThisMonth) }}</h2>
          <p
            class="mt-3 text-sm"
            :class="report.revenueGrowthPercent >= 0 ? 'text-green-600' : 'text-red-500'"
          >
            {{ report.revenueGrowthPercent >= 0 ? '↑' : '↓' }}
            {{ Math.abs(report.revenueGrowthPercent) }}% so với tháng trước
          </p>
        </div>

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Đơn hàng tháng {{ selectedMonth }}</p>
          <h2 class="mt-3 text-3xl font-bold">{{ report.ordersThisMonth }}</h2>
          <p
            class="mt-3 text-sm"
            :class="report.ordersGrowthPercent >= 0 ? 'text-green-600' : 'text-red-500'"
          >
            {{ report.ordersGrowthPercent >= 0 ? '↑' : '↓' }}
            {{ Math.abs(report.ordersGrowthPercent) }}% so với tháng trước
          </p>
        </div>

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Khách hàng mới tháng {{ selectedMonth }}</p>
          <h2 class="mt-3 text-3xl font-bold">{{ report.newCustomersThisMonth }}</h2>
          <p
            class="mt-3 text-sm"
            :class="report.customersGrowthPercent >= 0 ? 'text-green-600' : 'text-red-500'"
          >
            {{ report.customersGrowthPercent >= 0 ? '↑' : '↓' }}
            {{ Math.abs(report.customersGrowthPercent) }}% so với tháng trước
          </p>
        </div>

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Doanh thu tháng trước</p>
          <h2 class="mt-3 text-3xl font-bold">{{ ulti.formatVND(report.revenueLastMonth) }}</h2>
          <p class="mt-3 text-sm text-muted-foreground">{{ report.ordersLastMonth }} đơn hàng</p>
        </div>
      </div>

      <!-- Loading -->
      <div v-else class="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
        <div
          v-for="i in 4"
          :key="i"
          class="h-32 animate-pulse rounded-xl border border-border bg-secondary/30"
        />
      </div>

      <!-- ================= Charts ================= -->
      <div v-if="report" class="grid gap-6 xl:grid-cols-3">
        <div class="rounded-xl border border-border bg-background p-5 xl:col-span-2">
          <h2 class="mb-5 text-lg font-semibold">Doanh thu theo tháng {{ selectedYear }}</h2>
          <RevenueChart :data="report.monthlyRevenue" :selected-month="selectedMonth" />
        </div>

        <div class="rounded-xl border border-border bg-background p-5">
          <h2 class="mb-5 text-lg font-semibold">Tỷ lệ danh mục</h2>
          <CategoryPie :data="report.categoryStats" />
        </div>
      </div>

      <!-- ================= Order Chart ================= -->
      <div v-if="report" class="rounded-xl border border-border bg-background p-5">
        <h2 class="mb-5 text-lg font-semibold">Thống kê đơn hàng {{ selectedYear }}</h2>
        <OrderChart :data="report.monthlyOrders" :selected-month="selectedMonth" />
      </div>

      <!-- ================= Bottom ================= -->
      <div v-if="report" class="grid gap-6 lg:grid-cols-2">
        <!-- Đơn hàng hôm nay -->
        <div class="rounded-xl border border-border bg-background">
          <div class="flex items-center justify-between border-b border-border px-5 py-4">
            <h2 class="text-lg font-semibold">Đơn hàng hôm nay</h2>
            <span class="text-sm text-muted-foreground">
              {{ report.recentOrdersToday?.length ?? 0 }} đơn
            </span>
          </div>

          <div
            v-if="!report.recentOrdersToday?.length"
            class="py-12 text-center text-muted-foreground"
          >
            Hôm nay chưa có đơn hàng.
          </div>

          <div v-else class="divide-y divide-border">
            <div
              v-for="order in report.recentOrdersToday"
              :key="order.id"
              class="flex items-center justify-between px-5 py-4 hover:bg-secondary/40"
            >
              <div>
                <p class="font-medium">#{{ order.id }}</p>
                <p class="mt-1 text-sm text-muted-foreground">
                  {{ ulti.formatDate(order.createdAt) }}
                </p>
              </div>
              <div class="text-right">
                <p class="font-semibold">{{ ulti.formatVND(order.finalPrice) }}</p>
                <span
                  class="mt-2 inline-flex rounded-full px-3 py-1 text-xs"
                  :class="statusClass(order.status)"
                >
                  {{ statusText(order.status) }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Trạng thái đơn hôm nay -->
        <div class="rounded-xl border border-border bg-background">
          <div class="border-b border-border px-5 py-4">
            <h2 class="text-lg font-semibold">Trạng thái đơn hôm nay</h2>
          </div>

          <div
            v-if="!report.orderStatusToday?.length"
            class="py-12 text-center text-muted-foreground"
          >
            Không có dữ liệu.
          </div>

          <div v-else class="space-y-4 p-5">
            <div v-for="item in report.orderStatusToday" :key="item.status">
              <div class="mb-2 flex justify-between">
                <span :class="statusClass(item.status)" class="rounded-full px-2 py-0.5 text-xs">
                  {{ statusText(item.status) }}
                </span>
                <span class="font-semibold">{{ item.count }}</span>
              </div>
              <div class="h-2 overflow-hidden rounded-full bg-secondary">
                <div
                  class="h-full rounded-full bg-foreground transition-all"
                  :style="{
                    width: report.orderStatusToday.length
                      ? `${(item.count / report.orderStatusToday.reduce((s, x) => s + x.count, 0)) * 100}%`
                      : '0%',
                  }"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { computed, onMounted, watch } from 'vue'
import ulti from '@/ulti/ulti'
import RevenueChart from '@/components/admin/RevenueChart.vue'
import CategoryPie from '@/components/admin/CategoryPie.vue'
import OrderChart from '@/components/admin/OrderChart.vue'
import { useDashboardStore } from '@/stores/useDashboardStore'
import { storeToRefs } from 'pinia'

const dashboardStore = useDashboardStore()
const { selectedMonth, selectedYear, loading } = storeToRefs(dashboardStore)

const report = computed(() => dashboardStore.data)

const MONTHS = Array.from({ length: 12 }, (_, i) => ({
  value: i + 1,
  label: `Tháng ${i + 1}`,
}))
const YEARS = Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - i)

const fetchDashboard = () => dashboardStore.fetchDashboard()

const resetToToday = () => {
  selectedMonth.value = new Date().getMonth() + 1
  selectedYear.value = new Date().getFullYear()
}

onMounted(fetchDashboard)

watch([selectedMonth, selectedYear], fetchDashboard)

const statusClass = (status) => {
  switch (status) {
    case 'PENDING':
      return 'bg-yellow-100 text-yellow-700'

    case 'CONFIRMED':
      return 'bg-blue-100 text-blue-700'

    case 'SHIPPING':
      return 'bg-purple-100 text-purple-700'

    case 'DELIVERED':
      return 'bg-green-100 text-green-700'

    case 'CANCELLED':
      return 'bg-red-100 text-red-700'

    default:
      return 'bg-gray-100 text-gray-700'
  }
}

const statusText = (status) => {
  switch (status) {
    case 'PENDING':
      return 'Chờ xác nhận'

    case 'CONFIRMED':
      return 'Đã xác nhận'

    case 'SHIPPING':
      return 'Đang giao'

    case 'DELIVERED':
      return 'Đã giao'

    case 'CANCELLED':
      return 'Đã hủy'

    default:
      return 'Không xác định'
  }
}
</script>

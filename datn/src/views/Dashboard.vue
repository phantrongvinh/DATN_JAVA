<template>
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Thống kê</h1>
    </header>

    <div class="space-y-6 p-6">
      <!-- ================= Filter ================= -->
      <div class="flex flex-wrap items-center justify-between gap-4">
        <div class="flex flex-wrap items-center gap-2">
          <button
            v-for="opt in RANGE_PRESETS"
            :key="opt.value"
            @click="rangeType = opt.value"
            class="rounded-lg border px-3 py-2 text-sm transition-colors"
            :class="
              rangeType === opt.value
                ? 'border-black bg-black text-white'
                : 'border-border hover:bg-secondary'
            "
          >
            {{ opt.label }}
          </button>

          <template v-if="rangeType === 'CUSTOM'">
            <input
              type="date"
              v-model="customStart"
              class="rounded-lg border border-border px-3 py-2 text-sm outline-none focus:border-black"
            />
            <span class="text-sm text-muted-foreground">đến</span>
            <input
              type="date"
              v-model="customEnd"
              class="rounded-lg border border-border px-3 py-2 text-sm outline-none focus:border-black"
            />
          </template>

          <span class="text-sm text-muted-foreground"
            >({{ resolvedRange.start }} → {{ resolvedRange.end }})</span
          >
        </div>

        <div class="flex items-center gap-2">
          <select
            v-model="chartYear"
            class="rounded-lg border border-border bg-background px-3 py-2 text-sm outline-none focus:border-black"
          >
            <option v-for="y in YEARS" :key="y" :value="y">Biểu đồ năm {{ y }}</option>
          </select>

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
            @click="resetFilters"
          >
            Đặt lại
          </button>
        </div>
      </div>

      <!-- ================= Cards ================= -->
      <div v-if="report" class="grid gap-5 md:grid-cols-2 xl:grid-cols-5">
        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Doanh thu hôm nay</p>
          <h2 class="mt-3 text-3xl font-bold">{{ ulti.formatVND(report.revenueToday) }}</h2>
        </div>

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Doanh thu trong {{ rangeLabel }}</p>
          <h2 class="mt-3 text-3xl font-bold">{{ ulti.formatVND(report.revenueInRange) }}</h2>
          <p
            class="mt-3 text-sm"
            :class="report.revenueGrowthPercent >= 0 ? 'text-green-600' : 'text-red-500'"
          >
            {{ report.revenueGrowthPercent >= 0 ? '↑' : '↓' }}
            {{ Math.abs(report.revenueGrowthPercent) }}% so với kỳ trước
          </p>
        </div>

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Tổng đơn trong {{ rangeLabel }}</p>
          <h2 class="mt-3 text-3xl font-bold">{{ report.ordersInRange }}</h2>
          <p
            class="mt-3 text-sm"
            :class="report.ordersGrowthPercent >= 0 ? 'text-green-600' : 'text-red-500'"
          >
            {{ report.ordersGrowthPercent >= 0 ? '↑' : '↓' }}
            {{ Math.abs(report.ordersGrowthPercent) }}% so với kỳ trước
          </p>
        </div>

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Khách hàng mới trong {{ rangeLabel }}</p>
          <h2 class="mt-3 text-3xl font-bold">{{ report.newCustomersInRange }}</h2>
          <p
            class="mt-3 text-sm"
            :class="report.customersGrowthPercent >= 0 ? 'text-green-600' : 'text-red-500'"
          >
            {{ report.customersGrowthPercent >= 0 ? '↑' : '↓' }}
            {{ Math.abs(report.customersGrowthPercent) }}% so với kỳ trước
          </p>
        </div>

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Doanh thu kỳ trước {{ rangeLabel }}</p>
          <h2 class="mt-3 text-3xl font-bold">{{ ulti.formatVND(report.revenuePreviousRange) }}</h2>
          <p class="mt-3 text-sm text-muted-foreground">
            {{ report.ordersPreviousRange }} đơn hàng
          </p>
        </div>
      </div>

      <!-- ================= Đơn hôm nay / Trạng thái ================= -->
      <div v-if="report" class="grid gap-6 lg:grid-cols-2">
        <div class="rounded-xl border border-border bg-background">
          <div class="flex items-center justify-between border-b border-border px-5 py-4">
            <h2 class="text-lg font-semibold">Đơn hàng hôm nay</h2>
            <span class="text-sm text-muted-foreground"
              >{{ report.recentOrdersToday?.length ?? 0 }} đơn</span
            >
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
                <span :class="statusClass(item.status)" class="rounded-full px-2 py-0.5 text-xs">{{
                  statusText(item.status)
                }}</span>
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

      <!-- ================= Top sản phẩm ================= -->
      <div v-if="report" class="grid gap-6 lg:grid-cols-2">
        <div class="rounded-xl border border-border bg-background">
          <div class="border-b border-border px-5 py-4">
            <h2 class="text-lg font-semibold">Sản phẩm bán chạy</h2>
          </div>
          <div
            v-if="report.topSellingProducts?.length === 0"
            class="py-12 text-center text-muted-foreground"
          >
            Chưa có dữ liệu.
          </div>
          <div v-else class="divide-y divide-border">
            <div
              v-for="(p, idx) in report.topSellingProducts"
              :key="p.productId"
              class="flex items-center gap-3 px-5 py-3"
            >
              <span class="w-5 text-sm font-semibold text-muted-foreground">{{ idx + 1 }}</span>
              <img :src="p.image" class="h-12 w-12 rounded-lg object-cover bg-secondary" />
              <p class="flex-1 truncate text-sm font-medium">{{ p.productName }}</p>
              <span class="text-sm font-semibold">{{ p.value }} đã bán</span>
            </div>
          </div>
        </div>

        <div class="rounded-xl border border-border bg-background">
          <div class="border-b border-border px-5 py-4">
            <h2 class="text-lg font-semibold">Sản phẩm tương tác nhiều</h2>
          </div>
          <div
            v-if="!report.topInteractedProducts?.length"
            class="py-12 text-center text-muted-foreground"
          >
            Chưa có dữ liệu.
          </div>
          <div v-else class="divide-y divide-border">
            <div
              v-for="(p, idx) in report.topInteractedProducts"
              :key="p.productId"
              class="flex items-center gap-3 px-5 py-3"
            >
              <span class="w-5 text-sm font-semibold text-muted-foreground">{{ idx + 1 }}</span>
              <img :src="p.image" class="h-12 w-12 rounded-lg object-cover bg-secondary" />
              <p class="flex-1 truncate text-sm font-medium">{{ p.productName }}</p>
              <span class="text-sm font-semibold">{{ p.value }} tương tác</span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="grid gap-5 md:grid-cols-2 xl:grid-cols-5">
        <div
          v-for="i in 5"
          :key="i"
          class="h-32 animate-pulse rounded-xl border border-border bg-secondary/30"
        />
      </div>

      <!-- ================= Charts ================= -->
      <div v-if="report" class="grid gap-6 xl:grid-cols-3">
        <div class="rounded-xl border border-border bg-background p-5 xl:col-span-2">
          <h2 class="mb-5 text-lg font-semibold">Doanh thu theo tháng {{ chartYear }}</h2>
          <RevenueChart :data="report.monthlyRevenue" />
        </div>
        <div class="rounded-xl border border-border bg-background p-5">
          <h2 class="mb-5 text-lg font-semibold">Tỷ lệ danh mục</h2>
          <CategoryPie :data="report.categoryStats" />
        </div>
      </div>

      <div v-if="report" class="rounded-xl border border-border bg-background p-5">
        <h2 class="mb-5 text-lg font-semibold">Thống kê đơn hàng {{ chartYear }}</h2>
        <OrderChart :data="report.monthlyOrders" />
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
const { rangeType, customStart, customEnd, chartYear, resolvedRange, loading } =
  storeToRefs(dashboardStore)
const report = computed(() => dashboardStore.data)

const fetchDashboard = () => dashboardStore.fetchDashboard()

const RANGE_PRESETS = [
  { value: 'TODAY', label: 'Hôm nay' },
  { value: 'LAST_7_DAYS', label: '7 ngày qua' },
  { value: 'LAST_30_DAYS', label: '30 ngày qua' },
  { value: 'THIS_MONTH', label: 'Tháng này' },
  { value: 'CUSTOM', label: 'Tùy chỉnh' },
]

const RANGE_LABELS = {
  TODAY: 'hôm nay',
  LAST_7_DAYS: '7 ngày qua',
  LAST_30_DAYS: '30 ngày qua',
  THIS_MONTH: 'tháng này',
  CUSTOM: 'khoảng đã chọn',
}

const rangeLabel = computed(() => RANGE_LABELS[rangeType.value] ?? '')

const YEARS = Array.from({ length: 5 }, (_, i) => new Date().getFullYear() - i)

const resetFilters = () => {
  rangeType.value = 'THIS_MONTH'
  customStart.value = null
  customEnd.value = null
  chartYear.value = new Date().getFullYear()
}

onMounted(fetchDashboard)
watch([rangeType, chartYear], fetchDashboard)
watch([customStart, customEnd], () => {
  if (rangeType.value === 'CUSTOM' && customStart.value && customEnd.value) fetchDashboard()
})

const statusClass = (status) =>
  ({
    PENDING: 'bg-yellow-100 text-yellow-700',
    CONFIRMED: 'bg-blue-100 text-blue-700',
    SHIPPING: 'bg-purple-100 text-purple-700',
    DELIVERED: 'bg-green-100 text-green-700',
    CANCELLED: 'bg-red-100 text-red-700',
  })[status] ?? 'bg-gray-100 text-gray-700'

const statusText = (status) =>
  ({
    PENDING: 'Chờ xác nhận',
    CONFIRMED: 'Đã xác nhận',
    SHIPPING: 'Đang giao',
    DELIVERED: 'Đã giao',
    CANCELLED: 'Đã hủy',
  })[status] ?? 'Không xác định'
</script>

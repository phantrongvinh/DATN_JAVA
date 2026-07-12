<template>
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Thống kê</h1>
    </header>
    <div class="space-y-6 p-6">
      <!-- ================= Header ================= -->

      <div class="flex items-center justify-end">
        <button
          class="rounded-lg border border-border px-4 py-2 hover:bg-secondary"
          @click="loadDashboard"
        >
          Làm mới
        </button>
      </div>

      <!-- ================= Cards ================= -->

      <div class="grid gap-5 md:grid-cols-2 xl:grid-cols-4">
        <!-- Revenue -->

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Doanh thu tháng</p>

          <h2 class="mt-3 text-3xl font-bold">
            {{ ulti.formatVND(report.revenueThisMonth) }}
          </h2>

          <p
            class="mt-3 text-sm"
            :class="report.revenueGrowthPercent >= 0 ? 'text-green-600' : 'text-red-500'"
          >
            {{ report.revenueGrowthPercent }}% so với tháng trước
          </p>
        </div>

        <!-- Orders -->

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Đơn hàng tháng</p>

          <h2 class="mt-3 text-3xl font-bold">
            {{ report.ordersThisMonth }}
          </h2>

          <p
            class="mt-3 text-sm"
            :class="report.ordersGrowthPercent >= 0 ? 'text-green-600' : 'text-red-500'"
          >
            {{ report.ordersGrowthPercent }}% so với tháng trước
          </p>
        </div>

        <!-- Customers -->

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Khách hàng mới</p>

          <h2 class="mt-3 text-3xl font-bold">
            {{ report.newCustomersThisMonth }}
          </h2>

          <p
            class="mt-3 text-sm"
            :class="report.customersGrowthPercent >= 0 ? 'text-green-600' : 'text-red-500'"
          >
            {{ report.customersGrowthPercent }}% so với tháng trước
          </p>
        </div>

        <!-- Revenue Last Month -->

        <div class="rounded-xl border border-border bg-background p-5">
          <p class="text-sm text-muted-foreground">Doanh thu tháng trước</p>

          <h2 class="mt-3 text-3xl font-bold">
            {{ ulti.formatVND(report.revenueLastMonth) }}
          </h2>

          <p class="mt-3 text-sm text-muted-foreground">{{ report.ordersLastMonth }} đơn hàng</p>
        </div>
      </div>

      <!-- ================= Charts ================= -->

      <div class="grid gap-6 xl:grid-cols-3">
        <div class="rounded-xl border border-border bg-background p-5 xl:col-span-2">
          <h2 class="mb-5 text-lg font-semibold">Doanh thu theo tháng</h2>

          <RevenueChart :data="report.monthlyRevenue" />
        </div>

        <div class="rounded-xl border border-border bg-background p-5">
          <h2 class="mb-5 text-lg font-semibold">Tỷ lệ danh mục</h2>

          <CategoryPie :data="report.categoryStats" />
        </div>
      </div>

      <!-- ================= Order Chart ================= -->

      <div class="rounded-xl border border-border bg-background p-5">
        <h2 class="mb-5 text-lg font-semibold">Thống kê đơn hàng</h2>

        <OrderChart :data="report.monthlyOrders" />
      </div>
      <!-- ================= Bottom ================= -->

      <div class="grid gap-6 lg:grid-cols-2">
        <!-- Đơn hàng hôm nay -->

        <div class="rounded-xl border border-border bg-background">
          <div class="flex items-center justify-between border-b border-border px-5 py-4">
            <h2 class="text-lg font-semibold">Đơn hàng hôm nay</h2>

            <span class="text-sm text-muted-foreground">
              {{ report.recentOrdersToday.length }} đơn
            </span>
          </div>

          <div
            v-if="report.recentOrdersToday.length === 0"
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
                <p class="font-semibold">
                  {{ ulti.formatVND(order.finalPrice) }}
                </p>

                <span
                  class="mt-2 inline-flex rounded-full px-3 py-1 text-xs"
                  :class="statusClass(order.status)"
                >
                  {{ order.status }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <!-- Trạng thái đơn -->

        <div class="rounded-xl border border-border bg-background">
          <div class="border-b border-border px-5 py-4">
            <h2 class="text-lg font-semibold">Trạng thái đơn hôm nay</h2>
          </div>

          <div
            v-if="report.orderStatusToday.length === 0"
            class="py-12 text-center text-muted-foreground"
          >
            Không có dữ liệu.
          </div>

          <div v-else class="space-y-4 p-5">
            <div v-for="item in report.orderStatusToday" :key="item.status">
              <div class="mb-2 flex justify-between">
                <span>
                  {{ item.status }}
                </span>

                <span class="font-semibold">
                  {{ item.total }}
                </span>
              </div>

              <div class="h-2 overflow-hidden rounded-full bg-secondary">
                <div
                  class="h-full rounded-full bg-foreground"
                  :style="{
                    width: item.percent + '%',
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
import { computed, onMounted } from 'vue'
import ulti from '@/ulti/ulti'
import RevenueChart from '@/components/admin/RevenueChart.vue'
import CategoryPie from '@/components/admin/CategoryPie.vue'
import OrderChart from '@/components/admin/OrderChart.vue'
import { useDashboardStore } from '@/stores/useDashboardStore'

const dashboardStore = useDashboardStore()

const report = computed(() => dashboardStore.data)

onMounted(async () => {
  await dashboardStore.fetchDashboard()
})

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
</script>

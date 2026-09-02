<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { RotateCcw, Search } from 'lucide-vue-next'
import { storeToRefs } from 'pinia'

import ulti from '@/ulti/ulti'
import { useOrderStore } from '@/stores/useOrderStore'
import OrderModal from '@/components/admin/OrderModal.vue'
import { useNotificationStore } from '@/stores/useNotificationStore'
import { useDebounce } from '@/composables/useDebounce'

const orderStore = useOrderStore()
// const paymentMethodStore = usePaymentMethodStore()
const notificationStore = useNotificationStore()

const { orders, currentPage, totalPages, totalElements, size } = storeToRefs(orderStore)
// const { paymentMethods } = storeToRefs(paymentMethodStore)

// Status
const STATUS_OPTIONS = [
  {
    value: 'PENDING',
    label: 'Chờ xác nhận',
    tone: 'bg-yellow-100 text-yellow-700',
  },
  {
    value: 'CONFIRMED',
    label: 'Đã xác nhận',
    tone: 'bg-blue-100 text-blue-700',
  },
  {
    value: 'SHIPPING',
    label: 'Đang giao',
    tone: 'bg-orange-100 text-orange-700',
  },
  {
    value: 'DELIVERED',
    label: 'Hoàn thành',
    tone: 'bg-green-100 text-green-700',
  },
  {
    value: 'CANCELLED',
    label: 'Đã hủy',
    tone: 'bg-red-100 text-red-700',
  },
]

const getStatus = (status) => {
  return STATUS_OPTIONS.find((item) => item.value === status)
}

const PAYMENT_STATUS = {
  PENDING: { label: 'Chưa thanh toán', tone: 'bg-yellow-100 text-yellow-700' },
  PAID: { label: 'Đã thanh toán', tone: 'bg-green-100 text-green-700' },
  REFUNDED: { label: 'Đã hoàn tiền', tone: 'bg-purple-100 text-purple-700' },
  UNPAID: { label: 'Hủy giao dịch', tone: 'bg-red-100 text-red-700' },
}

// Filter
const filter = ref({
  search: '',
  status: '',
  paymentStatus: null,
  paymentMethodId: '',
  dateFrom: '',
  dateTo: '',
})

const buildParams = (page = 1) => ({
  page,
  size: size.value,
  ...filter.value,
  dateFrom: filter.value.dateFrom ? `${filter.value.dateFrom}T00:00:00` : null,
  dateTo: filter.value.dateTo ? `${filter.value.dateTo}T23:59:59` : null,
})

const applyFilter = async () => {
  await orderStore.fetchAllOrder(buildParams(1))
}

// Search debounce
const debouncedSearch = useDebounce(
  computed(() => filter.value.search),
  500,
)
watch(debouncedSearch, applyFilter)

// filter
watch(
  () => [
    filter.value.status,
    filter.value.paymentStatus,
    filter.value.paymentMethodId,
    filter.value.dateFrom,
    filter.value.dateTo,
  ],
  applyFilter,
)

const resetFilter = () => {
  filter.value = {
    search: '',
    status: '',
    paymentStatus: null,
    paymentMethodId: '',
    dateFrom: '',
    dateTo: '',
  }
  notificationStore.notify('Đặt lại bộ lọc', 'success')
}

// API
onMounted(async () => {
  await Promise.all([
    orderStore.fetchAllOrder({ page: 1, size: 5 }),
    // paymentMethodStore.fetchPaymentMethods(),
  ])
})

const changePage = async (page) => {
  if (page < 1 || page > totalPages.value) return
  await orderStore.fetchAllOrder(buildParams(page))
}

// Detail modal
const show = ref(false)
const selectOrder = ref(null)

const handleOpenDetail = (o) => {
  show.value = true
  selectOrder.value = o
}
</script>

<template>
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Quản lý đơn hàng</h1>
    </header>
    <main class="p-6">
      <div class="border border-border bg-background">
        <!-- Header -->
        <div class="space-y-4 border-b border-border p-4">
          <!-- Search -->
          <div class="relative">
            <Search
              class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
            />
            <input
              v-model="filter.search"
              placeholder="Tên khách hàng, SĐT hoặc mã đơn..."
              class="w-full rounded border border-border bg-background py-2 pl-10 pr-3 text-sm outline-none"
            />
          </div>

          <!-- Filter -->
          <div class="flex flex-col gap-3 md:flex-row md:flex-wrap md:items-center">
            <select
              v-model="filter.status"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option value="">Tất cả trạng thái</option>
              <option value="PENDING">Chờ thanh toán</option>
              <option value="CONFIRMED">Đã xác nhận</option>
              <option value="SHIPPING">Đang giao</option>
              <option value="DELIVERED">Hoàn thành</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>

            <select
              v-model="filter.paymentStatus"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option :value="null">Thanh toán</option>
              <option value="PENDING">Chưa thanh toán</option>
              <option value="PAID">Đã thanh toán</option>
              <option value="REFUNDED">Đã hoàn tiền</option>
            </select>

            <select
              v-model="filter.paymentMethodId"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option value="">Phương thức</option>
              <option v-for="item in paymentMethods" :key="item.id" :value="item.id">
                {{ item.name }}
              </option>
            </select>

            <input
              v-model="filter.dateFrom"
              type="date"
              placeholder="Từ ngày"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            />

            <input
              v-model="filter.dateTo"
              type="date"
              placeholder="Đến ngày"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            />

            <div class="flex gap-2">
              <button
                @click="resetFilter"
                class="w-full flex items-center justify-center gap-2 border border-border px-3 py-2 text-sm hover:bg-secondary"
              >
                <RotateCcw class="h-4 w-4" />

                Đặt lại
              </button>
            </div>
          </div>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr
                class="border-b border-border bg-secondary/40 text-left text-[11px] uppercase tracking-widest text-muted-foreground"
              >
                <th class="px-4 py-3">Mã đơn</th>
                <th>Khách hàng</th>
                <th>Ngày</th>
                <th>Sản phẩm</th>
                <th>Tổng tiền</th>
                <th>Thanh toán</th>
                <th>Trạng thái</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="order in orders"
                :key="order.id"
                class="border-b border-border hover:bg-secondary/20"
              >
                <td class="px-4 py-4 font-medium cursor-pointer" @click="handleOpenDetail(order)">
                  #{{ order.id }}
                </td>

                <td>
                  <p class="font-medium">{{ order.receiverName }}</p>
                  <p class="text-xs text-muted-foreground">{{ order.receiverPhone }}</p>
                </td>

                <td>{{ ulti.formatDate(order.createdAt) }}</td>

                <td>{{ order.items?.reduce((sum, item) => sum + item.quantity, 0) }}</td>

                <td class="font-medium">{{ ulti.formatVND(order.finalPrice) }}</td>

                <td>
                  <span
                    class="rounded-full px-3 py-1 text-xs"
                    :class="PAYMENT_STATUS[order.paymentStatus]?.tone"
                  >
                    {{ PAYMENT_STATUS[order.paymentStatus]?.label }}
                  </span>
                </td>

                <td class="pr-4">
                  <span
                    class="inline-flex rounded-full px-3 py-1 text-xs font-medium"
                    :class="getStatus(order.status)?.tone"
                  >
                    {{ getStatus(order.status)?.label || order.status }}
                  </span>
                </td>
              </tr>

              <tr v-if="orders && orders.length === 0">
                <td colspan="7" class="p-8 text-center text-muted-foreground">
                  Không có đơn hàng.
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div
          class="flex flex-col gap-4 border-t border-border px-6 py-4 md:flex-row md:items-center md:justify-between"
        >
          <div class="text-sm text-gray-500">
            Hiển thị {{ (currentPage - 1) * size + 1 }} -
            {{ Math.min(currentPage * size, totalElements) }} / {{ totalElements }} Đơn hàng
          </div>

          <div class="flex flex-wrap items-center gap-2">
            <button
              class="rounded border px-3 py-2 disabled:cursor-not-allowed disabled:opacity-50 hover:bg-secondary"
              :disabled="currentPage === 1"
              @click="changePage(currentPage - 1)"
            >
              Trước
            </button>

            <button
              v-for="pageNumber in totalPages"
              :key="pageNumber"
              @click="changePage(pageNumber)"
              class="flex h-9 w-9 items-center justify-center rounded border text-sm transition"
              :class="currentPage === pageNumber ? 'bg-black text-white' : 'hover:bg-secondary'"
            >
              {{ pageNumber }}
            </button>

            <button
              class="rounded border px-3 py-2 disabled:cursor-not-allowed disabled:opacity-50 hover:bg-secondary"
              :disabled="currentPage === totalPages"
              @click="changePage(currentPage + 1)"
            >
              Sau
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>

  <OrderModal v-model:show="show" :order="selectOrder"></OrderModal>
</template>

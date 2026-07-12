<script setup>
import { computed, onMounted, ref } from 'vue'
import { Search } from 'lucide-vue-next'
import { storeToRefs } from 'pinia'
import '@vuepic/vue-datepicker/dist/main.css'

import ulti from '@/ulti/ulti'
import { useOrderStore } from '@/stores/useOrderStore'
import { VueDatePicker } from '@vuepic/vue-datepicker'
import OrderModal from '@/components/admin/OrderModal.vue'

const orderStore = useOrderStore()

const { orders, currentPage, totalPages, totalElements, size } = storeToRefs(orderStore)

// ==============================
// Filter
// ==============================

const keyword = ref('')
const statusFilter = ref('ALL')

// ==============================
// Status
// ==============================

const STATUS_OPTIONS = [
  {
    value: 'PENDING',
    label: 'Chờ thanh toán',
  },
  {
    value: 'CONFIRMED',
    label: 'Đã xác nhận',
  },
  {
    value: 'SHIPPING',
    label: 'Đang giao',
  },
  {
    value: 'DELIVERED',
    label: 'Hoàn thành',
  },
  {
    value: 'CANCELLED',
    label: 'Đã hủy',
  },
]

const STATUS_FILTER = [
  {
    value: 'ALL',
    label: 'Tất cả',
  },
  ...STATUS_OPTIONS,
]

const PAYMENT_STATUS = {
  PENDING: {
    label: 'Chưa thanh toán',
    tone: 'bg-yellow-100 text-yellow-700',
  },

  PAID: {
    label: 'Đã thanh toán',
    tone: 'bg-green-100 text-green-700',
  },

  REFUNDED: {
    label: 'Đã hoàn tiền',
    tone: 'bg-purple-100 text-purple-700',
  },
}

// ==============================
// Search
// ==============================

const filteredOrders = computed(() => {
  return orders.value.filter((order) => {
    const matchStatus = statusFilter.value === 'ALL' || order.status === statusFilter.value

    const keywordLower = keyword.value.toLowerCase()

    const matchKeyword =
      keyword.value === '' ||
      order.trackingCode?.toLowerCase().includes(keywordLower) ||
      order.receiverName?.toLowerCase().includes(keywordLower)

    return matchStatus && matchKeyword
  })
})

// ==============================
// API
// ==============================

onMounted(async () => {
  await orderStore.fetchAllOrder({
    page: 1,
    size: 5,
  })
})

const changePage = async (page) => {
  if (page < 1 || page > totalPages.value) return

  await orderStore.fetchAllOrder({
    page,
    size: size.value,

    ...filter.value,
  })
}

const updateOrderStatus = async (id, status) => {
  await orderStore.updateStatusOrder(id, status)
  await orderStore.fetchAllOrder({
    page: currentPage.value,
    size: size.value,
  })
}
// handle filter
const filter = ref({
  search: '',
  status: '',
  paymentStatus: null,
  paymentMethodId: '',
  dateFrom: null,
  dateTo: null,
})

const handleFilter = async () => {
  const params = {
    page: 1,
    size: size.value,

    ...filter.value,

    dateFrom: filter.value.dateFrom ? ulti.formatLocalDateTime(filter.value.dateFrom) : null,

    dateTo: filter.value.dateTo ? ulti.formatLocalDateTime(filter.value.dateTo) : null,
  }
  await orderStore.fetchAllOrder(params)
}

const resetFilter = async () => {
  filter.value = {
    search: '',
    status: '',
    paymentStatus: null,
    paymentMethodId: '',
    dateFrom: null,
    dateTo: null,
  }

  await handleFilter()
}

// handle detail order

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
        <div class="flex flex-wrap items-center gap-3 border-b border-border p-4">
          <div class="border-b border-border p-4 space-y-4">
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
            <div class="grid gap-3 md:grid-cols-2 xl:grid-cols-6">
              <!-- Order Status -->
              <select v-model="filter.status" class="rounded border border-border p-2">
                <option value="">Tất cả trạng thái</option>
                <option value="PENDING">Chờ thanh toán</option>
                <option value="CONFIRMED">Đã xác nhận</option>
                <option value="SHIPPING">Đang giao</option>
                <option value="DELIVERED">Hoàn thành</option>
                <option value="CANCELLED">Đã hủy</option>
              </select>

              <!-- Payment Status -->
              <select v-model="filter.paymentStatus" class="rounded border border-border p-2">
                <option :value="null">Thanh toán</option>
                <option value="PENDING">Chưa thanh toán</option>
                <option value="PAID">Đã thanh toán</option>
                <option value="REFUNDED">Đã hoàn tiền</option>
              </select>

              <!-- Payment Method -->
              <select v-model="filter.paymentMethodId" class="rounded border border-border p-2">
                <option value="">Phương thức</option>

                <option v-for="item in paymentMethods" :key="item.id" :value="item.id">
                  {{ item.name }}
                </option>
              </select>

              <!-- From -->
              <VueDatePicker
                v-model="filter.dateFrom"
                auto-apply
                :enable-time-picker="false"
                placeholder="Từ ngày"
              />

              <!-- To -->
              <VueDatePicker
                v-model="filter.dateTo"
                auto-apply
                :enable-time-picker="false"
                placeholder="Đến ngày"
              />

              <!-- Button -->
              <div class="flex gap-2">
                <button
                  @click="handleFilter"
                  class="flex-1 rounded bg-black px-4 py-2 text-white hover:bg-black/90"
                >
                  Lọc
                </button>

                <button
                  @click="resetFilter"
                  class="rounded border border-border px-4 py-2 hover:bg-secondary"
                >
                  Đặt lại
                </button>
              </div>
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
                v-for="order in filteredOrders"
                :key="order.id"
                class="border-b border-border hover:bg-secondary/20"
              >
                <td class="px-4 py-4 font-medium cursor-pointer" @click="handleOpenDetail(order)">
                  #{{ order.trackingCode }}
                </td>

                <td>
                  <p class="font-medium">
                    {{ order.receiverName }}
                  </p>

                  <p class="text-xs text-muted-foreground">
                    {{ order.receiverPhone }}
                  </p>
                </td>

                <td>
                  {{ ulti.formatDate(order.createdAt) }}
                </td>

                <td>
                  {{ order.items?.reduce((sum, item) => sum + item.quantity, 0) }}
                </td>

                <td class="font-medium">
                  {{ ulti.formatVND(order.finalPrice) }}
                </td>

                <td>
                  <span
                    class="rounded-full px-3 py-1 text-xs"
                    :class="PAYMENT_STATUS[order.paymentStatus].tone"
                  >
                    {{ PAYMENT_STATUS[order.paymentStatus].label }}
                  </span>
                </td>

                <td class="pr-4">
                  <select
                    :value="order.status"
                    @change="updateOrderStatus(order.id, $event.target.value)"
                    class="border border-border bg-background px-2 py-1.5 text-xs"
                  >
                    <option v-for="item in STATUS_OPTIONS" :key="item.value" :value="item.value">
                      {{ item.label }}
                    </option>
                  </select>
                </td>
              </tr>

              <tr v-if="filteredOrders.length === 0">
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
            Hiển thị

            {{ (currentPage - 1) * size + 1 }}

            -

            {{ Math.min(currentPage * size, totalElements) }}

            /

            {{ totalElements }}

            Đơn hàng
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

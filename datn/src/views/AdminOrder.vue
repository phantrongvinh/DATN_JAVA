<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { storeToRefs } from 'pinia'
import { useOrderStore } from '@/stores/useOrderStore'
import { useNotificationStore } from '@/stores/useNotificationStore'
import OrderCard from '@/components/shared/OrderCard.vue'
import OrderDetailModal from '@/components/shared/OrderDetailModal.vue'
import OrderInvoice from '@/components/shared/OrderInvoice.vue'

const orderStore = useOrderStore()
const notification = useNotificationStore()
const { orders, loading } = storeToRefs(orderStore)

const statusFilter = ref('') // '' = tất cả

const STATUS_OPTIONS = [
  { value: '', label: 'Tất cả' },
  { value: 'PENDING', label: 'Chờ thanh toán' },
  { value: 'CONFIRMED', label: 'Đã xác nhận' },
  { value: 'SHIPPING', label: 'Đang giao' },
  { value: 'DELIVERED', label: 'Đã giao' },
  { value: 'RETURN_REQUESTED', label: 'Đang xử lý trả hàng' },
  { value: 'CANCELLED', label: 'Đã hủy' },
]

const fetchOrders = () =>
  orderStore.fetchAllOrder({ page: 1, size: 50, status: statusFilter.value || undefined })

onMounted(fetchOrders)

const changeFilter = (status) => {
  statusFilter.value = status
  fetchOrders()
}

// Nhãn nút "next status" — lấy động từ backend thay vì hardcode ở FE
const NEXT_LABEL_MAP = {
  PENDING: 'Xác nhận đơn',
  CONFIRMED: 'Chuyển sang đang giao',
}

const handleAdvance = async (order) => {
  try {
    await orderStore.advanceOrder(order.id)
    notification.notify('Cập nhật thành công', 'success')
    fetchOrders()
  } catch (err) {
    notification.notify(err.response?.data?.message ?? 'Có lỗi xảy ra', 'error')
  }
}

const showDetail = ref(false)
const selectedOrder = ref(null)
const handleViewDetail = (order) => {
  selectedOrder.value = order
  showDetail.value = true
}

const printingOrder = ref(null)
const handlePrint = (order) => {
  printingOrder.value = order
  nextTick(() => window.print())
}
</script>

<template>
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Quản lý đơn hàng</h1>
    </header>

    <main class="p-6">
      <div class="mb-4 flex flex-wrap gap-2">
        <button
          v-for="opt in STATUS_OPTIONS"
          :key="opt.value"
          @click="changeFilter(opt.value)"
          class="border px-4 py-2 text-sm"
          :class="
            statusFilter === opt.value
              ? 'border-black bg-black text-white'
              : 'border-border hover:bg-secondary'
          "
        >
          {{ opt.label }}
        </button>
      </div>

      <div class="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
        <OrderCard
          v-for="order in orders"
          :key="order.id"
          :order="order"
          :is-admin="true"
          :next-status-label="NEXT_LABEL_MAP[order.status]"
          @view-detail="handleViewDetail"
          @advance="handleAdvance"
          @print="handlePrint"
        />
      </div>

      <p v-if="!loading && !orders.length" class="py-16 text-center text-muted-foreground">
        Không có đơn hàng nào.
      </p>
    </main>

    <OrderDetailModal v-model:show="showDetail" :order="selectedOrder" :is-admin="true" />

    <div class="hidden print:block">
      <OrderInvoice v-if="printingOrder" :order="printingOrder" />
    </div>
  </div>
</template>

<style>
@media print {
  body * {
    visibility: hidden;
  }
  .print\:block,
  .print\:block * {
    visibility: visible;
  }
  .print\:block {
    position: absolute;
    top: 0;
    left: 0;
  }
}
</style>

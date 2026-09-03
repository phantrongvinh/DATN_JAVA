<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink } from 'vue-router'

import { useAuthStore } from '@/stores/useAuthStore'
import ulti from '@/ulti/ulti'
import { useOrderStore } from '@/stores/useOrderStore'
import { storeToRefs } from 'pinia'
import { useCheckout } from '@/composables/useCheckout'
import ReturnRequestModal from '@/components/ReturnRequestModal.vue'

const authStore = useAuthStore()
const orderStore = useOrderStore()

const { user } = storeToRefs(useAuthStore)
const { myOrders } = storeToRefs(orderStore)
const { repayVnpay } = useCheckout()

const loadOrder = async () => {
  await orderStore.fetchMyOrder()
}

onMounted(async () => {
  await loadOrder()
})

watch(
  user,
  async (newVal) => {
    if (!newVal) {
      myOrders.value = []
      return
    }

    await loadOrder()
  },
  { immediate: true },
)

const STATUS_LABEL = {
  PENDING: {
    label: 'Chờ xác nhận',
    tone: 'bg-amber-100 text-amber-800',
  },
  CONFIRMED: {
    label: 'Đã xác nhận',
    tone: 'bg-blue-100 text-blue-800',
  },
  PICKED: {
    label: 'Đã lấy hàng',
    tone: 'bg-cyan-100 text-cyan-800',
  },
  SHIPPING: {
    label: 'Đang giao',
    tone: 'bg-purple-100 text-purple-800',
  },
  DELIVERED: {
    label: 'Đã giao',
    tone: 'bg-green-100 text-green-800',
  },
  CANCELLED: {
    label: 'Đã hủy',
    tone: 'bg-red-100 text-red-800',
  },
  RETURN_REQUESTED: {
    label: 'Đang hoàn đơn',
    tone: 'bg-orange-100 text-orange-800',
  },
  RETURNED: {
    label: 'Đã hoàn đơn',
    tone: 'bg-yellow-100 text-yellow-800',
  },
}

// handle cancel order
const canCancel = (order) => {
  if (order.paymentMethod === 'COD') {
    return ['PENDING', 'CONFIRMED'].includes(order.status)
  }

  if (order.paymentMethod === 'VNPAY') {
    return order.paymentStatus === 'PAID' && order.status === 'CONFIRMED'
  }
}

const handleCancelOrder = async (order) => {
  try {
    await orderStore.cancelOrder(order.id)
  } catch (error) {
    console.log('Hủy đơn hàng thất bại', error)
  }
}

// handle repay order
const canPay = (order) => {
  return (
    order.paymentMethod === 'VNPAY' &&
    order.paymentStatus === 'PENDING' &&
    ['PENDING'].includes(order.status)
  )
}

const handleRepay = async (order) => {
  try {
    await repayVnpay(order.id)
  } catch (error) {
    console.log('Thanh toán lại thất bại', error)
  }
}

const canRequestReturn = (order) => {
  if (order.status !== 'DELIVERED' || !order.deliveredAt) return false
  const deadline = new Date(order.deliveredAt)
  deadline.setDate(deadline.getDate() + 7)
  return new Date() < deadline
}
const handleReturnSuccess = async () => {
  await loadOrder()
}
const showReturnModal = ref(false)
const returnOrder = ref(null)

const openModalReturn = (order) => {
  showReturnModal.value = true
  returnOrder.value = order
}
</script>

<template>
  <div class="border border-border">
    <!-- Header -->
    <div class="border-b border-border bg-secondary/20 p-6 py-7">
      <h2 class="font-display text-2xl">Đơn hàng của tôi</h2>

      <p class="mt-1 text-sm text-muted-foreground">Theo dõi trạng thái các đơn hàng gần đây</p>
    </div>

    <!-- Orders -->
    <ul v-if="myOrders && myOrders.length > 0" class="divide-y divide-border">
      <li v-for="order in myOrders" :key="order.id" class="flex justify-between gap-4 p-6">
        <div>
          <RouterLink :to="`/orders/${order.id}`">
            <p class="font-medium">#{{ order.id }}</p>
          </RouterLink>

          <div class="mt-2 flex flex-wrap items-center gap-2 text-xs text-muted-foreground">
            <span>{{ ulti.formatDate(order.createdAt) }}</span>

            <span>•</span>

            <span>{{ order.items?.length }} sản phẩm</span>

            <span>•</span>

            <span class="rounded bg-secondary px-2 py-0.5 font-medium">
              {{ order.paymentMethod }}
            </span>
          </div>
        </div>

        <div class="min-w-[220px] text-right">
          <!-- Tổng tiền -->
          <p class="font-display text-lg">
            {{ ulti.formatVND(order.price) }}
          </p>

          <!-- Trạng thái đơn -->
          <span
            class="mt-2 inline-block rounded-full px-3 py-1 text-[10px] font-semibold uppercase tracking-widest"
            :class="STATUS_LABEL[order.status]?.tone"
          >
            {{ STATUS_LABEL[order.status]?.label }}
          </span>

          <!-- Thanh toán -->
          <p class="mt-2 text-xs text-muted-foreground">
            Thanh toán:
            <span
              class="font-medium"
              :class="{
                'text-emerald-600': order.paymentStatus === 'PAID',
                'text-orange-500': order.paymentStatus === 'UNPAID',
                'text-red-500': order.paymentStatus === 'REFUNDED',
              }"
            >
              {{ order.paymentStatus }}
            </span>
          </p>

          <div
            v-if="
              order.paymentMethod === 'VNPAY' &&
              order.paymentStatus === 'UNPAID' &&
              order.status === 'PENDING'
            "
            class="mt-3 rounded border border-amber-200 bg-amber-50 px-3 py-2 text-left"
          >
            <p class="text-xs font-medium text-amber-700">Đơn hàng đang chờ thanh toán</p>

            <p class="mt-1 text-[11px] text-amber-600">
              Bạn chưa hoàn tất thanh toán VNPay. Có thể tiếp tục thanh toán bất cứ lúc nào.
            </p>
          </div>

          <!-- Action -->
          <div class="mt-4 flex justify-end gap-2">
            <button
              v-if="canRequestReturn(order)"
              @click="openModalReturn(order)"
              class="rounded border border-yellow-600 px-3 py-2 text-xs font-medium text-yellow-600 transition hover:bg-yellow-600 hover:text-white cursor-pointer"
            >
              Yêu cầu trả hàng
            </button>

            <span
              v-else-if="order.status === 'RETURN_REQUESTED'"
              class="rounded border border-gray-400 px-3 py-2 text-xs font-medium text-gray-500"
            >
              Đang chờ duyệt
            </span>

            <span
              v-else-if="order.status === 'RETURNED'"
              class="rounded border border-green-600 px-3 py-2 text-xs font-medium text-green-600"
            >
              Đã hoàn tất hoàn đơn
            </span>
            <button
              v-if="canPay(order)"
              @click="handleRepay(order)"
              class="rounded border border-emerald-600 px-3 py-2 text-xs font-medium text-emerald-600 transition hover:bg-emerald-600 hover:text-white cursor-pointer"
            >
              Thanh toán
            </button>

            <button
              v-if="canCancel(order)"
              @click="handleCancelOrder(order)"
              class="rounded border border-red-500 px-3 py-2 text-xs font-medium text-red-500 transition hover:bg-red-500 hover:text-white cursor-pointer"
            >
              Hủy đơn
            </button>
          </div>
        </div>
        <ReturnRequestModal
          v-model:show="showReturnModal"
          :order="returnOrder"
          @success="handleReturnSuccess"
        ></ReturnRequestModal>
      </li>
    </ul>

    <!-- Empty -->

    <div v-else class="p-12 text-center text-sm text-muted-foreground">
      Bạn chưa có đơn hàng nào.

      <div class="mt-4">
        <RouterLink to="/products" class="border-b border-foreground pb-0.5 text-foreground">
          Khám phá sản phẩm
        </RouterLink>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, watch } from 'vue'
import { RouterLink } from 'vue-router'

import { useAuthStore } from '@/stores/useAuthStore'
import ulti from '@/ulti/ulti'
import { useOrderStore } from '@/stores/useOrderStore'
import { storeToRefs } from 'pinia'

const authStore = useAuthStore()
const orderStore = useOrderStore()

const { user } = storeToRefs(useAuthStore)
const { myOrders } = storeToRefs(orderStore)

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
    tone: 'bg-muted text-muted-foreground',
  },
  CONFIRMED: {
    label: 'Đang xử lý',
    tone: 'bg-gold-soft text-ink',
  },
  SHIPPING: {
    label: 'Đang giao',
    tone: 'bg-ink/10 text-ink',
  },
  DELIVERED: {
    label: 'Đã giao',
    tone: 'bg-emerald-100 text-emerald-800',
  },
  CANCELLED: {
    label: 'Đã hủy',
    tone: 'bg-red-100 text-red-700',
  },
}
</script>

<template>
  <div class="border border-border">
    <!-- Header -->
    <div class="border-b border-border p-6">
      <h2 class="font-display text-2xl">Đơn hàng của tôi</h2>

      <p class="mt-1 text-sm text-muted-foreground">Theo dõi trạng thái các đơn hàng gần đây</p>
    </div>

    <!-- Empty -->
    <div v-if="myOrders.length === 0" class="p-12 text-center text-sm text-muted-foreground">
      Bạn chưa có đơn hàng nào.

      <div class="mt-4">
        <RouterLink to="/products" class="border-b border-foreground pb-0.5 text-foreground">
          Khám phá sản phẩm
        </RouterLink>
      </div>
    </div>

    <!-- Orders -->
    <ul v-else class="divide-y divide-border">
      <li
        v-for="order in myOrders"
        :key="order.id"
        class="flex items-center justify-between gap-4 p-6"
      >
        <div>
          <RouterLink :to="`/orders/${order.id}`">
            <p class="font-medium">#{{ order.trackingCode }}</p>
          </RouterLink>

          <p class="mt-1 text-xs text-muted-foreground">
            {{ ulti.formatDate(order.createdAt) }}

            ·

            {{ order.items.length }}
            sản phẩm ·

            {{ order.paymentMethod }}
          </p>
        </div>

        <div class="text-right">
          <p class="font-display text-lg">
            {{ ulti.formatVND(order.price) }}
          </p>

          <span
            class="mt-1 inline-block px-2 py-0.5 text-[10px] uppercase tracking-widest"
            :class="STATUS_LABEL[order.status]?.tone"
          >
            {{ STATUS_LABEL[order.status]?.label }}
          </span>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { Printer, RotateCcw } from 'lucide-vue-next'
import ulti from '@/ulti/ulti'

const props = defineProps({
  order: { type: Object, required: true },
  isAdmin: { type: Boolean, default: false },
  nextStatusLabel: { type: String, default: null },
})

const emit = defineEmits(['view-detail', 'advance', 'print', 'return'])

const STATUS_CONFIG = {
  PENDING: { label: 'Chờ thanh toán', tone: 'bg-yellow-100 text-yellow-700' },
  CONFIRMED: { label: 'Đã xác nhận', tone: 'bg-blue-100 text-blue-700' },
  SHIPPING: { label: 'Đang giao', tone: 'bg-purple-100 text-purple-700' },
  DELIVERED: { label: 'Đã giao', tone: 'bg-green-100 text-green-700' },
  CANCELLED: { label: 'Đã hủy', tone: 'bg-red-100 text-red-700' },
  RETURN_REQUESTED: { label: 'Đang xử lý trả hàng', tone: 'bg-orange-100 text-orange-700' },
  RETURNED: { label: 'Đã trả hàng', tone: 'bg-gray-200 text-gray-700' },
}
</script>

<template>
  <div class="border border-border bg-background p-5">
    <div class="flex items-start justify-between">
      <div>
        <p class="font-medium">Đơn #{{ order.id }}</p>
        <p class="mt-1 text-xs text-muted-foreground">{{ ulti.formatDate(order.createdAt) }}</p>
      </div>
      <span class="rounded-full px-3 py-1 text-xs" :class="STATUS_CONFIG[order.status]?.tone">
        {{ STATUS_CONFIG[order.status]?.label }}
      </span>
    </div>

    <div class="mt-3 space-y-1 text-sm text-muted-foreground">
      <p>{{ order.receiverName }} · {{ order.receiverPhone }}</p>
      <p v-if="order.trackingCode">
        Mã vận đơn: <span class="font-mono">{{ order.trackingCode }}</span>
      </p>
    </div>

    <div class="mt-3 flex items-center justify-between border-t border-border pt-3">
      <p class="font-display text-lg">{{ ulti.formatVND(order.finalPrice) }}</p>

      <div class="flex gap-2">
        <button
          @click="emit('view-detail', order)"
          class="border border-border px-3 py-2 text-xs hover:bg-secondary"
        >
          Chi tiết
        </button>

        <button
          @click="emit('print', order)"
          class="border border-border p-2 hover:bg-secondary"
          aria-label="In đơn"
        >
          <Printer class="h-4 w-4" />
        </button>

        <!-- Admin: nút chuyển trạng thái TIẾP THEO duy nhất -->
        <button
          v-if="isAdmin && nextStatusLabel"
          @click="emit('advance', order)"
          class="bg-ink px-3 py-2 text-xs uppercase tracking-widest text-ivory hover:bg-ink/90"
        >
          {{ nextStatusLabel }}
        </button>

        <!-- User: nút yêu cầu trả hàng -->
        <button
          v-if="!isAdmin && order.canRequestReturn"
          @click="emit('return', order)"
          class="flex items-center gap-1 border border-red-300 px-3 py-2 text-xs text-red-600 hover:bg-red-50"
        >
          <RotateCcw class="h-3.5 w-3.5" /> Trả hàng
        </button>
      </div>
    </div>
  </div>
</template>

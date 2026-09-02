<script setup>
import { X, Printer, RotateCcw } from 'lucide-vue-next'
import { nextTick, ref } from 'vue'
import ulti from '@/ulti/ulti'
import OrderInvoice from './OrderInvoice.vue'
import ReturnRequestModal from './ReturnRequestModal.vue'

const props = defineProps({
  show: Boolean,
  order: Object,
  isAdmin: { type: Boolean, default: false },
})
const emit = defineEmits(['update:show'])

const close = () => emit('update:show', false)

const showPrintArea = ref(false)
const handlePrint = () => {
  showPrintArea.value = true
  nextTick(() => window.print())
}

const showReturnModal = ref(false)
</script>

<template>
  <div
    v-if="show && order"
    class="fixed inset-0 z-50 flex items-center justify-center bg-ink/60 p-4"
  >
    <div class="flex max-h-[90vh] w-full max-w-2xl flex-col bg-background">
      <div class="flex items-center justify-between border-b border-border px-6 py-4">
        <h2 class="font-display text-xl">Chi tiết đơn hàng #{{ order.id }}</h2>
        <button @click="close" class="p-1 hover:bg-secondary"><X class="h-4 w-4" /></button>
      </div>

      <div class="flex-1 overflow-y-auto p-6">
        <div class="grid grid-cols-2 gap-4 text-sm">
          <div>
            <p class="text-xs uppercase text-muted-foreground">Người nhận</p>
            <p class="mt-1">{{ order.receiverName }}</p>
            <p>{{ order.receiverPhone }}</p>
          </div>
          <div>
            <p class="text-xs uppercase text-muted-foreground">Địa chỉ</p>
            <p class="mt-1">{{ order.shippingAddress }}</p>
          </div>
        </div>

        <div class="mt-6 space-y-3">
          <div
            v-for="item in order.items"
            :key="item.productVariantId"
            class="flex justify-between border-b border-border pb-3 text-sm"
          >
            <div>
              <p class="font-medium">{{ item.productName }}</p>
              <p class="text-muted-foreground">
                {{ item.color }} · {{ item.sizeName }} · x{{ item.quantity }}
              </p>
            </div>
            <p>{{ ulti.formatVND(item.price * item.quantity) }}</p>
          </div>
        </div>

        <div class="mt-4 flex justify-end">
          <p class="font-display text-xl">{{ ulti.formatVND(order.finalPrice) }}</p>
        </div>
      </div>

      <div class="flex items-center justify-between border-t border-border px-6 py-4">
        <button
          @click="handlePrint"
          class="flex items-center gap-2 border border-border px-4 py-2 text-sm hover:bg-secondary"
        >
          <Printer class="h-4 w-4" /> In đơn
        </button>

        <button
          v-if="!isAdmin && order.canRequestReturn"
          @click="showReturnModal = true"
          class="flex items-center gap-2 border border-red-300 px-4 py-2 text-sm text-red-600 hover:bg-red-50"
        >
          <RotateCcw class="h-4 w-4" /> Yêu cầu trả hàng
        </button>
      </div>
    </div>

    <div v-if="showPrintArea" class="hidden print:block">
      <OrderInvoice :order="order" />
    </div>

    <ReturnRequestModal v-model:show="showReturnModal" :order="order" />
  </div>
</template>

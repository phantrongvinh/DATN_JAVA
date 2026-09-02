<script setup>
import { nextTick, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { Printer, Package, User, Phone, MapPin, ShoppingBag, ChevronDown } from 'lucide-vue-next'

import ulti from '@/ulti/ulti'
import { useOrderOperationsStore } from '@/stores/userOrderOperationStore'
import { useNotificationStore } from '@/stores/useNotificationStore'
import OrderInvoice from '@/components/OrderInvoice.vue'

const store = useOrderOperationsStore()
const notification = useNotificationStore()

const { orders, loading, loadingMore, currentPage, totalPages, totalElements } = storeToRefs(store)

onMounted(() => {
  store.fetchPendingOrders()
})

const NEXT_ACTION = {
  PENDING: {
    label: 'Xác nhận đơn',
    action: 'confirmOrder',
  },
}

const handleAction = async (order) => {
  const config = NEXT_ACTION[order.status]

  if (!config) return

  try {
    await store[config.action](order.id)

    notification.notify(`Đã xác nhận đơn hàng #${order.id}`, 'success')

    await store.fetchPendingOrders()
  } catch (err) {
    notification.notify(err.response?.data?.message ?? 'Có lỗi xảy ra', 'error')
  }
}

const handleLoadMore = async () => {
  await store.fetchPendingOrders(true)
}

const printingOrder = ref(null)

const handlePrint = (order) => {
  printingOrder.value = order

  nextTick(() => {
    window.print()
  })
}
</script>

<template>
  <div class="flex-1">
    <!-- Header -->
    <header class="border-b border-border bg-background px-6 py-4">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="font-display text-2xl">Xử lý đơn hàng</h1>

          <p class="mt-1 text-sm text-muted-foreground">Các đơn hàng đang chờ xác nhận</p>
        </div>

        <div v-if="totalElements > 0" class="text-sm text-muted-foreground">
          {{ orders.length }} / {{ totalElements }} đơn
        </div>
      </div>
    </header>

    <main class="p-6">
      <!-- Loading lần đầu -->
      <div v-if="loading" class="flex min-h-[400px] items-center justify-center">
        <div class="text-sm text-muted-foreground">Đang tải đơn hàng...</div>
      </div>

      <!-- Không có đơn hàng -->
      <div
        v-else-if="orders.length === 0"
        class="border border-border bg-background py-20 text-center"
      >
        <div class="flex justify-center">
          <div class="flex h-16 w-16 items-center justify-center rounded-full bg-secondary">
            <Package class="h-8 w-8 text-muted-foreground" />
          </div>
        </div>

        <h2 class="mt-5 font-display text-xl">Không có đơn hàng</h2>

        <p class="mx-auto mt-2 max-w-md text-sm text-muted-foreground">
          Hiện tại không có đơn hàng nào đang chờ xác nhận. Các đơn hàng mới sẽ xuất hiện tại đây.
        </p>
      </div>

      <!-- Danh sách card -->
      <div v-else class="space-y-4">
        <div
          v-for="order in orders"
          :key="order.id"
          class="border border-border bg-background transition hover:border-foreground/30"
        >
          <!-- Card header -->
          <div
            class="flex flex-col gap-3 border-b border-border px-5 py-4 sm:flex-row sm:items-center sm:justify-between"
          >
            <div class="flex items-center gap-3">
              <div class="flex h-10 w-10 items-center justify-center rounded-full bg-secondary">
                <ShoppingBag class="h-5 w-5" />
              </div>

              <div>
                <div class="font-medium">Đơn hàng #{{ order.id }}</div>

                <div class="mt-1 text-xs text-muted-foreground">
                  {{ order.createdAt || '---' }}
                </div>
              </div>
            </div>

            <!-- Status -->
            <span
              class="inline-flex w-fit rounded-full bg-yellow-100 px-3 py-1 text-xs font-medium text-yellow-700"
            >
              Chờ xác nhận
            </span>
          </div>

          <!-- Card body -->
          <div class="grid gap-6 px-5 py-5 md:grid-cols-2">
            <!-- Customer -->
            <div class="space-y-4">
              <h3 class="text-xs font-medium uppercase tracking-widest text-muted-foreground">
                Thông tin khách hàng
              </h3>

              <div class="space-y-3">
                <div class="flex items-start gap-3">
                  <User class="mt-0.5 h-4 w-4 shrink-0 text-muted-foreground" />

                  <div>
                    <p class="text-xs text-muted-foreground">Người nhận</p>
                    <p class="mt-0.5 font-medium">
                      {{ order.receiverName || '---' }}
                    </p>
                  </div>
                </div>

                <div class="flex items-start gap-3">
                  <Phone class="mt-0.5 h-4 w-4 shrink-0 text-muted-foreground" />

                  <div>
                    <p class="text-xs text-muted-foreground">Số điện thoại</p>
                    <p class="mt-0.5">
                      {{ order.receiverPhone || '---' }}
                    </p>
                  </div>
                </div>

                <div class="flex items-start gap-3">
                  <MapPin class="mt-0.5 h-4 w-4 shrink-0 text-muted-foreground" />

                  <div>
                    <p class="text-xs text-muted-foreground">Địa chỉ giao hàng</p>

                    <p class="mt-0.5">
                      {{ order.shippingDetail || '---' }}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Order information -->
            <div class="space-y-4">
              <h3 class="text-xs font-medium uppercase tracking-widest text-muted-foreground">
                Thông tin đơn hàng
              </h3>

              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm text-muted-foreground"> Tạm tính </span>

                  <span class="font-medium">
                    {{ ulti.formatVND(order.totalPrice) }}
                  </span>
                </div>

                <div class="flex items-center justify-between">
                  <span class="text-sm text-muted-foreground"> Phí vận chuyển </span>

                  <span>
                    {{ ulti.formatVND(order.shippingFee || 0) }}
                  </span>
                </div>

                <div class="flex items-center justify-between border-t border-border pt-3">
                  <span class="font-medium"> Tổng tiền </span>

                  <span class="text-lg font-semibold">
                    {{ ulti.formatVND(order.finalPrice) }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <!-- Card footer -->
          <div
            class="flex flex-col gap-3 border-t border-border bg-secondary/20 px-5 py-4 sm:flex-row sm:items-center sm:justify-end"
          >
            <!-- Confirm -->
            <button
              v-if="NEXT_ACTION[order.status]"
              @click="handleAction(order)"
              class="inline-flex items-center justify-center bg-ink px-5 py-2.5 text-xs uppercase tracking-widest text-ivory transition hover:bg-ink/90"
            >
              {{ NEXT_ACTION[order.status].label }}
            </button>

            <!-- Print -->
            <button
              @click="handlePrint(order)"
              class="inline-flex items-center justify-center gap-2 border border-border bg-background px-4 py-2.5 text-xs uppercase tracking-widest transition hover:bg-secondary"
            >
              <Printer class="h-4 w-4" />
              In đơn
            </button>
          </div>
        </div>

        <!-- Load more -->
        <div v-if="currentPage <= totalPages" class="flex justify-center pt-4">
          <button
            @click="handleLoadMore"
            :disabled="loadingMore"
            class="inline-flex items-center gap-2 border border-border bg-background px-6 py-3 text-xs uppercase tracking-widest transition hover:bg-secondary disabled:cursor-not-allowed disabled:opacity-50"
          >
            <ChevronDown class="h-4 w-4" />

            <span v-if="loadingMore"> Đang tải... </span>

            <span v-else> Xem thêm đơn hàng </span>
          </button>
        </div>

        <!-- Đã hết -->
        <div v-else-if="orders.length > 0" class="pt-4 text-center text-xs text-muted-foreground">
          Đã hiển thị tất cả {{ totalElements }} đơn hàng
        </div>
      </div>
    </main>

    <!-- Vùng in -->
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

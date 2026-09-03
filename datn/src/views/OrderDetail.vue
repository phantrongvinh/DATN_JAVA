<script setup>
import { RouterLink, useRoute } from 'vue-router'
import { CheckCircle2, Printer } from 'lucide-vue-next'
import ulti from '@/ulti/ulti'
import { useOrderStore } from '@/stores/useOrderStore'
import { storeToRefs } from 'pinia'
import { computed, nextTick, onMounted, ref, watch } from 'vue'
import OrderInvoice from '@/components/OrderInvoice.vue'

const route = useRoute()

const orderId = computed(() => route.params.id)

const orderStore = useOrderStore()

const { order } = storeToRefs(orderStore)

const loadOrder = async (id) => {
  if (!id) return
  await orderStore.fetchOrderById(id)
}

onMounted(async () => {
  await loadOrder(orderId.value)
})

watch(orderId, async (newVal) => {
  if (!newVal) return
  await loadOrder(newVal)
})

// Handle status

const STATUS_STEPS = [
  {
    key: 'PENDING',
    label: 'Chờ xác nhận',
    description: 'Đơn hàng đang chờ cửa hàng xác nhận',
  },
  {
    key: 'CONFIRMED',
    label: 'Đã xác nhận',
    description: 'Cửa hàng đã xác nhận đơn hàng',
  },
  {
    key: 'PICKED',
    label: 'Đã lấy hàng',
    description: 'Shipper đã nhận hàng từ cửa hàng',
  },
  {
    key: 'SHIPPING',
    label: 'Đang giao hàng',
    description: 'Đơn hàng đang được giao đến bạn',
  },
  {
    key: 'DELIVERED',
    label: 'Đã giao hàng',
    description: 'Đơn hàng đã được giao thành công',
  },
]

const STATUS_LABEL = {
  PENDING: {
    label: 'Chờ xác nhận',
    tone: 'bg-muted text-muted-foreground',
  },

  CONFIRMED: {
    label: 'Đã xác nhận',
    tone: 'bg-gold-soft text-ink',
  },

  PICKED: {
    label: 'Đã lấy hàng',
    tone: 'bg-blue-100 text-blue-700',
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

const STATUS_HISTORY_LABEL = {
  PENDING: 'Chờ xác nhận',
  CONFIRMED: 'Đã xác nhận',
  PICKED: 'Đã lấy hàng',
  SHIPPING: 'Đang giao hàng',
  DELIVERED: 'Đã giao hàng',
  CANCELLED: 'Đã hủy',
  RETURN_REQUESTED: 'Yêu cầu trả hàng',
  RETURNED: 'Đã trả hàng',
}

const RETURN_STATUS_LABEL = {
  PENDING: {
    label: 'Chờ duyệt',
    tone: 'bg-yellow-100 text-yellow-700',
  },

  APPROVED: {
    label: 'Đã duyệt',
    tone: 'bg-blue-100 text-blue-700',
  },

  REJECTED: {
    label: 'Từ chối',
    tone: 'bg-red-100 text-red-700',
  },

  PICKED: {
    label: 'Đã lấy hàng',
    tone: 'bg-indigo-100 text-indigo-700',
  },

  DELIVERING: {
    label: 'Đang hoàn hàng',
    tone: 'bg-orange-100 text-orange-700',
  },

  COMPLETED: {
    label: 'Đã hoàn tất',
    tone: 'bg-green-100 text-green-700',
  },
}

const currentStepIndex = computed(() => {
  if (!order.value?.status) return -1

  return STATUS_STEPS.findIndex((step) => step.key === order.value.status)
})

const statusHistory = computed(() => order.value?.statusHistory ?? [])

const returnRequests = computed(() => order.value?.returnRequests ?? [])

const returnCount = computed(() => returnRequests.value.length)

const printingOrder = ref(null)

const handlePrint = (order) => {
  printingOrder.value = order

  nextTick(() => {
    window.print()
  })
}
</script>

<template>
  <div class="hidden print:block">
    <OrderInvoice v-if="printingOrder" :order="printingOrder" />
  </div>
  <div class="container-x py-16">
    <!-- SUCCESS -->
    <div class="mx-auto max-w-3xl text-center">
      <CheckCircle2 class="mx-auto h-20 w-20 text-green-500" :stroke-width="1.5" />

      <h1 class="mt-6 font-display text-5xl">Đặt hàng thành công</h1>

      <p class="mt-4 text-muted-foreground">Cảm ơn bạn đã mua sắm tại Maison Calcio.</p>

      <p class="mt-2 text-sm text-muted-foreground">
        Mã đơn hàng:
        <span class="font-semibold text-foreground"> #{{ order?.id }} </span>
      </p>

      <div class="mt-8 flex justify-center gap-3">
        <RouterLink
          to="/profile/orders"
          class="bg-ink px-6 py-3 text-xs uppercase tracking-widest text-ivory"
        >
          Xem đơn hàng
        </RouterLink>

        <RouterLink
          to="/products"
          class="border border-border px-6 py-3 text-xs uppercase tracking-widest"
        >
          Tiếp tục mua sắm
        </RouterLink>
      </div>
    </div>

    <!-- INVOICE -->
    <div class="mx-auto mt-16 max-w-6xl">
      <button
        @click="handlePrint(order)"
        class="flex items-end gap-2 border border-border px-4 py-2 text-sm hover:bg-secondary my-2"
      >
        <Printer class="h-4 w-4" /> In đơn hàng
      </button>
      <div class="grid gap-8 lg:grid-cols-[1fr_380px] items-stretch">
        <!-- LEFT -->
        <div>
          <!-- ORDER INFO -->
          <div class="border border-border p-6">
            <h2 class="font-display text-2xl">Thông tin đơn hàng</h2>

            <div class="mt-6 grid gap-5 md:grid-cols-2">
              <div>
                <p class="text-xs uppercase tracking-widest text-muted-foreground">Mã đơn</p>

                <p class="mt-1">#{{ order?.id }}</p>
              </div>

              <div>
                <p class="text-xs uppercase tracking-widest text-muted-foreground">Ngày đặt</p>

                <p class="mt-1">
                  {{ ulti.formatDate(order?.createdAt) }}
                </p>
              </div>

              <div>
                <p class="text-xs uppercase tracking-widest text-muted-foreground">
                  Trạng thái thanh toán
                </p>

                <span
                  class="mt-2 inline-block rounded-full px-3 py-1 text-[10px] font-semibold uppercase tracking-widest"
                  :class="
                    order?.paymentStatus === 'PAID'
                      ? 'bg-emerald-100 text-emerald-700'
                      : order?.paymentStatus === 'REFUNDED'
                        ? 'bg-blue-100 text-blue-700'
                        : 'bg-yellow-100 text-yellow-700'
                  "
                >
                  {{
                    order?.paymentStatus === 'PAID'
                      ? 'Đã thanh toán'
                      : order?.paymentStatus === 'REFUNDED'
                        ? 'Đã hoàn tiền'
                        : 'Chưa thanh toán'
                  }}
                </span>
              </div>

              <div>
                <p class="text-xs uppercase tracking-widest text-muted-foreground">Thanh toán</p>

                <p class="mt-1">
                  {{ order?.paymentMethod }}
                </p>
              </div>

              <div>
                <p class="text-xs uppercase tracking-widest text-muted-foreground">Mã vận đơn</p>

                <p class="mt-1">#{{ order?.trackingCode }}</p>
              </div>
            </div>
          </div>

          <!-- SHIPPING -->
          <div class="mt-6 border border-border p-6">
            <h2 class="font-display text-2xl">Thông tin giao hàng</h2>

            <div class="mt-5">
              <p class="font-medium">
                {{ order?.receiverName }}
              </p>

              <p class="text-sm text-muted-foreground">
                {{ order?.receiverPhone }}
              </p>

              <p class="mt-2">
                {{ order?.shippingAddress }}
              </p>
            </div>
          </div>

          <!-- PRODUCTS -->
          <div class="mt-6 border border-border">
            <div class="border-b border-border p-6">
              <h2 class="font-display text-2xl">Sản phẩm đã mua</h2>
            </div>

            <ul>
              <li
                v-for="item in order?.items"
                :key="item.id"
                class="flex gap-4 border-b border-border p-6 last:border-0"
              >
                <div class="flex-1">
                  <h3 class="font-medium">
                    {{ item.productName }}
                  </h3>

                  <p class="mt-1 text-sm text-muted-foreground">
                    Size: {{ item.sizeName }}
                    ·
                    {{ item.color }}
                  </p>

                  <p class="mt-1 text-sm text-muted-foreground">
                    Số lượng:
                    {{ item.quantity }}
                  </p>
                </div>

                <div class="font-display text-lg">
                  {{ ulti.formatVND(item.price) }}
                </div>
                <div
                  v-if="order.status === 'DELIVERED' && !item.reviewed"
                  class="flex items-center justify-between border border-gold/30 bg-gold/5 p-3 text-sm"
                >
                  <span>Đánh giá sản phẩm để nhận {{ estimatedPoints }} điểm tích lũy</span>
                  <button @click="openReviewModal(item)" class="text-gold underline">
                    Đánh giá ngay
                  </button>
                </div>
              </li>
            </ul>
          </div>
        </div>

        <!-- RIGHT -->
        <aside class="sticky top-24 h-fit border border-border p-6">
          <h2 class="font-display text-2xl">Hóa đơn</h2>

          <div class="mt-6 space-y-3">
            <div class="flex justify-between">
              <span>Tạm tính</span>
              <span>
                {{ ulti.formatVND(order?.totalPrice) }}
              </span>
            </div>

            <div class="flex justify-between">
              <span>Phí vận chuyển</span>
              <span>
                {{ ulti.formatVND(order?.shippingFee) }}
              </span>
            </div>

            <div v-if="order?.voucherCode" class="flex justify-between text-green-600">
              <span>Voucher</span>

              <span> -{{ ulti.formatVND(order?.discountAmount) }} </span>
            </div>

            <div v-if="order?.timeDiscount > 0" class="flex justify-between text-green-600">
              <span>Giờ vàng</span>

              <span> -{{ ulti.formatVND(order?.timeDiscount) }} </span>
            </div>

            <div class="border-t border-border pt-4">
              <div class="flex justify-between font-display text-2xl">
                <span>Tổng cộng</span>

                <span class="text-red-500">
                  {{ ulti.formatVND(order?.finalPrice) }}
                </span>
              </div>
            </div>
          </div>
          <div
            v-if="order?.status === 'CANCELLED'"
            class="rounded-lg border border-red-200 bg-red-50 p-4 mt-4"
          >
            <p class="font-medium text-red-600">Đơn hàng đã bị hủy</p>
            <p class="text-sm text-red-500">Đơn hàng không tiếp tục quy trình giao hàng.</p>
          </div>

          <div v-else>
            <!-- ORDER STATUS -->
            <div class="mt-8 border-t border-border pt-6">
              <div class="flex items-center justify-between">
                <h3 class="text-sm font-semibold uppercase tracking-widest">Tiến trình đơn hàng</h3>

                <span
                  v-if="order?.status"
                  class="rounded-full px-3 py-1 text-[10px] font-semibold uppercase tracking-widest"
                  :class="STATUS_LABEL[order.status]?.tone"
                >
                  {{ STATUS_LABEL[order.status]?.label }}
                </span>
              </div>

              <div class="mt-6 space-y-5">
                <div v-for="(status, index) in STATUS_STEPS" :key="status.key" class="flex gap-3">
                  <!-- DOT + LINE -->
                  <div class="flex flex-col items-center">
                    <div
                      class="mt-1 flex h-5 w-5 items-center justify-center rounded-full"
                      :class="index <= currentStepIndex ? 'bg-green-500' : 'bg-gray-200'"
                    >
                      <CheckCircle2 v-if="index <= currentStepIndex" class="h-3 w-3 text-white" />
                    </div>

                    <div
                      v-if="index < STATUS_STEPS.length - 1"
                      class="mt-1 w-0.5 flex-1"
                      :class="index < currentStepIndex ? 'bg-green-500' : 'bg-gray-200'"
                    />
                  </div>

                  <!-- CONTENT -->
                  <div class="pb-5">
                    <p
                      class="font-medium"
                      :class="
                        index === currentStepIndex
                          ? 'text-green-600'
                          : index < currentStepIndex
                            ? 'text-gray-500'
                            : 'text-gray-300'
                      "
                    >
                      {{ status.label }}
                    </p>

                    <p
                      class="mt-1 text-xs text-muted-foreground"
                      :class="{
                        'text-green-600': index === currentStepIndex,
                      }"
                    >
                      {{ status.description }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- STATUS HISTORY -->
          <div class="mt-8 border-t border-border pt-6">
            <div class="flex items-center justify-between">
              <h3 class="text-sm font-semibold uppercase tracking-widest">Lịch sử đơn hàng</h3>

              <span class="text-xs text-muted-foreground">
                {{ statusHistory.length }} lần cập nhật
              </span>
            </div>

            <div class="mt-5 space-y-4">
              <div
                v-for="history in statusHistory"
                :key="history.id"
                class="relative border-l-2 border-green-200 pl-4"
              >
                <div class="absolute -left-[6px] top-1 h-2.5 w-2.5 rounded-full bg-green-500" />

                <div class="flex items-start justify-between gap-3">
                  <div>
                    <p class="font-medium">
                      {{ STATUS_HISTORY_LABEL[history.status] || history.status }}
                    </p>

                    <p v-if="history.note" class="mt-1 text-xs text-muted-foreground">
                      {{ history.note }}
                    </p>
                  </div>

                  <span class="shrink-0 text-[11px] text-muted-foreground">
                    {{ ulti.formatDate(history.createdAt) }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <div v-if="returnCount > 0" class="mt-8 border-t border-border pt-6">
            <div class="flex items-center justify-between">
              <h3 class="text-sm font-semibold uppercase tracking-widest">Yêu cầu trả hàng</h3>

              <span class="rounded-full bg-gray-100 px-3 py-1 text-xs">
                {{ returnCount }} lần
              </span>
            </div>

            <div class="mt-5 space-y-4">
              <div
                v-for="request in returnRequests"
                :key="request.id"
                class="rounded-lg border border-border p-4"
              >
                <div class="flex items-center justify-between">
                  <p class="font-medium">Yêu cầu #{{ request.id }}</p>

                  <span
                    class="rounded-full px-3 py-1 text-[10px] font-semibold"
                    :class="RETURN_STATUS_LABEL[request.status]?.tone"
                  >
                    {{ RETURN_STATUS_LABEL[request.status]?.label }}
                  </span>
                </div>

                <div class="mt-3 space-y-2 text-sm">
                  <p>
                    <span class="text-muted-foreground">Lý do:</span>
                    {{ request.reason }}
                  </p>

                  <p>
                    <span class="text-muted-foreground">Ngày yêu cầu:</span>
                    {{ ulti.formatDate(request.createdAt) }}
                  </p>

                  <div v-if="request.adminNote" class="mt-3 rounded-md bg-yellow-50 p-3">
                    <p class="text-xs font-semibold text-yellow-700">Ghi chú từ cửa hàng</p>

                    <p class="mt-1 text-sm text-yellow-800">
                      {{ request.adminNote }}
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

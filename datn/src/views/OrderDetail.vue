<script setup>
import { RouterLink, useRoute } from 'vue-router'
import { CheckCircle2 } from 'lucide-vue-next'
import ulti from '@/ulti/ulti'
import { useOrderStore } from '@/stores/useOrderStore'
import { storeToRefs } from 'pinia'
import { computed, onMounted, watch } from 'vue'

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
  console.log(order.value)
})

// Handle status

const STATUS_STEPS = [
  { key: 'PENDING', label: 'Chờ xác nhận', icon: 'pending' },
  { key: 'CONFIRMED', label: 'Đã xác nhận', icon: 'check_circle' },
  { key: 'SHIPPING', label: 'Đang giao hàng', icon: 'local_shipping' },
  { key: 'DELIVERED', label: 'Đã giao hàng', icon: 'done_all' },
  { key: 'CANCELLED', label: 'Đã hủy', icon: 'cancel' },
]

const currentStepIndex = computed(() =>
  STATUS_STEPS.findIndex((s) => s.key === order.value?.status),
)
</script>

<template>
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
                <p class="text-xs uppercase tracking-widest text-muted-foreground">Trạng thái</p>

                <span
                  class="mt-1 inline-block rounded bg-yellow-100 px-3 py-1 text-xs font-medium text-yellow-700"
                >
                  Chờ xác nhận
                </span>
              </div>

              <div>
                <p class="text-xs uppercase tracking-widest text-muted-foreground">Thanh toán</p>

                <p class="mt-1">
                  {{ order?.paymentMethod }}
                </p>
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
                <img :src="item.image" class="h-24 w-24 object-cover" />

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

          <!-- TIMELINE -->
          <div class="mt-8 border-t border-border pt-6">
            <h3 class="mb-4 text-sm font-semibold uppercase tracking-widest">
              Trạng thái đơn hàng
            </h3>

            <div class="space-y-5">
              <div class="flex gap-3" v-for="(status, index) in STATUS_STEPS" :key="status.key">
                <!-- dot -->
                <div class="flex flex-col items-center">
                  <div
                    class="mt-1 h-3 w-3 rounded-full"
                    :class="index <= currentStepIndex ? 'bg-green-500' : 'bg-gray-300'"
                  ></div>
                  <!-- line nối giữa các bước -->
                  <div
                    v-if="index < STATUS_STEPS.length - 1"
                    class="w-0.5 flex-1 mt-1"
                    :class="index < currentStepIndex ? 'bg-green-500' : 'bg-gray-300'"
                  ></div>
                </div>

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
                  <p v-if="index === currentStepIndex" class="text-xs text-muted-foreground">
                    {{ ulti.formatDate(order?.createdAt) }}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

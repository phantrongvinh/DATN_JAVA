<script setup>
import ulti from '@/ulti/ulti'

const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },

  order: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['update:show'])

const closeModal = () => {
  emit('update:show', false)
}
</script>

<template>
  <Transition name="fade">
    <div
      v-if="show"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4"
      @click.self="closeModal"
    >
      <div class="w-full max-w-2xl max-h-[90vh] overflow-y-auto rounded-lg bg-white shadow-xl">
        <!-- Header -->
        <div class="flex items-center justify-between border-b p-6">
          <div class="flex items-center gap-3">
            <h2 class="text-xl font-bold text-gray-900">Đơn hàng #{{ order.id }}</h2>
            <p class="mt-1 text-sm text-gray-500">
              {{ ulti.formatDate(order.createdAt) }}
            </p>
          </div>
          <span
            :class="{
              'bg-amber-100 text-amber-800': order.status === 'PENDING',
              'bg-blue-100 text-blue-800': order.status === 'CONFIRMED',
              'bg-purple-100 text-purple-800': order.status === 'SHIPPING',
              'bg-green-100 text-green-800': order.status === 'DELIVERED',
              'bg-red-100 text-red-800': order.status === 'CANCELLED',
            }"
            class="rounded-full px-2.5 py-0.5 text-xs font-medium"
          >
            {{ order.status }}
          </span>
          <button
            @click="closeModal"
            class="ml-4 text-2xl leading-none text-gray-400 hover:text-gray-700"
            aria-label="Đóng"
          >
            &times;
          </button>
        </div>

        <!-- Body -->
        <div class="space-y-6 p-6">
          <!-- Người nhận -->
          <section>
            <h3 class="mb-3 text-sm font-semibold uppercase tracking-wider text-gray-500">
              Người nhận
            </h3>
            <div class="grid grid-cols-1 gap-3 sm:grid-cols-3">
              <div>
                <p class="text-xs text-gray-500">Họ tên</p>
                <p class="font-medium text-gray-900">{{ order.receiverName }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-500">Số điện thoại</p>
                <p class="font-medium text-gray-900">{{ order.receiverPhone }}</p>
              </div>
              <div class="sm:col-span-3">
                <p class="text-xs text-gray-500">Địa chỉ</p>
                <p class="font-medium text-gray-900">{{ order.shippingAddress }}</p>
              </div>
            </div>
          </section>

          <!-- Thanh toán -->
          <section>
            <h3 class="mb-3 text-sm font-semibold uppercase tracking-wider text-gray-500">
              Thanh toán
            </h3>
            <div class="grid grid-cols-1 gap-3 sm:grid-cols-2">
              <div>
                <p class="text-xs text-gray-500">Phương thức</p>
                <p class="font-medium text-gray-900">{{ order.paymentMethod }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-500">Trạng thái</p>
                <span
                  :class="{
                    'bg-amber-100 text-amber-800': order.paymentStatus === 'PENDING',
                    'bg-green-100 text-green-800': order.paymentStatus === 'PAID',
                    'bg-red-100 text-red-800': order.paymentStatus === 'FAILED',
                    'bg-yellow-100 text-yellow-800': order.paymentStatus === 'REFUNDED',
                    'bg-orange-100 text-orange-800': order.paymentStatus === 'UNPAID',
                  }"
                  class="rounded-full px-2.5 py-0.5 text-xs font-medium"
                >
                  {{ order.paymentStatus }}
                </span>
              </div>
              <div>
                <p class="text-xs text-gray-500">Mã giao dịch</p>
                <p class="font-medium text-gray-900">{{ order.transactionId || '---' }}</p>
              </div>
              <div>
                <p class="text-xs text-gray-500">Mã vận đơn</p>
                <p class="font-medium text-gray-900">{{ order.trackingCode }}</p>
              </div>
            </div>
          </section>

          <!-- Sản phẩm -->
          <section>
            <h3 class="mb-3 text-sm font-semibold uppercase tracking-wider text-gray-500">
              Sản phẩm
            </h3>
            <ul class="divide-y divide-gray-100">
              <li
                v-for="item in order.items"
                :key="item.id"
                class="flex items-center justify-between py-3"
              >
                <div class="flex-1">
                  <p class="font-medium text-gray-900">{{ item.productName }}</p>
                  <p class="mt-0.5 text-sm text-gray-500">
                    {{ item.color }} &bull; {{ item.sizeName }}
                  </p>
                  <p v-if="item.promotionName" class="mt-0.5 text-xs text-rose-500">
                    {{ item.promotionName }}
                  </p>
                </div>
                <div class="text-right">
                  <p class="font-medium text-gray-900">x{{ item.quantity }}</p>
                  <p class="mt-0.5 flex items-center gap-2 text-sm">
                    <span
                      v-if="item.originalPrice && item.originalPrice > item.price"
                      class="text-gray-400 line-through"
                    >
                      {{ ulti.formatVND(item.originalPrice) }}
                    </span>
                    <span
                      :class="
                        item.originalPrice && item.originalPrice > item.price
                          ? 'font-medium text-red-600'
                          : 'text-gray-600'
                      "
                    >
                      {{ ulti.formatVND(item.price) }}
                    </span>
                  </p>
                </div>
              </li>
            </ul>
          </section>

          <!-- Tổng cộng -->
          <section class="rounded-lg bg-gray-50 p-4">
            <div class="space-y-2 text-sm">
              <div class="flex justify-between text-gray-600">
                <span>Tạm tính</span>
                <span>{{ ulti.formatVND(order.totalPrice) }}</span>
              </div>
              <div class="flex justify-between text-gray-600">
                <span>Voucher</span>
                <span class="text-rose-500">-{{ ulti.formatVND(order.discountAmount) }}</span>
              </div>
              <div class="flex justify-between text-gray-600">
                <span>Khuyến mãi</span>
                <span class="text-rose-500">-{{ ulti.formatVND(order.timeDiscount) }}</span>
              </div>
              <div
                class="flex justify-between border-t border-gray-200 pt-2 text-base font-bold text-gray-900"
              >
                <span>Tổng cộng</span>
                <span>{{ ulti.formatVND(order.finalPrice) }}</span>
              </div>
            </div>
          </section>
        </div>

        <!-- Footer -->
        <div class="flex items-center justify-end gap-3 border-t p-6">
          <button
            @click="closeModal"
            class="rounded-md border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50"
          >
            Đóng
          </button>
          <button
            @click="printInvoice"
            class="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700"
          >
            In hóa đơn
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

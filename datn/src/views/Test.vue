<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { CheckCircle2 } from 'lucide-vue-next'

import { useCheckout } from '@/composables/useCheckout'
import { useCartStore } from '@/stores/useCartStore'
import ulti from '@/ulti/ulti'
import { useAddressStore } from '@/stores/useAddressStore'
import { storeToRefs } from 'pinia'

const addressStore = useAddressStore()

const { addresses } = storeToRefs(addressStore)
const cartStore = useCartStore()

const items = computed(() => cartStore.items)
const totalItems = computed(() => items.value.reduce((s, i) => s + i.quantity, 0))

const selectedAddressId = ref(null)
const selectedAddress = ref(null)
const paymentMethodId = ref(1)
const showNewForm = ref(false)

const newAddress = ref({
  receiverName: '',
  receiverPhone: '',
  address: '',
  isPrimary: false,
})

onMounted(async () => {
  await addressStore.fetchAddresses()

  const primary = addresses.value?.find((a) => a.primary)
  if (primary) selectAddress(primary)
})

const selectAddress = (addr) => {
  selectedAddressId.value = addr.id
  selectedAddress.value = addr
}

const handleAddAddress = async () => {
  await addressStore.addAddress(newAddress.value)
  showNewForm.value = false
  newAddress.value = { receiverName: '', receiverPhone: '', address: '', isPrimary: false }

  // Auto chọn địa chỉ vừa thêm nếu là primary
  const primary = addresses.value?.find((a) => a.primary)
  if (primary) selectAddress(primary)
}

const handleCheckout = async () => {
  if (!selectedAddress.value) return
  const form = {
    shippingAddress: selectedAddress.value.address,
    receiverName: selectedAddress.value.receiverName,
    receiverPhone: selectedAddress.value.receiverPhone,
    paymentMethodId: paymentMethodId.value,
  }
  await checkout(form)
}

// handle voucher
const {
  loading,
  subtotal,
  voucherDiscount,
  timeDiscount,
  finalPrice,
  timeLeft,
  timePromotion,
  voucher,
  voucherCode,
  voucherError,
  error,
  handleApplyVoucher,
  removeVoucher,
  checkout,
} = useCheckout()
</script>

<template>
  <!-- SUCCESS -->
  <!-- <div v-if="orderId" class="container mx-auto py-24">
    <div class="flex flex-col items-center text-center">
      <CheckCircle2 class="h-16 w-16 text-green-500" :stroke-width="1" />

      <h1 class="mt-6 text-4xl font-bold">Cảm ơn bạn!</h1>

      <p class="mt-3 max-w-md text-gray-500">
        Đơn hàng

        <span class="font-semibold text-black"> #{{ orderId }} </span>

        đã được tiếp nhận.
      </p>

      <div class="mt-8 flex gap-3">
        <RouterLink
          to="/account/orders"
          class="bg-black px-6 py-3 text-xs uppercase tracking-widest text-white"
        >
          Xem đơn hàng
        </RouterLink>

        <RouterLink
          to="/products"
          class="border border-black px-6 py-3 text-xs uppercase tracking-widest"
        >
          Mua tiếp
        </RouterLink>
      </div>
    </div>
  </div> -->

  <!-- CHECKOUT -->
  <div class="container-x py-16">
    <div class="grid gap-10 lg:grid-cols-[1fr_400px]">
      <!-- LEFT -->
      <div class="space-y-8">
        <!-- ADDRESS -->
        <section>
          <h2 class="font-display text-xl mb-4">Địa chỉ giao hàng</h2>

          <!-- List Address -->
          <div class="space-y-3">
            <div
              v-for="addr in addresses"
              :key="addr.id"
              @click="selectAddress(addr)"
              class="cursor-pointer border p-4 transition-all"
              :class="
                selectedAddressId === addr.id
                  ? 'border-foreground bg-secondary/50'
                  : 'border-border hover:border-foreground'
              "
            >
              <div class="flex items-start justify-between">
                <div>
                  <div class="flex items-center gap-2">
                    <span class="font-medium">
                      {{ addr.receiverName }}
                    </span>

                    <span
                      v-if="addr.primary"
                      class="bg-foreground px-2 py-0.5 text-[10px] uppercase tracking-widest text-background"
                    >
                      Mặc định
                    </span>
                  </div>

                  <p class="mt-1 text-sm text-muted-foreground">
                    {{ addr.receiverPhone }}
                  </p>

                  <p class="mt-1 text-sm">
                    {{ addr.address }}
                  </p>
                </div>
              </div>
            </div>
          </div>

          <!-- Add new address -->
          <button class="mt-4 text-sm underline" @click="showNewForm = !showNewForm">
            + Thêm địa chỉ mới
          </button>

          <div v-if="showNewForm" class="mt-4 border border-border p-4">
            <div class="grid gap-4 md:grid-cols-2">
              <input
                v-model="newAddress.receiverName"
                placeholder="Họ tên"
                class="border border-border px-3 py-2"
              />

              <input
                v-model="newAddress.receiverPhone"
                placeholder="Số điện thoại"
                class="border border-border px-3 py-2"
              />
            </div>

            <input
              v-model="newAddress.address"
              placeholder="Địa chỉ"
              class="mt-4 w-full border border-border px-3 py-2"
            />

            <label class="mt-4 flex items-center gap-2 text-sm">
              <input type="checkbox" v-model="newAddress.isPrimary" />
              Đặt làm mặc định
            </label>

            <button
              class="mt-4 bg-ink px-6 py-2 text-xs uppercase tracking-widest text-ivory"
              @click="handleAddAddress"
            >
              Lưu địa chỉ
            </button>
          </div>
        </section>

        <!-- PAYMENT -->
        <section>
          <h2 class="font-display text-xl mb-4">Phương thức thanh toán</h2>

          <div class="space-y-3">
            <label
              class="flex cursor-pointer items-start gap-3 border p-4"
              :class="paymentMethodId === 1 ? 'border-foreground bg-secondary/50' : 'border-border'"
            >
              <input type="radio" v-model="paymentMethodId" :value="1" />

              <div>
                <p class="font-medium">Thanh toán khi nhận hàng (COD)</p>
                <p class="text-xs text-muted-foreground">Thanh toán trực tiếp cho shipper.</p>
              </div>
            </label>

            <label
              class="flex cursor-pointer items-start gap-3 border p-4"
              :class="paymentMethodId === 2 ? 'border-foreground bg-secondary/50' : 'border-border'"
            >
              <input type="radio" v-model="paymentMethodId" :value="2" />

              <div>
                <p class="font-medium">VNPay</p>
                <p class="text-xs text-muted-foreground">Thanh toán online qua VNPay.</p>
              </div>
            </label>
          </div>
        </section>
      </div>

      <!-- RIGHT -->
      <aside class="h-fit border border-border p-6 sticky top-24">
        <!-- RIGHT -->
        <div class="lg:col-span-4">
          <aside class="sticky top-24 border border-gray-200 p-6">
            <h2 class="font-serif text-xl">Đơn hàng ({{ totalItems }} sản phẩm)</h2>
            <!-- Voucher -->
            <div v-if="!voucher" class="mt-5">
              <label class="mb-2 block text-[11px] uppercase tracking-widest text-gray-500">
                Mã giảm giá
              </label>

              <div class="flex gap-2">
                <input
                  v-model="voucherCode"
                  @keyup.enter="handleApplyVoucher"
                  placeholder="Nhập voucher"
                  class="flex-1 border border-gray-300 px-3 py-2 text-sm outline-none focus:border-black"
                />

                <button @click="handleApplyVoucher" class="bg-black px-4 text-sm text-white">
                  Áp dụng
                </button>
              </div>

              <p v-if="voucherError" class="mt-2 text-sm text-red-500">
                {{ voucherError }}
              </p>
            </div>

            <!-- Summary -->
            <div class="mt-6 border-t pt-4 space-y-3 text-sm">
              <div class="flex justify-between">
                <span class="text-gray-500"> Tạm tính </span>

                <span>
                  {{ ulti.formatVND(subtotal) }}
                </span>
              </div>

              <div v-if="voucherDiscount > 0" class="flex justify-between text-green-600">
                <div class="flex items-center gap-2">
                  <span>Voucher</span>

                  <span class="rounded bg-green-100 px-2 py-0.5 text-xs">
                    {{ voucher.code }}
                  </span>

                  <button
                    @click="removeVoucher"
                    class="flex h-5 w-5 items-center justify-center rounded-full text-gray-400 transition hover:bg-red-50 hover:text-red-500"
                  >
                    ✕
                  </button>
                </div>

                <span> -{{ ulti.formatVND(voucherDiscount) }} </span>
              </div>

              <div v-if="timeDiscount > 0" class="flex justify-between text-green-600">
                <div>
                  Khung giờ vàng
                  <span class="ml-2 rounded bg-green-100 px-2 py-1 text-xs text-green-600">
                    {{ timeLeft }}
                  </span>

                  <span class="ml-2 rounded bg-red-100 px-2 py-1 text-xs text-red-600">
                    {{
                      timePromotion.discountType === 'percent'
                        ? `-${timePromotion.discountValue}%`
                        : `-${ulti.formatVND(timePromotion.discountValue)}`
                    }}
                  </span>
                </div>
                <span class="font-medium text-green-600">
                  -{{ ulti.formatVND(timeDiscount) }}
                </span>
              </div>

              <div
                v-if="selectedAddress"
                class="mt-4 rounded border border-green-200 bg-green-50 p-3"
              >
                <p class="font-medium text-green-700">Địa chỉ được chọn</p>

                <p class="mt-1 text-sm">
                  {{ selectedAddress.receiverName }}
                  - {{ selectedAddress.receiverPhone }}
                </p>

                <p class="text-sm text-gray-600">
                  {{ selectedAddress.address }}
                </p>
              </div>

              <div class="flex justify-between border-t pt-4 text-lg font-semibold">
                <span>Tổng cộng</span>

                <span class="text-red-500">
                  {{ ulti.formatVND(finalPrice) }}
                </span>
              </div>
            </div>

            <p v-if="error" class="mt-3 text-sm text-red-500">
              {{ error }}
            </p>

            <button
              class="mt-6 w-full bg-black py-3 text-xs uppercase tracking-widest text-white transition hover:opacity-90 disabled:opacity-50"
              :disabled="!selectedAddressId || loading"
              @click="handleCheckout"
            >
              {{ loading ? 'Đang xử lý...' : 'Đặt hàng' }}
            </button>
          </aside>
        </div>
      </aside>
    </div>
  </div>
</template>

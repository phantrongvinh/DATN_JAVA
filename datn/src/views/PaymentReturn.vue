<!-- views/payment/PaymentReturn.vue -->
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/stores/useCartStore'
import { useAuthStore } from '@/stores/useAuthStore'
import axiosClient from '@/api/axiosClient'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()
const authStore = useAuthStore()

const status = ref(null) // 'success' | 'failed' | 'processing'
const orderId = ref(null)

onMounted(async () => {
  status.value = 'processing'

  try {
    // Gửi toàn bộ query params VNPay trả về lên BE verify
    const res = await axiosClient.get('/payment/vnpay/callback', {
      params: route.query,
    })

    if (res.data.message === 'Thanh toán thành công') {
      status.value = 'success'

      // Lấy orderId từ vnp_TxnRef (dạng "orderId_timestamp")
      const txnRef = route.query.vnp_TxnRef
      orderId.value = txnRef?.split('_')[0]

      // Clear cart
      if (authStore.isAuthenticated) {
        await cartStore.fetchCart()
      } else {
        cartStore.clearLocal()
      }

      // Redirect sang trang order detail sau 2s
      setTimeout(() => {
        router.push(orderId.value ? `/orders/${orderId.value}` : '/orders')
      }, 2000)
    } else {
      status.value = 'failed'
    }
  } catch (err) {
    console.error(err)
    status.value = 'failed'
  }
})
</script>

<template>
  <div class="container-x py-24">
    <div class="mx-auto max-w-2xl">
      <div class="border border-border p-10 text-center">
        <!-- Processing -->
        <div v-if="status === 'processing'">
          <div
            class="mx-auto h-16 w-16 animate-spin rounded-full border-4 border-border border-t-foreground"
          />

          <h1 class="mt-6 font-display text-4xl">Đang xử lý thanh toán</h1>

          <p class="mt-3 text-muted-foreground">
            Vui lòng không đóng trình duyệt trong khi hệ thống xác nhận giao dịch.
          </p>
        </div>

        <!-- Success -->
        <div v-else-if="status === 'success'">
          <div class="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-green-100">
            <i class="material-symbols-rounded text-green-600" style="font-size: 48px">
              check_circle
            </i>
          </div>

          <h1 class="mt-6 font-display text-5xl text-green-600">Thanh toán thành công</h1>

          <p class="mt-4 text-muted-foreground">
            Đơn hàng
            <span class="font-semibold text-foreground"> #{{ orderId }} </span>
            đã được xác nhận.
          </p>

          <p class="mt-2 text-sm text-muted-foreground">
            Chúng tôi đang chuyển bạn tới trang chi tiết đơn hàng...
          </p>

          <div class="mt-8 h-1 overflow-hidden bg-secondary">
            <div class="h-full w-full animate-pulse bg-green-500"></div>
          </div>
        </div>

        <!-- Failed -->
        <div v-else-if="status === 'failed'">
          <div class="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-red-100">
            <i class="material-symbols-rounded text-red-500" style="font-size: 48px"> cancel </i>
          </div>

          <h1 class="mt-6 font-display text-5xl text-red-500">Thanh toán thất bại</h1>

          <p class="mt-4 text-muted-foreground">Giao dịch không thành công hoặc đã bị hủy.</p>

          <p class="mt-2 text-sm text-muted-foreground">
            Bạn có thể thử lại hoặc kiểm tra đơn hàng của mình.
          </p>

          <div class="mt-10 flex flex-col gap-3 sm:flex-row sm:justify-center">
            <button
              @click="router.back()"
              class="bg-ink px-8 py-3 text-xs uppercase tracking-widest text-ivory hover:bg-ink/85"
            >
              Thử lại
            </button>

            <button
              @click="router.push('/account/orders')"
              class="border border-border px-8 py-3 text-xs uppercase tracking-widest hover:bg-secondary"
            >
              Xem đơn hàng
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

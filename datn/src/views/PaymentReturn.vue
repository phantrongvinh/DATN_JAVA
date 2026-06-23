<!-- PaymentReturn.vue — trang VNPay/MoMo redirect về -->
<script setup>
import { useCartStore } from '@/stores/useCartStore'
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const status = ref(null)

const cartStore = useCartStore();

onMounted(async () => {
  // VNPay return
  if (route.query.vnp_ResponseCode) {
    const res = await axiosClient.get('/api/v1/payment/vnpay/callback', {
      params: route.query
    })
    status.value = res.data.message === 'Thanh toán thành công' ? 'success' : 'failed'
    if (status.value === 'success') {
      await cartStore.fetchCart()
      setTimeout(() => router.push('/orders'), 2000)
    }
  }
})
</script>

<template>
  <div class="text-center mt-5">
    <div v-if="status === 'success'">
      <i class="material-symbols-rounded text-success" style="font-size: 64px">check_circle</i>
      <h4 class="mt-3">Thanh toán thành công!</h4>
      <p class="text-secondary">Đang chuyển về trang đơn hàng...</p>
    </div>
    <div v-else-if="status === 'failed'">
      <i class="material-symbols-rounded text-danger" style="font-size: 64px">cancel</i>
      <h4 class="mt-3">Thanh toán thất bại</h4>
      <button class="btn bg-gradient-danger mt-3" @click="router.back()">Thử lại</button>
    </div>
    <div v-else>
      <div class="spinner-border text-danger"></div>
      <p class="mt-3">Đang xử lý...</p>
    </div>
  </div>
</template>
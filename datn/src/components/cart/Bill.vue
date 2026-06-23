<template>
  <div class="card">
    <div class="card-body">
      <!-- Flash sale banner -->
      <div
        v-if="timePromotion && timeLeft"
        class="alert alert-danger py-2 d-flex justify-content-between align-items-center mb-3"
      >
        <span>🔥 {{ timePromotion.name }}</span>
        <span class="fw-bold">{{ timeLeft }}</span>
      </div>

      <!-- Tóm tắt giá -->
      <div class="d-flex justify-content-between">
        <span>Tạm tính</span>
        <span>{{ ulti.formatVND(subtotal) }}</span>
      </div>

      <div v-if="voucherDiscount > 0" class="d-flex justify-content-between mt-2">
        <span>
          Voucher
          <span class="badge bg-success ms-1">{{ voucher.code }}</span>
          <i
            class="material-symbols-rounded text-sm text-danger cursor-pointer ms-1"
            @click="removeVoucher"
            >close</i
          >
        </span>
        <span class="text-success">-{{ ulti.formatVND(voucherDiscount) }}</span>
      </div>

      <div v-if="timeDiscount > 0" class="d-flex justify-content-between mt-2">
        <span>
          Khung giờ vàng
          <span class="badge bg-danger ms-1">
            {{
              timePromotion.discountType === 'percent'
                ? `-${timePromotion.discountValue}%`
                : `-${ulti.formatVND(timePromotion.discountValue)}`
            }}
          </span>
        </span>
        <span class="text-success">-{{ ulti.formatVND(timeDiscount) }}</span>
      </div>

      <hr />

      <div class="d-flex justify-content-between">
        <h5>Thành tiền</h5>
        <h5 class="text-danger">{{ ulti.formatVND(finalPrice) }}</h5>
      </div>

      <!-- Voucher input -->
      <div v-if="!voucher" class="mt-3">
        <div class="input-group input-group-outline">
          <label class="form-label">Mã giảm giá</label>
          <input class="form-control" v-model="voucherCode" @keyup.enter="handleApplyVoucher" />
        </div>
        <div v-if="voucherError" class="text-danger text-sm mt-1">{{ voucherError }}</div>
        <button class="btn btn-outline-success w-100 mt-2" @click="handleApplyVoucher">
          Áp dụng
        </button>
      </div>

      <div v-if="error" class="text-danger text-sm mt-2">{{ error }}</div>

      <!-- <button class="btn bg-gradient-danger w-100 mt-4" :disabled="loading" @click="checkout(form)">
        {{ loading ? 'Đang xử lý...' : 'Thanh toán' }}
      </button> -->
      <button class="btn bg-gradient-danger w-100 mt-4" @click="router.push('/checkout')">
        Tiến hành thanh toán
      </button>
    </div>
  </div>
</template>

<script setup>
import { useCheckout } from '@/composables/useCheckout'
import ulti from '@/ulti/ulti'
import { useRouter } from 'vue-router'

const props = defineProps({
  data: Array,
})

const router = useRouter()

// handle thanh toan
const {
  subtotal,
  voucherDiscount,
  timeDiscount,
  finalPrice,
  timePromotion,
  timeLeft,
  voucher,
  voucherCode,
  voucherError,
  loading,
  error,
  handleApplyVoucher,
  removeVoucher,
  checkout,
} = useCheckout()

// const form = reactive({
//   shippingAddress: '',
//   receiverName: '',
//   receiverPhone: '',
//   paymentMethodId: 1,
// })
</script>

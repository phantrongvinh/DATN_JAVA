import orderAPI from '@/api/orderAPI'
import timePromotionAPI from '@/api/timePromotion'
import voucherAPI from '@/api/voucherAPI'
import { useAuthStore } from '@/stores/useAuthStore'
import { useCartStore } from '@/stores/useCartStore'
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'

// composables/useCheckout.js
export function useCheckout() {
  const cartStore = useCartStore()
  const authStore = useAuthStore()
  const router = useRouter()

  const timePromotion = ref(null)
  const voucher = ref(null)
  const voucherCode = ref('')
  const voucherError = ref('')
  const loading = ref(false)
  const error = ref(null)
  const timeLeft = ref(null)

  // subtotal — price trong cart đã là discountedPrice sau product promotion
  const subtotal = computed(() => cartStore.items.reduce((sum, i) => sum + i.price * i.quantity, 0))

  // voucher discount (preview)
  const voucherDiscount = computed(() => {
    if (!voucher.value) return 0
    if (voucher.value.discountType === 'percent') {
      const d = Math.round((subtotal.value * voucher.value.discountValue) / 100)
      return voucher.value.maxDiscount ? Math.min(d, voucher.value.maxDiscount) : d
    }
    return Math.min(voucher.value.discountValue, subtotal.value)
  })

  // time discount (preview) — apply lên subtotal sau voucher
  const afterVoucher = computed(() => subtotal.value - voucherDiscount.value)
  const timeDiscount = computed(() => {
    if (!timePromotion.value) return 0
    if (timePromotion.value.discountType === 'percent') {
      return Math.round((afterVoucher.value * timePromotion.value.discountValue) / 100)
    }
    return Math.min(timePromotion.value.discountValue, afterVoucher.value)
  })

  const finalPrice = computed(() =>
    Math.max(subtotal.value - voucherDiscount.value - timeDiscount.value, 0),
  )

  // load time promotion
  const loadTimePromotion = async () => {
    try {
      timePromotion.value = await timePromotionAPI.fetchActiveTimePromotion()
    } catch {
      timePromotion.value = null
    }
  }

  // countdown timer
  let timer
  const updateCountdown = () => {
    if (!timePromotion.value?.endTime) return
    const now = new Date()
    const end = new Date()
    const [h, m, s] = timePromotion.value.endTime.split(':')
    end.setHours(+h, +m, +s, 0)
    const diff = end - now
    if (diff <= 0) {
      timeLeft.value = null
      timePromotion.value = null
      clearInterval(timer)
      return
    }
    const hh = Math.floor(diff / 3600000)
    const mm = Math.floor((diff % 3600000) / 60000)
    const ss = Math.floor((diff % 60000) / 1000)
    timeLeft.value = `${String(hh).padStart(2, '0')}:${String(mm).padStart(2, '0')}:${String(ss).padStart(2, '0')}`
  }

  // apply voucher
  const handleApplyVoucher = async () => {
    voucherError.value = ''
    voucher.value = null
    if (!voucherCode.value.trim()) return
    try {
      voucher.value = await voucherAPI.applyVoucher(voucherCode.value, subtotal.value)
    } catch (err) {
      voucherError.value = err.response?.data?.message ?? 'Mã giảm giá không hợp lệ'
    }
  }

  const removeVoucher = () => {
    voucher.value = null
    voucherCode.value = ''
    voucherError.value = ''
  }

  // checkout
  const checkout = async (form) => {
    loading.value = true
    error.value = null
    try {
      const payload = {
        items: cartStore.items.map((i) => ({
          variantId: i.productVariantId,
          quantity: i.quantity,
        })),
        voucherCode: voucher.value?.code ?? null,
        shippingAddress: form.shippingAddress,
        receiverName: form.receiverName,
        receiverPhone: form.receiverPhone,
        paymentMethodId: form.paymentMethodId,
      }

      const orderId = await orderAPI.placeOrder(payload)

      console.log(orderId)

      if (authStore.isAuthenticated) {
        await cartStore.fetchCart()
      } else {
        cartStore.clearLocal()
      }

      // COD
      if (form.paymentMethodId === 1) {
        await handlePostOrder()
        router.push(`/orders/${orderId}`)
        return
      }

      // VNPAY
      if (form.paymentMethodId === 2) {
        const res = await axiosClient.get(`/api/v1/payment/vnpay/${orderId.data}`)
        window.location.href = res.data.paymentUrl
        return
      }

      router.push(`/orders/${orderId}`)
    } catch (err) {
      error.value = err.response?.data?.message ?? 'Đặt hàng thất bại'
      throw err
    } finally {
      loading.value = false
    }
  }

  const handlePostOrder = async () => {
    if (authStore.isAuthenticated) {
      await cartStore.fetchCart()
    } else {
      cartStore.clearLocal()
    }
  }

  onMounted(async () => {
    await loadTimePromotion()
    timer = setInterval(updateCountdown, 1000)
  })
  onUnmounted(() => clearInterval(timer))

  return {
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
  }
}

<template>
  <div class="container my-5">
    <div class="row mx-3">
      <!-- LEFT: Địa chỉ + Payment -->
      <div class="col-md-7">
        <!-- Chọn địa chỉ -->
        <div class="card mb-4">
          <div class="card-header pb-0">
            <h6>Địa chỉ giao hàng</h6>
          </div>
          <div class="card-body">
            <!-- Danh sách địa chỉ đã lưu -->
            <div
              v-for="addr in addresses"
              :key="addr.id"
              class="border rounded p-3 mb-2 cursor-pointer"
              :class="{ 'border-danger': selectedAddressId === addr.id }"
              @click="selectAddress(addr)"
            >
              <div class="d-flex justify-content-between">
                <div>
                  <strong>{{ addr.receiverName }}</strong> · {{ addr.receiverPhone }}
                  <span v-if="addr.primary" class="badge bg-danger ms-2">Mặc định</span>
                  <div class="text-sm text-secondary mt-1">{{ addr.address }}</div>
                </div>
                <button class="btn btn-link text-danger p-0" @click.stop="deleteAddress(addr.id)">
                  <i class="material-symbols-rounded text-sm">delete</i>
                </button>
              </div>
            </div>

            <!-- Form thêm địa chỉ mới -->
            <div class="mt-3">
              <a class="text-danger cursor-pointer text-sm" @click="showNewForm = !showNewForm">
                + Thêm địa chỉ mới
              </a>
              <div v-if="showNewForm" class="mt-3">
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <div class="input-group input-group-outline">
                      <label class="form-label">Họ tên</label>
                      <input class="form-control" v-model="newAddress.receiverName" />
                    </div>
                  </div>
                  <div class="col-md-6 mb-3">
                    <div class="input-group input-group-outline">
                      <label class="form-label">Số điện thoại</label>
                      <input class="form-control" v-model="newAddress.receiverPhone" />
                    </div>
                  </div>
                </div>
                <div class="input-group input-group-outline mb-3">
                  <label class="form-label">Địa chỉ</label>
                  <input class="form-control" v-model="newAddress.address" />
                </div>
                <div class="form-check ps-0 mb-3">
                  <input
                    class="form-check-input"
                    type="checkbox"
                    v-model="newAddress.isPrimary"
                    id="isPrimary"
                  />
                  <label class="form-check-label" for="isPrimary">Đặt làm địa chỉ mặc định</label>
                </div>
                <button class="btn bg-gradient-danger btn-sm" @click="handleAddAddress">
                  Lưu địa chỉ
                </button>
              </div>
            </div>
          </div>
        </div>

        <!-- Phương thức thanh toán -->
        <div class="card">
          <div class="card-header pb-0">
            <h6>Phương thức thanh toán</h6>
          </div>
          <div class="card-body">
            <div class="form-check mb-2">
              <input
                class="form-check-input"
                type="radio"
                v-model="paymentMethodId"
                :value="1"
                id="cod"
              />
              <label class="form-check-label" for="cod">💵 Thanh toán khi nhận hàng (COD)</label>
            </div>
            <div class="form-check">
              <input
                class="form-check-input"
                type="radio"
                v-model="paymentMethodId"
                :value="2"
                id="vnpay"
              />
              <label class="form-check-label" for="vnpay">🏦 VNPay</label>
            </div>
          </div>
        </div>
      </div>

      <!-- RIGHT: Order summary -->
      <div class="col-md-5">
        <div class="card position-sticky top-10">
          <div class="card-header pb-0">
            <h6>Đơn hàng ({{ totalItems }} sản phẩm)</h6>
          </div>
          <div class="card-body">
            <div
              v-for="i in items"
              :key="i.productVariantId"
              class="d-flex justify-content-between mb-2"
            >
              <span class="text-sm">{{ i.name }} x{{ i.quantity }}</span>
              <span class="text-sm">{{ ulti.formatVND(i.price * i.quantity) }}</span>
            </div>
            <hr />
            <div class="d-flex justify-content-between">
              <span>Tạm tính</span>
              <span>{{ ulti.formatVND(subtotal) }}</span>
            </div>
            <div v-if="voucherDiscount > 0" class="d-flex justify-content-between mt-1">
              <span class="text-success">Voucher</span>
              <span class="text-success">-{{ ulti.formatVND(voucherDiscount) }}</span>
            </div>
            <hr />
            <div class="d-flex justify-content-between">
              <h6>Thành tiền</h6>
              <h6 class="text-danger">{{ ulti.formatVND(finalPrice) }}</h6>
            </div>
            <div v-if="error" class="text-danger text-sm mt-2">{{ error }}</div>
            <button
              class="btn bg-gradient-danger w-100 mt-3"
              :disabled="!selectedAddressId || loading"
              @click="handleCheckout"
            >
              {{ loading ? 'Đang xử lý...' : 'Đặt hàng' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useCheckout } from '@/composables/useCheckout'
import { useCartStore } from '@/stores/useCartStore'
import ulti from '@/ulti/ulti'
import { useAddressStore } from '@/stores/useAddressStore'
import { storeToRefs } from 'pinia'

const addressStore = useAddressStore()

const { addresses } = storeToRefs(addressStore)
const { subtotal, voucherDiscount, finalPrice, loading, error, checkout } = useCheckout()
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
</script>

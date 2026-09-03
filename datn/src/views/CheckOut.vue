<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useCheckout } from '@/composables/useCheckout'
import { useCartStore } from '@/stores/useCartStore'
import ulti from '@/ulti/ulti'
import { useAddressStore } from '@/stores/useAddressStore'
import { storeToRefs } from 'pinia'
import axiosClient from '@/api/axiosClient'
import * as yup from 'yup'
import { useForm } from 'vee-validate'

const addressStore = useAddressStore()

const { addresses } = storeToRefs(addressStore)
const cartStore = useCartStore()

const totalItems = computed(() => cartStore.items.reduce((s, i) => s + i.quantity, 0))

const selectedAddressId = ref(null)
const selectedAddress = ref(null)
const paymentMethodId = ref(1)
const showNewForm = ref(false)

// address
const schema = yup.object({
  receiverName: yup
    .string()
    .trim()
    .required('Vui lòng nhập họ tên người nhận')
    .min(2, 'Họ tên phải có ít nhất 2 ký tự')
    .max(50, 'Họ tên không quá 50 ký tự')
    .matches(/^[A-Za-zÀ-ỹ\s]+$/, 'Họ tên chỉ được chứa chữ cái'),

  receiverPhone: yup
    .string()
    .required('Vui lòng nhập số điện thoại')
    .matches(/^(0|\+84)(3|5|7|8|9)\d{8}$/, 'Số điện thoại không hợp lệ'),

  detail: yup
    .string()
    .trim()
    .required('Vui lòng nhập địa chỉ chi tiết')
    .min(5, 'Địa chỉ quá ngắn')
    .max(200, 'Địa chỉ không quá 200 ký tự'),

  provinceName: yup.string().required('Vui lòng chọn tỉnh/thành phố'),

  districtName: yup.string().required('Vui lòng chọn quận/huyện'),

  wardName: yup.string().required('Vui lòng chọn phường/xã'),
})

const { errors, defineField, handleSubmit, setFieldValue, resetForm } = useForm({
  validationSchema: schema,
  initialValues: {
    receiverName: '',
    receiverPhone: '',
    detail: '',
    provinceName: '',
    districtName: '',
    wardName: '',
  },
})

const [receiverName] = defineField('receiverName')
const [receiverPhone] = defineField('receiverPhone')
const [detail] = defineField('detail')

const provinces = ref([])
const districts = ref([])
const wards = ref([])

const newAddress = ref({
  receiverName: '',
  receiverPhone: '',
  detail: '',
  provinceName: '',
  districtId: null,
  districtName: '',
  wardCode: '',
  wardName: '',
  address: '',
  isPrimary: false,
})

const selectedProvince = ref(null)
const selectedDistrict = ref(null)
const selectedWard = ref(null)

onMounted(async () => {
  await addressStore.fetchAddresses()
  const primary = addresses.value?.find((a) => a.primary)
  if (primary) selectAddress(primary)

  // Load provinces
  const res = await axiosClient.get('/shipping/provinces')
  provinces.value = res.data
})

watch(selectedProvince, async (p) => {
  if (!p) return

  setFieldValue('provinceName', p.ProvinceName)

  newAddress.value.provinceName = p.ProvinceName

  districts.value = []
  wards.value = []

  selectedDistrict.value = null
  selectedWard.value = null

  setFieldValue('districtName', '')
  setFieldValue('wardName', '')

  const res = await axiosClient.get('/shipping/districts', {
    params: {
      provinceId: p.ProvinceID,
    },
  })

  districts.value = res.data
})

watch(selectedDistrict, async (d) => {
  if (!d) return

  setFieldValue('districtName', d.DistrictName)

  newAddress.value.districtId = d.DistrictID
  newAddress.value.districtName = d.DistrictName

  wards.value = []
  selectedWard.value = null

  setFieldValue('wardName', '')

  const res = await axiosClient.get('/shipping/wards', {
    params: {
      districtId: d.DistrictID,
    },
  })

  wards.value = res.data
})

watch(selectedWard, (w) => {
  if (!w) return

  setFieldValue('wardName', w.WardName)

  newAddress.value.wardCode = w.WardCode
  newAddress.value.wardName = w.WardName
})

const selectAddress = async (addr) => {
  selectedAddressId.value = addr.id
  selectedAddress.value = addr

  await calculateShipping(addr)
}

const handleAddAddress = handleSubmit(async (values) => {
  const payload = {
    ...newAddress.value,
    receiverName: values.receiverName,
    receiverPhone: values.receiverPhone,
    detail: values.detail,
  }

  payload.address = [payload.detail, payload.wardName, payload.districtName, payload.provinceName]
    .filter(Boolean)
    .join(', ')

  try {
    const address = await addressStore.addAddress(payload)

    selectAddress(address)

    showNewForm.value = false

    resetForm({
      values: {
        receiverName: '',
        receiverPhone: '',
        detail: '',
        provinceName: '',
        districtName: '',
        wardName: '',
      },
    })

    setFieldValue('provinceName', '')
    setFieldValue('districtName', '')
    setFieldValue('wardName', '')
  } catch (e) {
    console.error(e)
  }
})

const handleCheckout = async () => {
  if (!selectedAddress.value) return
  const form = {
    shippingAddress: selectedAddress.value.address,

    receiverName: selectedAddress.value.receiverName,

    receiverPhone: selectedAddress.value.receiverPhone,

    paymentMethodId: paymentMethodId.value,

    districtId: selectedAddress.value.districtId,

    wardCode: selectedAddress.value.wardCode,
  }
  await checkout(form)
}

// handle voucher
const {
  checkoutItems,
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
  calculateShipping,
  shippingFee,
} = useCheckout()
</script>

<template>
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

          <div v-if="showNewForm" class="mt-4 border border-border p-4 space-y-3">
            <div class="grid gap-3 md:grid-cols-2">
              <div class="">
                <input
                  v-model="receiverName"
                  placeholder="Họ tên người nhận"
                  class="border border-border px-3 py-2 text-sm w-full"
                />
                <p class="mt-1 text-xs text-red-500">
                  {{ errors.receiverName }}
                </p>
              </div>
              <div>
                <input
                  v-model="receiverPhone"
                  placeholder="Số điện thoại"
                  class="border border-border px-3 py-2 text-sm w-full"
                />
                <p class="mt-1 text-xs text-red-500">
                  {{ errors.receiverPhone }}
                </p>
              </div>
            </div>

            <!-- Tỉnh/TP -->
            <select
              v-model="selectedProvince"
              class="w-full border border-border px-3 py-2 text-sm"
            >
              <option :value="null">Chọn tỉnh/thành phố</option>
              <option v-for="p in provinces" :key="p.ProvinceID" :value="p">
                {{ p.ProvinceName }}
              </option>
            </select>
            <p class="mt-1 text-xs text-red-500">
              {{ errors.provinceName }}
            </p>

            <!-- Quận/Huyện -->
            <select
              v-model="selectedDistrict"
              :disabled="!selectedProvince"
              class="w-full border border-border px-3 py-2 text-sm disabled:opacity-50"
            >
              <option :value="null">Chọn quận/huyện</option>
              <option v-for="d in districts" :key="d.DistrictID" :value="d">
                {{ d.DistrictName }}
              </option>
            </select>
            <p class="mt-1 text-xs text-red-500">
              {{ errors.districtName }}
            </p>

            <!-- Phường/Xã -->
            <select
              v-model="selectedWard"
              :disabled="!selectedDistrict"
              class="w-full border border-border px-3 py-2 text-sm disabled:opacity-50"
            >
              <option :value="null">Chọn phường/xã</option>
              <option v-for="w in wards" :key="w.WardCode" :value="w">
                {{ w.WardName }}
              </option>
            </select>
            <p class="mt-1 text-xs text-red-500">
              {{ errors.wardName }}
            </p>

            <!-- Số nhà, đường -->
            <input
              v-model="detail"
              placeholder="Số nhà, tên đường..."
              class="w-full border border-border px-3 py-2 text-sm"
            />
            <p class="mt-1 text-xs text-red-500">
              {{ errors.detail }}
            </p>

            <label class="flex items-center gap-2 text-sm">
              <input type="checkbox" v-model="newAddress.isPrimary" />
              Đặt làm mặc định
            </label>

            <button
              class="bg-ink px-6 py-2 text-xs uppercase tracking-widest text-ivory"
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
              <div class="mt-4 space-y-2">
                <div
                  v-for="i in checkoutItems"
                  :key="i.productVariantId"
                  class="flex justify-between text-sm"
                >
                  <span class="text-gray-500 truncate max-w-[200px]">
                    {{ i.name }} x{{ i.quantity }}
                  </span>
                  <div class="text-right">
                    <!-- Khi voucher không stackable và item có promotion -->
                    <template
                      v-if="
                        voucher &&
                        !voucher.isStackable &&
                        i.originalPrice &&
                        i.originalPrice !== i.price
                      "
                    >
                      <span class="block text-xs text-muted-foreground line-through">
                        {{ ulti.formatVND(i.price * i.quantity) }}
                      </span>
                      <span class="text-amber-600">
                        {{ ulti.formatVND(i.originalPrice * i.quantity) }}
                      </span>
                    </template>
                    <template v-else>
                      <span :class="i.originalPrice !== i.price ? 'text-red-500' : ''">
                        {{ ulti.formatVND(i.price * i.quantity) }}
                      </span>
                    </template>
                  </div>
                </div>
              </div>

              <div class="border-t border-border my-3" />
              <div class="flex justify-between">
                <span class="text-gray-500"> Tạm tính </span>

                <span>
                  {{ ulti.formatVND(subtotal) }}
                </span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500">Phí vận chuyển</span>

                <span v-if="selectedAddress && shippingFee > 0">
                  {{ ulti.formatVND(shippingFee) }}
                </span>

                <span v-else-if="selectedAddress"> Đang tính... </span>

                <span v-else class="text-gray-400"> Chọn địa chỉ </span>
              </div>

              <div
                v-if="voucher && !voucher.isStackable"
                class="text-xs text-amber-600 bg-amber-50 p-2 rounded"
              >
                ⚠️ Mã giảm giá này không áp dụng cùng với khuyến mãi sản phẩm. Giá gốc sẽ được tính
                cho các sản phẩm đang có khuyến mãi.
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
                  <div class="flex justify-between gap-2 mt-2">
                    <span class="rounded bg-red-100 px-2 py-1 text-xs text-red-600">
                      {{ timeLeft }}
                    </span>

                    <span class="rounded bg-green-100 px-2 py-1 text-xs text-green-600">
                      {{
                        timePromotion.discountType === 'PERCENT'
                          ? `-${timePromotion.discountValue}%`
                          : `-${ulti.formatVND(timePromotion.discountValue)}`
                      }}
                    </span>
                  </div>
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

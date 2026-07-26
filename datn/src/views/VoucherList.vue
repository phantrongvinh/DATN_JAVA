<script setup>
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { Copy, Check, Tag } from 'lucide-vue-next'
import { ref } from 'vue'
import { useNotificationStore } from '@/stores/useNotificationStore'
import ulti from '@/ulti/ulti'
import { useVoucherStore } from '@/stores/useVoucherStore'

const voucherStore = useVoucherStore()
const notification = useNotificationStore()

const { publicVouchers, loading } = storeToRefs(voucherStore)

onMounted(async () => {
  await voucherStore.fetchPublicVouchers()
  console.log(publicVouchers.value)
})

const copiedCode = ref(null)

const handleCopy = async (voucher) => {
  if (voucher.disabled) return

  try {
    await navigator.clipboard.writeText(voucher.code)
    copiedCode.value = voucher.code
    notification.notify(`Đã sao chép mã ${voucher.code}`, 'success')

    setTimeout(() => {
      if (copiedCode.value === voucher.code) copiedCode.value = null
    }, 4000)
  } catch (err) {
    notification.notify('Không thể sao chép mã, vui lòng thử lại', 'error')
  }
}

const discountLabel = (v) =>
  v.discountType === 'PERCENT'
    ? `Giảm ${v.discountValue}%`
    : `Giảm ${ulti.formatVND(v.discountValue)}`
</script>

<template>
  <div class="border border-border">
    <!-- Header -->
    <div class="border-b border-border bg-secondary/20 px-6 py-7">
      <h2 class="font-display text-2xl">Mã giảm giá</h2>

      <p class="mt-2 text-sm text-muted-foreground">
        {{ publicVouchers.length }} ưu đãi dành cho bạn
      </p>
    </div>

    <!-- Loading skeleton -->
    <div v-if="loading" class="grid grid-cols-1 gap-6 p-6 md:grid-cols-2">
      <div
        v-for="i in 4"
        :key="i"
        class="h-44 animate-pulse rounded-2xl border border-[#ECE3D4] bg-secondary"
      ></div>
    </div>

    <!-- Empty -->
    <div
      v-else-if="!publicVouchers.length"
      class="flex flex-col items-center justify-center px-6 py-28 text-center"
    >
      <div class="mb-8 flex h-24 w-24 items-center justify-center rounded-full bg-secondary">
        <Tag class="h-10 w-10 text-[#B8860B]" />
      </div>

      <h2 class="font-display text-lg">Chưa có mã giảm giá</h2>

      <p class="mt-3 max-w-md text-sm text-muted-foreground">
        Hiện tại chưa có chương trình ưu đãi nào. Hãy quay lại sau để nhận những mã giảm giá hấp
        dẫn.
      </p>

      <RouterLink to="/products" class="mt-5 border-b border-foreground pb-0.5 text-foreground">
        Khám phá sản phẩm
      </RouterLink>
    </div>
    <!-- Danh sách -->
    <div v-else class="grid grid-cols-1 gap-8 p-8 lg:grid-cols-2">
      <div
        v-for="v in publicVouchers"
        :key="v.code"
        class="group overflow-hidden rounded-2xl border border-[#ECE3D4] bg-white transition-all duration-500 hover:-translate-y-2 hover:shadow-2xl"
        :class="{ 'opacity-50': v.disabled }"
      >
        <div class="flex h-full">
          <!-- Left -->
          <div
            class="flex w-24 flex-col items-center justify-center border-r border-dashed border-[#E7D9C6] bg-[#FFF8EF]"
          >
            <Tag class="mb-2 h-7 w-7" :class="v.disabled ? 'text-gray-400' : 'text-[#D4A373]'" />

            <span class="text-xs font-semibold uppercase tracking-widest"> Voucher </span>
          </div>

          <!-- Right -->
          <div class="flex flex-1 flex-col p-6">
            <div class="flex items-start justify-between gap-3">
              <div>
                <h3 class="font-display text-xl text-[#3B2E2A]">
                  {{ discountLabel(v) }}
                </h3>

                <p class="mt-2 text-sm text-muted-foreground">
                  {{ v.description }}
                </p>
              </div>

              <span
                v-if="v.disabled"
                class="rounded-full bg-gray-100 px-3 py-1 text-[10px] uppercase tracking-widest text-gray-500"
              >
                {{ v.disabledReason }}
              </span>
            </div>

            <div class="mt-5 space-y-2 text-sm text-muted-foreground">
              <p v-if="v.minOrderValue > 0">
                Đơn tối thiểu:
                <span class="font-medium text-[#3B2E2A]">
                  {{ ulti.formatVND(v.minOrderValue) }}
                </span>
              </p>

              <p v-if="v.maxDiscount">
                Giảm tối đa:
                <span class="font-medium text-[#3B2E2A]">
                  {{ ulti.formatVND(v.maxDiscount) }}
                </span>
              </p>

              <p>
                HSD:
                <span class="font-medium text-[#3B2E2A]">
                  {{ ulti.formatDate(new Date(v.endDate)) }}
                </span>
              </p>

              <p>
                Áp dụng cùng KM:
                <span
                  class="font-semibold"
                  :class="v.stackable ? 'text-emerald-600' : 'text-red-500'"
                >
                  {{ v.stackable ? 'Có' : 'Không' }}
                </span>
              </p>
            </div>

            <div
              class="mt-auto flex items-center justify-between border-t border-dashed border-[#ECE3D4] pt-5"
            >
              <span
                class="rounded-lg bg-[#FFF8EF] px-3 py-2 font-mono text-sm font-bold tracking-widest text-[#8B5E3C]"
              >
                {{ v.code }}
              </span>

              <button
                @click="handleCopy(v)"
                :disabled="v.disabled"
                class="rounded-xl border px-4 py-2 text-xs font-semibold uppercase tracking-widest transition-all cursor-pointer"
                :class="
                  copiedCode === v.code
                    ? 'border-emerald-500 bg-emerald-50 text-emerald-700'
                    : 'border-[#8B5E3C] text-[#8B5E3C] hover:bg-[#8B5E3C] hover:text-white'
                "
              >
                <div class="flex items-center gap-2">
                  <Check v-if="copiedCode === v.code" class="h-4 w-4" />

                  <Copy v-else class="h-4 w-4" />

                  {{ copiedCode === v.code ? 'Đã chép' : 'Sao chép' }}
                </div>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { Minus, Plus, X, ShoppingBag } from 'lucide-vue-next'

import { useCartStore } from '@/stores/useCartStore'
import ulti from '@/ulti/ulti'
import { useCart } from '@/composables/useCart'
import SummaryRow from '@/components/site/SummaryRow.vue'
import { useCheckout } from '@/composables/useCheckout'
import { onMounted } from 'vue'

const { items, removeItem, updateQuantity } = useCart()

// handle thanh toan
const { subtotal, timeDiscount, finalPrice, timePromotion, timeLeft } = useCheckout()

//
const url = 'http://localhost:8080/uploads/images'
</script>

<template>
  <div class="container-x py-16">
    <!-- Breadcrumb -->
    <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">
      <RouterLink to="/"> Trang chủ </RouterLink>

      / Giỏ hàng
    </p>

    <h1 class="mt-3 font-display text-4xl md:text-5xl">Giỏ hàng</h1>

    <!-- Empty Cart -->
    <div
      v-if="items.length === 0"
      class="mt-16 flex flex-col items-center border border-border p-16 text-center"
    >
      <ShoppingBag class="mb-4 h-10 w-10 text-muted-foreground" :stroke-width="1" />

      <p class="text-muted-foreground">Giỏ hàng của bạn đang trống.</p>

      <RouterLink
        to="/products"
        class="mt-6 bg-ink px-8 py-3 text-xs uppercase tracking-widest text-ivory"
      >
        Tiếp tục mua sắm
      </RouterLink>
    </div>

    <!-- Cart -->
    <div v-else class="mt-12 grid gap-10 lg:grid-cols-[1fr_400px]">
      <!-- Items -->
      <div class="border border-border">
        <div
          class="hidden border-b border-border px-6 py-3 text-[11px] uppercase tracking-widest text-muted-foreground md:grid md:grid-cols-[2fr_120px_120px_120px_40px]"
        >
          <span>Sản phẩm</span>
          <span>Đơn giá</span>
          <span>Số lượng</span>
          <span>Thành tiền</span>
          <span></span>
        </div>

        <ul class="divide-y divide-border">
          <li
            v-for="item in items"
            :key="item.variantId"
            class="grid grid-cols-[80px_1fr_40px] items-center gap-4 p-6 md:grid-cols-[2fr_120px_120px_120px_40px]"
          >
            <!-- Product -->
            <div class="flex items-center gap-4 md:col-span-1">
              <img
                :src="url + '/' + item.image"
                :alt="item.name"
                class="h-20 w-20 object-contain"
              />

              <div class="hidden md:block">
                <p class="font-medium">
                  {{ item.name }}
                </p>

                <p class="mt-1 text-xs text-muted-foreground">
                  Size {{ item.size }}
                  ·
                  {{ item.color }}
                </p>
              </div>
            </div>

            <!-- Mobile -->
            <div class="md:hidden">
              <p class="font-medium">
                {{ item.name }}
              </p>

              <p class="mt-1 text-xs text-muted-foreground">
                Size {{ item.size }}
                ·
                {{ item.color }}
              </p>

              <p class="mt-2 font-display text-end">
                {{ ulti.formatVND(item.price) }}
              </p>
            </div>

            <!-- Price -->
            <span class="hidden text-sm md:block text-end">
              <span :class="item.originalPrice !== item.price ? 'text-red-500' : ''">
                {{ ulti.formatVND(item.price) }}
              </span>
              <span
                v-if="item.originalPrice && item.originalPrice !== item.price"
                class="block text-xs text-muted-foreground line-through"
              >
                {{ ulti.formatVND(item.originalPrice) }}
              </span>
            </span>

            <!-- Qty -->
            <div class="hidden justify-center items-center border border-border md:inline-flex">
              <button class="p-2" @click="updateQuantity(item.productVariantId, item.quantity - 1)">
                <Minus class="h-3 w-3" />
              </button>

              <span class="w-8 text-center text-sm">
                {{ item.quantity }}
              </span>

              <button class="p-2" @click="updateQuantity(item.productVariantId, item.quantity + 1)">
                <Plus class="h-3 w-3" />
              </button>
            </div>

            <!-- Total -->
            <span class="hidden font-display md:block">
              <span :class="item.originalPrice !== item.price ? 'text-red-500' : ''">
                {{ ulti.formatVND(item.price * item.quantity) }}
              </span>
              <span
                v-if="item.originalPrice && item.originalPrice !== item.price"
                class="block text-xs text-muted-foreground line-through"
              >
                {{ ulti.formatVND(item.originalPrice * item.quantity) }}
              </span>
            </span>

            <!-- Delete -->
            <button
              class="rounded-full p-2 text-muted-foreground hover:bg-secondary hover:text-foreground"
              @click="removeItem(item.productVariantId)"
            >
              <X class="h-4 w-4" />
            </button>
          </li>
        </ul>
      </div>

      <!-- Summary -->
      <aside class="h-fit border border-border p-6 sticky top-24">
        <h2 class="font-display text-xl">Tóm tắt</h2>

        <div class="mt-6 space-y-3 text-sm">
          <SummaryRow label="Tạm tính" :value="ulti.formatVND(subtotal)" />

          <!-- Khung giờ vàng -->
          <div v-if="timeDiscount > 0" class="flex justify-between">
            <div>
              Khung giờ vàng
              <span class="ml-2 rounded bg-red-100 px-2 py-1 text-xs text-red-600">
                {{ timeLeft }}
              </span>

              <span class="ml-2 rounded bg-green-100 px-2 py-1 text-xs text-green-600">
                {{
                  timePromotion.discountType === 'PERCENT'
                    ? `-${timePromotion.discountValue}%`
                    : `-${ulti.formatVND(timePromotion.discountValue)}`
                }}
              </span>
            </div>

            <span class="font-medium text-green-600"> -{{ ulti.formatVND(timeDiscount) }} </span>
          </div>

          <div class="border-t border-border pt-3">
            <div class="flex justify-between items-center">
              <span class="font-display text-lg"> Thành tiền </span>

              <span class="font-display text-xl text-red-500">
                {{ ulti.formatVND(finalPrice) }}
              </span>
            </div>
          </div>

          <!-- Voucher Input -->
          <!-- <div v-if="!voucher" class="pt-3">
            <input
              v-model="voucherCode"
              @keyup.enter="handleApplyVoucher"
              placeholder="Nhập mã giảm giá"
              class="w-full border border-border px-4 py-3 outline-none focus:border-black"
            />

            <p v-if="voucherError" class="mt-2 text-sm text-red-500">
              {{ voucherError }}
            </p>

            <button
              @click="handleApplyVoucher"
              class="mt-3 w-full border border-green-600 py-3 text-sm font-medium text-green-600 transition hover:bg-green-600 hover:text-white"
            >
              Áp dụng voucher
            </button>
          </div>

          <p v-if="error" class="text-sm text-red-500">
            {{ error }}
          </p> -->

          <RouterLink
            to="/checkout"
            class="mt-4 block bg-ink py-3 text-center text-xs uppercase tracking-widest text-ivory hover:bg-ink/85"
          >
            Tiến hành thanh toán
          </RouterLink>

          <RouterLink
            to="/products"
            class="block py-3 text-center text-xs uppercase tracking-widest text-muted-foreground hover:text-foreground"
          >
            Tiếp tục mua sắm
          </RouterLink>
        </div>
      </aside>
    </div>
  </div>
</template>

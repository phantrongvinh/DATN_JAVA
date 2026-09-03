<script setup>
import ulti from '@/ulti/ulti'

defineProps({
  order: { type: Object, required: true },
})
</script>

<template>
  <div class="mx-auto max-w-2xl bg-white p-10 text-black">
    <div class="mb-8 flex items-center justify-between border-b-2 border-black pb-4">
      <div>
        <h1 class="font-display text-2xl">Maison Calcio</h1>
        <p class="text-xs text-gray-500">Hóa đơn bán hàng</p>
      </div>
      <div class="text-right text-sm">
        <p>
          Mã đơn: <b>#{{ order?.id }}</b>
        </p>
        <p>Ngày: {{ ulti.formatDate(order?.createdAt) }}</p>
      </div>
    </div>

    <div class="mb-6 grid grid-cols-2 gap-4 text-sm">
      <div>
        <p class="font-medium">Người nhận</p>
        <p>{{ order?.receiverName }}</p>
        <p>{{ order?.receiverPhone }}</p>
        <p>{{ order?.shippingAddress }}</p>
      </div>
    </div>

    <table class="w-full text-sm">
      <thead>
        <tr class="border-b border-black text-left">
          <th class="py-2">Sản phẩm</th>
          <th>SL</th>
          <th class="text-right">Đơn giá</th>
          <th class="text-right">Thành tiền</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="item in order?.items"
          :key="item.productVariantId"
          class="border-b border-gray-200"
        >
          <td class="py-2">{{ item.productName }} ({{ item.color }}, {{ item.sizeName }})</td>
          <td>{{ item.quantity }}</td>
          <td class="text-right">{{ ulti.formatVND(item.price) }}</td>
          <td class="text-right">{{ ulti.formatVND(item.price * item.quantity) }}</td>
        </tr>
      </tbody>
    </table>

    <div class="mt-6 flex justify-end">
      <div class="w-64 space-y-1 text-sm">
        <div class="flex justify-between">
          <span>Tạm tính</span><span>{{ ulti.formatVND(order?.totalPrice) }}</span>
        </div>
        <div v-if="order?.discountAmount" class="flex justify-between">
          <span>Giảm giá</span><span>-{{ ulti.formatVND(order?.discountAmount) }}</span>
        </div>
        <div v-if="order?.timeDiscount" class="flex justify-between">
          <span>Khung giờ vàng</span><span>-{{ ulti.formatVND(order?.timeDiscount) }}</span>
        </div>
        <div class="flex justify-between border-t border-black pt-1 font-bold">
          <span>Tổng cộng</span><span>{{ ulti.formatVND(order?.finalPrice) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

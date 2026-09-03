<script setup>
import { ref, nextTick, watch } from 'vue'
import { useRouter } from 'vue-router'
import { MessageCircle, X, Send, Minus, Plus } from 'lucide-vue-next'
import { useChat } from '@/composables/useChat'
import { useCart } from '@/composables/useCart'
import { useNotificationStore } from '@/stores/useNotificationStore'
import ulti from '@/ulti/ulti'
import ReturnRequestModal from '@/components/ReturnRequestModal.vue'

const router = useRouter()
const { addItem } = useCart()
const notification = useNotificationStore()
const { messages, recommendedProducts, proposedItems, returnableOrders, loading, sendMessage } =
  useChat()

const open = ref(false)
const input = ref('')
const messagesEndRef = ref(null)

const handleSend = async () => {
  const text = input.value
  input.value = ''
  await sendMessage(text)
  scrollToBottom()
}

const scrollToBottom = () => {
  nextTick(() => messagesEndRef.value?.scrollIntoView({ behavior: 'smooth' }))
}

watch(messages, scrollToBottom, { deep: true })
watch([recommendedProducts, proposedItems, returnableOrders], scrollToBottom)

// ─── Click sản phẩm gợi ý → sang trang chi tiết ─────────
const handleProductClick = (productId) => {
  open.value = false
  router.push(`/products/${productId}`)
}

// ─── Chỉnh số lượng đề xuất trước khi xác nhận ─────────
const localQuantities = ref({})
watch(proposedItems, (items) => {
  if (!items) return
  const q = {}
  items.forEach((i) => (q[i.productVariantId] = i.quantity))
  localQuantities.value = q
})

const changeQty = (item, delta) => {
  const current = localQuantities.value[item.productVariantId] ?? item.quantity
  const next = Math.max(1, Math.min(item.stock, current + delta))
  localQuantities.value = { ...localQuantities.value, [item.productVariantId]: next }
}

const handleConfirmOrder = async () => {
  for (const item of proposedItems.value) {
    const qty = localQuantities.value[item.productVariantId] ?? item.quantity
    await addItem({
      name: item.productName,
      productVariantId: item.productVariantId,
      sizeName: item.sizeName,
      color: item.color,
      quantity: qty,
      originalPrice: item.price,
      price: item.price,
      image: item.image,
    })
  }

  // Chỉ chọn đúng những item vừa thêm để checkout (không kéo theo cả giỏ hàng cũ)
  sessionStorage.setItem(
    'checkoutSelectedIds',
    JSON.stringify(proposedItems.value.map((i) => i.productVariantId)),
  )

  open.value = false
  router.push('/checkout')
}

// ─── Chọn đơn để trả hàng ───────────────────────────────
const showReturnModal = ref(false)
const returningOrder = ref(null)
const handleSelectReturnableOrder = (order) => {
  returningOrder.value = { id: order.orderId }
  showReturnModal.value = true
}
</script>

<template>
  <button
    v-if="!open"
    @click="open = true"
    class="fixed bottom-6 right-6 z-50 flex h-14 w-14 items-center justify-center rounded-full bg-ink text-ivory shadow-lg transition-transform hover:scale-105"
  >
    <MessageCircle class="h-6 w-6" />
  </button>

  <div
    v-if="open"
    class="fixed bottom-6 right-6 z-50 flex h-[560px] w-[380px] flex-col border border-border bg-background shadow-2xl"
  >
    <div
      class="flex items-center justify-between border-b border-border bg-ink px-4 py-3 text-ivory"
    >
      <div>
        <p class="font-display text-sm">Trợ lý mua sắm</p>
        <p class="text-[10px] text-ivory/60">Maison Calcio AI</p>
      </div>
      <button @click="open = false"><X class="h-4 w-4" /></button>
    </div>

    <div class="flex-1 space-y-3 overflow-y-auto p-4">
      <div v-if="!messages.length" class="text-sm text-muted-foreground">
        Xin chào! Mình có thể giúp bạn tìm sản phẩm, đặt hàng, hoặc trả hàng. Thử gõ: "Tôi muốn mua
        giày đá bóng size 40" hoặc "Tôi muốn trả đơn hàng".
      </div>

      <div
        v-for="(m, i) in messages"
        :key="i"
        class="flex"
        :class="m.role === 'user' ? 'justify-end' : 'justify-start'"
      >
        <div
          class="max-w-[80%] px-3 py-2 text-sm"
          :class="m.role === 'user' ? 'bg-ink text-ivory' : 'bg-secondary text-foreground'"
        >
          {{ m.content }}
        </div>
      </div>

      <div v-if="loading" class="text-xs text-muted-foreground">Đang trả lời...</div>

      <!-- Sản phẩm gợi ý — click chuyển hướng chi tiết -->
      <div v-if="recommendedProducts?.length" class="space-y-2">
        <div
          v-for="p in recommendedProducts"
          :key="p.productVariantId"
          @click="handleProductClick(p.productId)"
          class="flex cursor-pointer items-center gap-2 border border-border p-2 transition-colors hover:bg-secondary"
        >
          <img :src="p.image" class="h-10 w-10 shrink-0 object-cover" />
          <div class="flex-1 text-xs">
            <p class="font-medium">{{ p.name }}</p>
            <p class="text-muted-foreground">{{ p.color }} · {{ p.sizeName }}</p>
          </div>
          <p class="text-xs font-medium">{{ ulti.formatVND(p.price) }}</p>
        </div>
      </div>

      <!-- Đề xuất đơn hàng — có thể chỉnh số lượng -->
      <div v-if="proposedItems?.length" class="border border-gold/40 bg-gold/5 p-3">
        <p class="mb-2 text-xs font-medium uppercase tracking-widest text-gold">
          Xác nhận đơn hàng
        </p>

        <div
          v-for="item in proposedItems"
          :key="item.productVariantId"
          class="flex items-center gap-2 border-b border-border/50 py-2 last:border-0"
        >
          <img :src="item.image" class="h-10 w-10 shrink-0 object-cover" />
          <div class="flex-1 text-xs">
            <p class="font-medium">{{ item.productName }}</p>
            <p class="text-muted-foreground">{{ item.color }} · {{ item.sizeName }}</p>
            <p class="mt-0.5 font-medium">{{ ulti.formatVND(item.price) }}</p>
          </div>

          <div class="flex items-center border border-border">
            <button @click="changeQty(item, -1)" class="p-1.5"><Minus class="h-3 w-3" /></button>
            <span class="w-6 text-center text-xs">{{
              localQuantities[item.productVariantId] ?? item.quantity
            }}</span>
            <button
              @click="changeQty(item, 1)"
              class="p-1.5"
              :disabled="(localQuantities[item.productVariantId] ?? item.quantity) >= item.stock"
            >
              <Plus class="h-3 w-3" />
            </button>
          </div>
        </div>

        <button
          @click="handleConfirmOrder"
          class="mt-3 w-full bg-ink py-2 text-xs uppercase tracking-widest text-ivory hover:bg-ink/90"
        >
          Xác nhận & đến trang thanh toán
        </button>
      </div>

      <!-- Đơn hàng có thể trả hàng -->
      <div
        v-if="returnableOrders?.length"
        class="space-y-2 border border-orange-300 bg-orange-50 p-3"
      >
        <p class="text-xs font-medium text-orange-700">Đơn hàng có thể trả hàng</p>
        <div
          v-for="o in returnableOrders"
          :key="o.orderId"
          @click="handleSelectReturnableOrder(o)"
          class="cursor-pointer border border-orange-200 bg-white p-2 text-xs transition-colors hover:bg-orange-100"
        >
          <p class="font-medium">Đơn #{{ o.orderId }} — {{ ulti.formatVND(o.finalPrice) }}</p>
          <p class="text-muted-foreground">{{ o.productNames.join(', ') }}</p>
          <p class="mt-1 text-[10px] text-orange-600">
            Giao ngày {{ ulti.formatDate(o.deliveredAt) }}
          </p>
        </div>
      </div>

      <div ref="messagesEndRef" />
    </div>

    <div class="flex items-center gap-2 border-t border-border p-3">
      <input
        v-model="input"
        @keyup.enter="handleSend"
        placeholder="Nhập tin nhắn..."
        class="flex-1 border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
      />
      <button @click="handleSend" class="bg-ink p-2.5 text-ivory hover:bg-ink/90">
        <Send class="h-4 w-4" />
      </button>
    </div>
  </div>

  <ReturnRequestModal v-model:show="showReturnModal" :order="returningOrder" />
</template>

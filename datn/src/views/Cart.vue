<template>
  <div class="main-content container-fluid my-4">
    <div class="row mx-5">
      <div class="col-md-7">
        <div class="card">
          <div class="card-header pb-0 px-3">
            <h6 class="mb-0">Giỏ hàng ( {{ count }} sản phẩm )</h6>
          </div>
          <div class="card-body pt-4 p-3">
            <ul class="list-group" v-if="items && items.length > 0">
              <li
                class="list-group-item d-flex p-4 mb-2 border border-radius-lg"
                v-for="i in items"
                :key="i.productVariantId"
              >
                <CartItem :i="i" @delete="handleDelete" @updateQuantity="handleUpdateQuantity" />
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="col-md-5">
        <div class="position-sticky top-10">
          <Bill :data="items"></Bill>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Bill from '@/components/cart/Bill.vue'
import CartItem from '@/components/cart/CartItem.vue'
import { useCart } from '@/composables/useCart'
import { useCartStore } from '@/stores/useCartStore'
import { computed, ref, watch } from 'vue'

const { items, removeItem, updateQuantity } = useCart()

const count = computed(() => {
  return items?.value.reduce((sum, item) => sum + (item.quantity || 0), 0)
})

const handleDelete = async (id) => {
  await removeItem(id)
}

const handleUpdateQuantity = async ({ id, delta }) => {
  const item = items?.value.find((i) => i.productVariantId === id)
  if (!item) return
  const newQuantity = item.quantity + delta
  await updateQuantity(id, newQuantity)
}
</script>

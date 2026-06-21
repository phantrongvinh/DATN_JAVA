<template>
  <div class="main-content container-fluid my-4">
    <div class="row mx-5">
      <div class="col-md-7">
        <div class="card">
          <div class="card-header pb-0 px-3">
            <h6 class="mb-0">Giỏ hàng ( số lượng )</h6>
          </div>
          <div class="card-body pt-4 p-3">
            <ul class="list-group" v-if="items && items.length > 0">
              <li
                class="list-group-item d-flex p-4 mb-2 border border-radius-lg"
                v-for="i in items"
              >
                <CartItem :i="i" />
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
import { useCartStore } from '@/stores/useCartStore'
import { storeToRefs } from 'pinia'

const cartStore = useCartStore()

const { items } = storeToRefs(cartStore)
</script>

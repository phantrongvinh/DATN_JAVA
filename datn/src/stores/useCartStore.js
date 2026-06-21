import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCartStore = defineStore(
  'cart',
  () => {
    // state
    const loadding = ref(false)
    const error = ref(null)
    const items = ref([])

    // actions
    // add items cart tự động vào localStorage của thư viện
    function addItem(item) {
      const existing = items.value.find(
        (i) => i.productVariantId === item.productVariantId && i.sku === item.sku,
      )
      if (existing) {
        existing.quantity += item.quantity
      } else {
        items.value.push(item)
      }
    }

    return {
      loadding,
      error,
      items,
      addItem,
    }
  },
  { persist: true },
)

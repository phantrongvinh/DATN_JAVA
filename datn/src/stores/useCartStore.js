import cartAPI from '@/api/cartAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useCartStore = defineStore(
  'cart',
  () => {
    // state
    const loadding = ref(false)
    const error = ref(null)
    const items = ref([])
    const message = ref(null)

    function ensureArray() {
      if (!Array.isArray(items.value)) {
        items.value = []
      }
    }

    // actions
    // add items cart tự động vào localStorage của thư viện
    function addItemLocal(item) {
      ensureArray()
      const existing = items.value.find(
        (i) => i.productVariantId === item.productVariantId && i.sku === item.sku,
      )
      if (existing) {
        existing.quantity += item.quantity
      } else {
        items.value.push(item)
      }
    }
    function removeItemLocal(variantId) {
      ensureArray()
      items.value = items.value.filter((i) => i.productVariantId !== variantId)
    }

    function updateQuantityLocal(variantId, quantity) {
      ensureArray()
      const item = items.value.find((i) => i.productVariantId === variantId)
      if (!item) return
      if (quantity <= 0) removeItemLocal(variantId)
      else item.quantity = quantity
    }

    // ─── Logged-in actions (API) ─────────────────────────

    async function fetchCart() {
      loadding.value = true
      error.value = null
      ensureArray()
      try {
        const res = await cartAPI.fetchCart()
        items.value = res
      } catch (err) {
        error.value = err.data?.response?.message
        throw err
      } finally {
        loadding.value = false
      }
    }

    async function addItemServer(item) {
      loadding.value = true
      error.value = null
      try {
        const res = await cartAPI.addItemServer({
          productVariantId: item.productVariantId,
          quantity: item.quantity,
        })
        message.value = res
        await fetchCart()
      } catch (err) {
        error.value = err.data?.response?.message
        throw err
      } finally {
        loadding.value = false
      }
    }

    async function removeItemServer(variantId) {
      loadding.value = true
      error.value = null
      try {
        const res = await cartAPI.removeItemServer(variantId)
        message.value = res
        await fetchCart()
      } catch (err) {
        error.value = err.data?.response?.message
        throw err
      } finally {
        loadding.value = false
      }
    }

    async function updateQuantityServer(variantId, quantity) {
      loadding.value = true
      error.value = null
      try {
        const res = await cartAPI.updateQuantityServer(variantId, quantity)
        message.value = res
        await fetchCart()
      } catch (err) {
        error.value = err.data?.response?.message
        throw err
      } finally {
        loadding.value = false
      }
    }

    async function mergeCartToServer() {
      loadding.value = true
      error.value = null
      ensureArray()

      try {
        if (items.value.length === 0) return

        console.log('🔍 Local items trước khi merge:', JSON.parse(JSON.stringify(items.value)))

        const payload = items.value.map((i) => ({
          productVariantId: i.productVariantId,
          quantity: i.quantity,
        }))

        console.log('🔍 Payload gửi lên server:', payload)
        items.value = await cartAPI.mergeCartToServer(payload)
      } catch (err) {
        error.value = err.data?.response?.message
        throw err
      } finally {
        loadding.value = false
      }
    }

    function clearLocal() {
      items.value = []
    }

    return {
      loadding,
      error,
      items,
      addItemLocal,
      updateQuantityLocal,
      removeItemLocal,
      fetchCart,
      addItemServer,
      removeItemServer,
      updateQuantityServer,
      mergeCartToServer,
      clearLocal,
    }
  },
  { persist: true },
)

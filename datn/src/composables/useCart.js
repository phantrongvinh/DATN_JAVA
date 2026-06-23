import { useAuthStore } from '@/stores/useAuthStore'
import { useCartStore } from '@/stores/useCartStore'
import { storeToRefs } from 'pinia'

export function useCart() {
  const cartStore = useCartStore()
  const authStore = useAuthStore()
  const { items } = storeToRefs(cartStore)

  async function addItem(item) {
    if (authStore.isAuthenticated) {
      await cartStore.addItemServer(item)
    } else {
      cartStore.addItemLocal(item)
    }
  }

  async function removeItem(variantId) {
    if (authStore.isAuthenticated) {
      await cartStore.removeItemServer(variantId)
    } else {
      cartStore.removeItemLocal(variantId)
    }
  }

  async function updateQuantity(variantId, quantity) {
    if (authStore.isAuthenticated) {
      await cartStore.updateQuantityServer(variantId, quantity)
    } else {
      cartStore.updateQuantityLocal(variantId, quantity)
    }
  }

  return { items, addItem, removeItem, updateQuantity }
}

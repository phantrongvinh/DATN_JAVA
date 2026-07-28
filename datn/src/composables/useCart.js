import { useAuthStore } from '@/stores/useAuthStore'
import { useCartStore } from '@/stores/useCartStore'
import { useNotificationStore } from '@/stores/useNotificationStore'
import { storeToRefs } from 'pinia'

export function useCart() {
  const cartStore = useCartStore()
  const authStore = useAuthStore()
  const { items } = storeToRefs(cartStore)

  const notification = useNotificationStore()

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
      notification.notify('Xóa sản phẩm trong giỏ hàng thành công', 'success')
    } else {
      cartStore.removeItemLocal(variantId)
      notification.notify('Xóa sản phẩm trong giỏ hàng thành công', 'success')
    }
  }

  async function updateQuantity(variantId, quantity) {
    if (quantity <= 0) {
      await removeItem(variantId)
      return
    }

    const item = items.value.find((i) => i.productVariantId === variantId)
    if (item?.stock && quantity > item.stock) {
      notification.notify(`Chỉ còn ${item.stock} sản phẩm trong kho`, 'warning')
      return
    }

    if (authStore.isAuthenticated) {
      await cartStore.updateQuantityServer(variantId, quantity)
    } else {
      cartStore.updateQuantityLocal(variantId, quantity)
    }
    notification.notify('Cập nhật giỏ hàng thành công', 'success')
  }

  return { items, addItem, removeItem, updateQuantity }
}

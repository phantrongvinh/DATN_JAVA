import wishlistAPI from '@/api/wishlistAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useWishlistStore = defineStore('wishlist', () => {
  const loading = ref(false)
  const error = ref(null)
  const message = ref('')

  const ids = ref([])
  const wishlists = ref([])

  const fetchWishlist = async () => {
    loading.value = true
    error.value = null
    try {
      wishlists.value = await wishlistAPI.getWishList()
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: err.message }
    } finally {
      loading.value = false
    }
  }

  const fetchIds = async () => {
    loading.value = true
    error.value = null
    try {
      ids.value = await wishlistAPI.getIds()
      console.log(ids.value)
    } catch (err) {
      error.value = err.response?.data?.message
      return { success: false, errorMessages: err.message }
    } finally {
      loading.value = false
    }
  }

  const toggle = async (productId) => {
    try {
      const res = await wishlistAPI.toggle(productId)
      if (res.inWishlist) {
        ids.value = [...ids.value, productId]
      } else {
        ids.value = ids.value.filter((id) => id !== productId)
        wishlists.value = wishlists.value.filter((p) => p.id !== productId)
      }
      return res.inWishlist
    } catch (err) {
      error.value = err.response?.data?.message
      throw err
    }
  }

  const isInWishlist = (productId) => ids.value.includes(productId)

  return {
    loading,
    error,
    message,
    ids,
    wishlists,
    fetchWishlist,
    fetchIds,
    toggle,
    isInWishlist,
  }
})

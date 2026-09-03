import { ref } from 'vue'
import chatAPI from '@/api/chatAPI'

export function useChat() {
  const messages = ref([])
  const recommendedProducts = ref(null)
  const proposedItems = ref(null)
  const returnableOrders = ref(null)
  const loading = ref(false)

  const sendMessage = async (text) => {
    if (!text.trim()) return

    messages.value.push({ role: 'user', content: text })
    loading.value = true
    recommendedProducts.value = null
    proposedItems.value = null
    returnableOrders.value = null

    try {
      const historyForApi = messages.value.slice(0, -1)
      const res = await chatAPI.sendMessage(historyForApi, text)

      messages.value.push({ role: 'assistant', content: res.reply })
      recommendedProducts.value = res.recommendedProducts?.length ? res.recommendedProducts : null
      proposedItems.value = res.proposedItems?.length ? res.proposedItems : null
      returnableOrders.value = res.returnableOrders?.length ? res.returnableOrders : null
    } catch (err) {
      messages.value.push({
        role: 'assistant',
        content: 'Xin lỗi, đã có lỗi xảy ra, bạn thử lại nhé.',
      })
    } finally {
      loading.value = false
    }
  }

  return { messages, recommendedProducts, proposedItems, returnableOrders, loading, sendMessage }
}

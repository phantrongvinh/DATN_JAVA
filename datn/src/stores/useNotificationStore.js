import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNotificationStore = defineStore('notification', () => {
  const show = ref(false)
  const message = ref('')
  const type = ref('')
  let timeoutId = null

  function notify(newMessage, newType) {
    if (timeoutId) clearTimeout(timeoutId)
    message.value = newMessage
    type.value = newType
    show.value = true
    timeoutId = setTimeout(() => {
      show.value = false
    }, 3000)
  }

  return { show, message, type, notify }
})

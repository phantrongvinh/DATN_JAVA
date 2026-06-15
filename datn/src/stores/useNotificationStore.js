import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useNotificationStore = defineStore('notification', () => {
  // states
  const show = ref(false)
  const message = ref('')
  const type = ref('')

  // actions
  function notify(newMessage, newType) {
    message.value = newMessage
    type.value = newType
    show.value = true
    setTimeout(() => {
      show.value = false
    }, 3000)
  }

  return { show, message, type, notify }
})

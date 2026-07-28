<script setup>
import { RouterView, useRoute, useRouter } from 'vue-router'
import Toast from './components/Toast.vue'
import { useSiteSettingsStore } from './stores/useSiteSettingsStore.js'
import { onMounted } from 'vue'

const siteSettingsStore = useSiteSettingsStore()
const router = useRouter()
const route = useRoute()

onMounted(async () => {
  await siteSettingsStore.fetchFromServer()
  // ─── Xử lý Google OAuth callback ─────────────────
  const success = route.query.success
  const error = route.query.error
  const token = route.query.token // nếu BE trả về token qua query param

  if (success === 'true') {
    // Nếu BE trả token qua URL
    if (token) {
      localStorage.setItem('token', token)
    }
    notification.notify('Đăng nhập Google thành công!', 'success')
    // Xóa query params khỏi URL
    router.replace({ query: {} })
  } else if (error) {
    notification.notify('Đăng nhập Google thất bại', 'error')
    router.replace({ query: {} })
  }
})
</script>

<template>
  <RouterView />
  <Toast />
</template>

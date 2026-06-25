<template>
  <div v-if="user">
    <div class="container-x py-16">
      <!-- Breadcrumb -->
      <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">
        <RouterLink to="/"> Trang chủ </RouterLink>

        / Hồ sơ
      </p>

      <!-- Title -->
      <h1 class="mt-3 font-display text-4xl md:text-5xl">
        Xin chào,

        <span class="italic text-gold">
          {{ user.fullName }}
        </span>
      </h1>

      <div class="mt-12 grid gap-10 md:grid-cols-[240px_1fr]">
        <AccountSidebar />

        <RouterView />
      </div>
    </div>
  </div>
</template>
<script setup>
import { useRouter } from 'vue-router'
import AccountSidebar from './AccountSidebar.vue'
import { useAuthStore } from '@/stores/useAuthStore.js'
import { watch } from 'vue'
import { storeToRefs } from 'pinia'

const router = useRouter()
const authStore = useAuthStore()

const { user } = storeToRefs(authStore)

watch(
  user,
  (newUser) => {
    if (!newUser) {
      router.push('/login')
    }
  },
  { immediate: true },
)
</script>

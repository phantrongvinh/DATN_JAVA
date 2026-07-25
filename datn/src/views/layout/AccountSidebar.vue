<script setup>
import { useAuthStore } from '@/stores/useAuthStore'
import { LogOut, Package, Heart, MapPin, Settings } from 'lucide-vue-next'
import { useRoute } from 'vue-router'

const menuItems = [
  {
    icon: Settings,
    label: 'Thông tin cá nhân',
    path: '/profile',
  },
  {
    icon: Package,
    label: 'Đơn hàng của tôi',
    path: '/profile/orders',
  },
  {
    icon: Heart,
    label: 'Yêu thích',
    path: '/profile/wishlist',
  },
]

const route = useRoute()

const authStore = useAuthStore()
</script>
<template>
  <!-- Sidebar -->
  <aside class="space-y-1 text-sm">
    <RouterLink
      v-for="item in menuItems"
      :key="item.path"
      :to="item.path"
      class="flex w-full items-center gap-3 border-l-2 px-3 py-2.5"
      :class="route.path === item.path ? 'border-foreground bg-secondary' : 'border-transparent'"
    >
      <component :is="item.icon" class="h-4 w-4" />
      {{ item.label }}
    </RouterLink>

    <button
      @click="authStore.logout()"
      class="mt-6 flex w-full items-center gap-3 border-l-2 border-transparent px-3 py-2.5 text-left text-red-500 transition-colors hover:bg-secondary/60 cursor-pointer"
    >
      <LogOut class="h-4 w-4" :stroke-width="1.5" />

      Đăng xuất
    </button>
  </aside>
</template>

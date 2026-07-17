<script setup>
import { computed, onMounted } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import {
  LayoutDashboard,
  Package,
  Users,
  ShoppingCart,
  LogOut,
  ArrowLeft,
  FileText,
  Palette,
  Tag,
  Clock,
} from 'lucide-vue-next'

import { useAuthStore } from '@/stores/useAuthStore'

const authStore = useAuthStore()

const router = useRouter()
const route = useRoute()

const user = computed(() => authStore.user)

const navs = [
  {
    to: '/admin',
    label: 'Dashboard',
    icon: LayoutDashboard,
    exact: true,
  },
  {
    to: '/admin/products',
    label: 'Hàng hóa',
    icon: Package,
  },
  {
    to: '/admin/promotion',
    label: 'Khuyến mãi',
    icon: Tag,
  },
  {
    // to: '/admin/landing',
    to: '/admin/time-promotion',
    label: 'Khung giờ KM',
    icon: Clock,
  },
  {
    // to: '/admin/orders',
    to: '/admin/orders',
    label: 'Đơn hàng',
    icon: ShoppingCart,
  },
  {
    to: '/admin/users',
    label: 'Người dùng',
    icon: Users,
  },
  // {
  //   // to: '/admin/blog',
  //   to: '/',
  //   label: 'Bài viết',
  //   icon: FileText,
  // },
  {
    to: '/admin/landing',
    label: 'Trang chủ',
    icon: Palette,
  },
]

const isActive = (item) => {
  if (item.exact) {
    return route.path === item.to
  }

  return route.path.startsWith(item.to)
}

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}
</script>

<template>
  <div class="flex h-screen bg-secondary/30 overflow-hidden">
    <!-- Sidebar -->
    <aside
      class="hidden md:flex w-60 shrink-0 h-screen flex-col border-r border-border bg-background"
    >
      <!-- Header -->
      <div class="border-b border-border px-6 py-5 shrink-0">
        <RouterLink to="/" class="flex items-baseline gap-1">
          <span class="font-display text-xl font-semibold">Maison</span>
          <span class="font-display text-xl italic text-gold">Calcio</span>
        </RouterLink>

        <p class="mt-1 text-[10px] uppercase tracking-[0.25em] text-muted-foreground">Admin</p>
      </div>

      <!-- Menu -->
      <nav class="flex-1 overflow-y-auto p-3 space-y-0.5">
        <RouterLink
          v-for="item in navs"
          :key="item.to"
          :to="item.to"
          class="flex items-center gap-3 rounded-sm px-3 py-2.5 text-sm transition-colors"
          :class="isActive(item) ? 'bg-ink text-ivory' : 'hover:bg-secondary'"
        >
          <component :is="item.icon" class="h-4 w-4" :stroke-width="1.5" />
          {{ item.label }}
        </RouterLink>
      </nav>

      <!-- Footer -->
      <div class="shrink-0 border-t border-border p-4 text-sm">
        <p class="font-medium">{{ user?.name }}</p>
        <p class="truncate text-xs text-muted-foreground">
          {{ user?.email }}
        </p>

        <div class="mt-3 flex gap-2">
          <RouterLink
            to="/"
            class="flex flex-1 items-center justify-center gap-1.5 border border-border px-2 py-1.5 text-xs hover:bg-secondary"
          >
            <ArrowLeft class="h-3 w-3" />
            Cửa hàng
          </RouterLink>

          <button
            @click="handleLogout"
            class="flex items-center justify-center border border-destructive p-1.5 text-destructive hover:bg-secondary"
          >
            <LogOut class="h-3.5 w-3.5" />
          </button>
        </div>
      </div>
    </aside>

    <!-- Content -->
    <main class="flex-1 h-screen overflow-y-auto">
      <!-- <RouterView v-slot="{ Component, route }">
        <Transition name="page" mode="out-in" appear>
          <component :is="Component" :key="route.fullPath" />
        </Transition>
      </RouterView> -->

      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.page-enter-active,
.page-leave-active {
  transition: opacity 0.2s ease;
}

.page-enter-from,
.page-leave-to {
  opacity: 0;
}
</style>

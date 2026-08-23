<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { storeToRefs } from 'pinia'
import {
  Search,
  ShoppingBag,
  Heart,
  User,
  LogOut,
  UserCircle2,
  LayoutDashboard,
  Package,
} from 'lucide-vue-next'

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'

import { useAudienceStore } from '@/stores/useAudienceStore'
import { useAuthStore } from '@/stores/useAuthStore'
import { useSiteSettingsStore } from '@/stores/useSiteSettingsStore'
import { useCart } from '@/composables/useCart'
import { useWishlistStore } from '@/stores/useWishlistStore'
import AuthPopover from '@/components/AuthPopover.vue'
// import NotificationBell from './NotificationBell.vue'

const router = useRouter()
const route = useRoute()

const audienceStore = useAudienceStore()
const authStore = useAuthStore()
const siteSettingsStore = useSiteSettingsStore()
const wishlistStore = useWishlistStore()

onMounted(async () => {
  await audienceStore.fetchAudiences()
  if (authStore.isAuthenticated) {
    await wishlistStore.fetchIds()
  }
})

const { audiences } = storeToRefs(audienceStore)
const { user, isAuthenticated, isAdmin } = storeToRefs(authStore)
const { landing } = storeToRefs(siteSettingsStore)

const { ids } = storeToRefs(wishlistStore)

const { items } = useCart()

const cartCount = computed(() => items.value?.reduce((sum, i) => sum + i.quantity, 0) ?? 0)

const t = computed(() => landing.value.theme)

const audienceLabel = (name) => {
  const map = { Men: 'Nam', Women: 'Nữ', Kids: 'Trẻ em' }
  return map[name] ?? 'Phi giới tính'
}

// scroll shadow
const scrolled = ref(false)
const onScroll = () => {
  scrolled.value = window.scrollY > 8
}
onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))

const handleLogout = () => {
  authStore.logout()
  router.push('/')
}
</script>

<template>
  <header
    class="sticky top-0 z-50 w-full border-b transition-shadow"
    :class="{ 'shadow-sm': scrolled }"
    :style="{
      backgroundColor: `color-mix(in oklab, ${t?.headerBg} 92%, transparent)`,
      color: t?.headerText,
      borderColor: `color-mix(in oklab, ${t?.headerText} 15%, transparent)`,
      backdropFilter: 'blur(10px)',
    }"
  >
    <!-- Announcement bar -->
    <div
      v-if="landing?.announcement"
      class="text-center text-[11px] uppercase tracking-[0.25em]"
      :style="{ backgroundColor: t?.announcementBg, color: t?.announcementText }"
    >
      <p class="container-x py-2">{{ landing?.announcement }}</p>
    </div>

    <div class="container-x flex h-16 items-center justify-between gap-6">
      <!-- Logo -->
      <RouterLink to="/" class="flex items-baseline gap-1">
        <span class="font-display text-2xl font-semibold tracking-tight">Maison</span>
        <span class="font-display text-2xl italic" :style="{ color: t?.headerAccent }">Calcio</span>
      </RouterLink>

      <!-- Navigation -->
      <nav class="hidden items-center gap-8 md:flex">
        <RouterLink
          to="/"
          class="text-sm tracking-wide opacity-80 transition-opacity hover:opacity-100"
          :class="{ 'font-medium opacity-100': route.path === '/' }"
        >
          Trang chủ
        </RouterLink>

        <RouterLink
          v-for="audience in audiences"
          :key="audience.id"
          :to="{ name: 'products', query: { audienceIds: audience.id } }"
          class="text-sm tracking-wide opacity-80 transition-opacity hover:opacity-100"
          :class="{ 'font-medium opacity-100': route.query.audienceIds == audience.id }"
        >
          {{ audienceLabel(audience.name) }}
        </RouterLink>
      </nav>

      <!-- Actions -->
      <div class="flex items-center gap-1">
        <!-- <NotificationBell /> -->

        <!-- User -->
        <AuthPopover guest-message="Đăng nhập để xem thông tin tài khoản">
          <template #icon>
            <UserCircle2 class="h-5 w-5" />
            <span class="hidden max-w-[120px] truncate sm:inline">{{ user?.fullName }}</span>
          </template>

          <template #dropdown>
            <div class="" :style="{ color: t?.subHeaderText }">
              <div class="p-3">
                <p class="text-sm font-medium">{{ user?.fullName }}</p>
                <p class="truncate text-xs text-muted-foreground">{{ user?.email }}</p>
              </div>
              <hr class="border-border" />

              <RouterLink
                to="/profile"
                class="flex items-center px-3 py-2.5 text-sm hover:bg-secondary"
              >
                <User class="mr-2 h-4 w-4" /> Hồ sơ cá nhân
              </RouterLink>
              <RouterLink
                to="/profile/orders"
                class="flex items-center px-3 py-2.5 text-sm hover:bg-secondary"
              >
                <Package class="mr-2 h-4 w-4" /> Đơn hàng của tôi
              </RouterLink>
              <RouterLink
                to="/profile/wishlist"
                class="flex items-center px-3 py-2.5 text-sm hover:bg-secondary"
              >
                <Heart class="mr-2 h-4 w-4" /> Yêu thích
              </RouterLink>

              <template v-if="isAdmin">
                <hr class="border-border" />
                <RouterLink
                  to="/admin"
                  class="flex items-center px-3 py-2.5 text-sm hover:bg-secondary"
                >
                  <LayoutDashboard class="mr-2 h-4 w-4" /> Quản trị
                </RouterLink>
              </template>

              <hr class="border-border" />
              <button
                @click="handleLogout"
                class="flex w-full items-center px-3 py-2.5 text-left text-sm text-red-500 hover:bg-red-50"
              >
                <LogOut class="mr-2 h-4 w-4" /> Đăng xuất
              </button>
            </div>
          </template>
        </AuthPopover>

        <!-- Wishlist -->
        <AuthPopover
          guest-message="Đăng nhập để lưu sản phẩm yêu thích"
          @click-authenticated="router.push('/profile/wishlist')"
        >
          <template #icon>
            <Heart class="hidden h-[18px] w-[18px] sm:inline-flex" />
            <span
              v-if="isAuthenticated && ids.length > 0"
              class="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full px-1 text-[10px] font-medium"
              :style="{ background: t.headerAccent, color: t.headerBg }"
            >
              {{ ids.length }}
            </span>
          </template>
        </AuthPopover>

        <!-- Cart — giữ nguyên, không đổi -->
        <RouterLink
          to="/cart"
          aria-label="Giỏ hàng"
          class="relative rounded-full p-2 transition-colors hover:bg-white/10"
        >
          <ShoppingBag class="h-[18px] w-[18px]" />
          <span
            v-if="cartCount > 0"
            class="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full px-1 text-[10px] font-medium"
            :style="{ background: t.headerText, color: t.headerBg }"
          >
            {{ cartCount }}
          </span>
        </RouterLink>
      </div>
    </div>
  </header>
</template>

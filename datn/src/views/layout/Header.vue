<script setup>
import { ref } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { Search, ShoppingBag, User, LogOut, UserCircle2, LayoutDashboard } from 'lucide-vue-next'

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
import { useBrandStore } from '@/stores/useBrandStore'
import { useCategoryStore } from '@/stores/useCategoryStore'
import ulti from '@/ulti/ulti'
import { storeToRefs } from 'pinia'
import { computed, onMounted } from 'vue'
import { useCart } from '@/composables/useCart'

const scrolled = ref(false)

const router = useRouter()

const route = useRoute()

// // stores
const audienceStore = useAudienceStore()
const brandStore = useBrandStore()
const categoryStore = useCategoryStore()
const authStore = useAuthStore()

onMounted(async () => {
  await Promise.all([
    audienceStore.fetchAudiences(),
    brandStore.fetchBrand(),
    categoryStore.fetchAccessory(),
  ])
})

const { audiences } = storeToRefs(audienceStore)
const { brands } = storeToRefs(brandStore)
const { accessories } = storeToRefs(categoryStore)
const isAuthenticated = computed(() => authStore.isAuthenticated)
const { user, isAdmin } = storeToRefs(authStore)

const audienceLabel = (name) => {
  const map = {
    Men: 'Nam',
    Women: 'Nữ',
    Kids: 'Trẻ em',
  }
  return map[name] ?? 'Phi giới tính'
}

// logout
const handleLogout = () => {
  try {
    authStore.logout()
    router.push('/')
    return { success: true }
  } catch (error) {
    return { success: false }
  }
}

// cart count

const { items } = useCart()
</script>
<template>
  <header
    class="sticky top-0 z-50 w-full border-b bg-background/85 backdrop-blur-md"
    :class="{ 'shadow-sm': scrolled }"
  >
    <div class="container-x flex h-16 items-center justify-between gap-6">
      <!-- Logo -->
      <RouterLink to="/" class="flex items-baseline gap-1">
        <span class="font-display text-2xl font-semibold tracking-tight"> Maison </span>
        <span class="font-display text-2xl italic text-gold"> Calcio </span>
      </RouterLink>

      <!-- Navigation -->
      <nav class="hidden items-center gap-8 md:flex">
        <RouterLink
          to="/"
          class="text-sm tracking-wide text-foreground/80 transition-colors hover:text-foreground"
          active-class="font-medium text-foreground"
          >Trang chủ</RouterLink
        >
        <RouterLink
          v-for="audience in audiences"
          :key="audience.id"
          :to="{
            name: 'products',
            query: {
              audienceIds: audience.id,
            },
          }"
          class="text-sm tracking-wide text-foreground/80 transition-colors hover:text-foreground"
          :class="{ 'font-medium text-foreground': route.query.audienceIds == audience.id }"
        >
          {{ audienceLabel(audience.name) }}
        </RouterLink>
      </nav>

      <!-- Actions -->
      <div class="flex items-center gap-1">
        <!-- Search -->
        <button aria-label="Tìm kiếm" class="rounded-full p-2 hover:bg-accent">
          <Search size="20" />
        </button>

        <!-- User -->
        <DropdownMenu v-if="isAuthenticated">
          <DropdownMenuTrigger as-child>
            <button
              class="flex items-center gap-2 rounded-full px-2 py-1.5 text-sm hover:bg-accent"
            >
              <UserCircle2 size="20" />

              <span class="hidden max-w-[120px] truncate sm:inline">
                {{ user?.fullName }}
              </span>
            </button>
          </DropdownMenuTrigger>

          <DropdownMenuContent class="w-56">
            <DropdownMenuLabel>
              <div class="font-normal">
                <p class="text-sm font-medium">
                  {{ user?.fullName }}
                </p>

                <p class="truncate text-xs text-muted-foreground">
                  {{ user?.email }}
                </p>
              </div>
            </DropdownMenuLabel>

            <DropdownMenuSeparator />

            <RouterLink to="/profile">
              <DropdownMenuItem>
                <User class="mr-2 h-4 w-4" />
                Hồ sơ cá nhân
              </DropdownMenuItem>
            </RouterLink>
            <template v-if="isAdmin">
              <RouterLink to="/admin">
                <DropdownMenuItem>
                  <LayoutDashboard class="h-4 w-4" />
                  Quản trị
                </DropdownMenuItem>
              </RouterLink>
            </template>
            <hr class="my-2 border-border" />

            <div class="text-destructive">
              <DropdownMenuItem @click.prevent="handleLogout">
                <LogOut class="mr-2 h-4 w-4" />
                Đăng xuất
              </DropdownMenuItem>
            </div>
          </DropdownMenuContent>
        </DropdownMenu>

        <!-- Login -->
        <RouterLink v-else to="/login" class="rounded-full p-2 hover:bg-accent">
          <User size="20" />
        </RouterLink>

        <!-- Cart -->
        <RouterLink to="/cart">
          <button
            aria-label="Giỏ hàng"
            class="relative rounded-full p-2 hover:bg-accent cursor-pointer"
          >
            <ShoppingBag size="20" />

            <span
              class="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-ink px-1 text-[10px] font-medium text-ivory"
            >
              {{ items?.length }}
            </span>
          </button>
        </RouterLink>
      </div>
    </div>
  </header>
</template>

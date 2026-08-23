<script setup>
import { ref, useSlots } from 'vue'
import { RouterLink } from 'vue-router'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '@/stores/useAuthStore'
import { LogIn } from 'lucide-vue-next'

const props = defineProps({
  redirectTo: { type: String, default: '/login' },
  align: { type: String, default: 'right' },
  guestTitle: { type: String, default: 'Bạn chưa đăng nhập' },
  guestMessage: { type: String, default: 'Đăng nhập để sử dụng đầy đủ tính năng' },
  buttonClass: {
    type: String,
    default:
      'flex items-center gap-2 rounded-full px-2 py-1.5 text-sm transition-colors hover:bg-[color-mix(in_oklab,currentColor_10%,transparent)]',
  },
})

const emit = defineEmits(['click-authenticated'])

const authStore = useAuthStore()
const { isAuthenticated } = storeToRefs(authStore)

const slots = useSlots()
const hasDropdownContent = !!slots.dropdown

const open = ref(false)
let closeTimer = null

const handleMouseEnter = () => {
  if (isAuthenticated.value && !hasDropdownContent) return

  clearTimeout(closeTimer)
  open.value = true
}

const handleMouseLeave = () => {
  closeTimer = setTimeout(() => {
    open.value = false
  }, 1)
}

const handleButtonClick = () => {
  if (isAuthenticated.value) {
    emit('click-authenticated')
  }
}
</script>

<template>
  <div class="relative inline-block" @mouseenter="handleMouseEnter" @mouseleave="handleMouseLeave">
    <button @click="handleButtonClick" :class="buttonClass">
      <slot name="icon" />
    </button>

    <Transition name="dropdown">
      <div
        v-if="open"
        class="absolute top-full z-50 pt-2"
        :class="align === 'right' ? 'right-0' : 'left-0'"
      >
        <div class="min-w-[220px] border border-border bg-background shadow-lg">
          <div v-if="isAuthenticated" @click="open = false">
            <slot name="dropdown" />
          </div>

          <div v-else class="p-4">
            <p class="font-medium">{{ guestTitle }}</p>
            <p class="mt-1 text-xs text-muted-foreground">{{ guestMessage }}</p>
            <RouterLink
              :to="redirectTo"
              class="mt-3 flex items-center justify-center gap-2 bg-ink py-2 text-xs uppercase tracking-widest text-ivory transition-colors hover:bg-ink/90"
            >
              <LogIn class="h-3.5 w-3.5" />
              Đăng nhập ngay
            </RouterLink>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.15s ease;
}
.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}
</style>

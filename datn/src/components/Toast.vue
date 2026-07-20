<script setup>
import { computed } from 'vue'
import { storeToRefs } from 'pinia'
import { CheckCircle2, XCircle, AlertTriangle, Info, X } from 'lucide-vue-next'
import { useNotificationStore } from '@/stores/useNotificationStore'

const notificationStore = useNotificationStore()
const { show, message, type } = storeToRefs(notificationStore)

const config = computed(() => {
  const map = {
    success: {
      icon: CheckCircle2,
      bg: 'bg-ink',
      text: 'text-ivory',
      iconColor: 'text-gold',
    },
    error: {
      icon: XCircle,
      bg: 'bg-destructive',
      text: 'text-destructive-foreground',
      iconColor: 'text-ivory',
    },
    warning: {
      icon: AlertTriangle,
      bg: 'bg-background border border-border',
      text: 'text-foreground',
      iconColor: 'text-gold',
    },
    info: {
      icon: Info,
      bg: 'bg-background border border-border',
      text: 'text-foreground',
      iconColor: 'text-foreground',
    },
  }
  return map[type.value] ?? map.info
})

const close = () => {
  show.value = false
}
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition ease-out duration-300"
      enter-from-class="opacity-0 translate-y-2"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-200"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 translate-y-2"
    >
      <div
        v-if="show"
        class="fixed top-30 right-6 z-[100] flex min-w-[280px] max-w-sm items-center gap-3 px-4 py-3 shadow-lg"
        :class="[config.bg, config.text]"
      >
        <component :is="config.icon" class="h-5 w-5 shrink-0" :class="config.iconColor" />

        <p class="flex-1 text-sm">{{ message }}</p>

        <button
          @click="close"
          class="shrink-0 opacity-70 transition-opacity hover:opacity-100 cursor-pointer"
        >
          <X class="h-4 w-4" />
        </button>
      </div>
    </Transition>
  </Teleport>
</template>

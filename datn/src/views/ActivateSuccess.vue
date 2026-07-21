<template>
  <div class="flex min-h-screen items-center justify-center bg-secondary/30">
    <div class="w-full max-w-[480px] border border-border bg-background p-10 text-center shadow-lg">
      <!-- Content -->
      <h4 class="font-display text-xl font-bold text-foreground mb-2">Kích hoạt thành công!</h4>
      <p class="mb-6 text-sm text-muted-foreground">
        Tài khoản của bạn đã được kích hoạt. <br />
        Bạn có thể đăng nhập ngay bây giờ.
      </p>

      <!-- Countdown -->
      <div class="mb-6">
        <div class="relative inline-block">
          <svg width="64" height="64">
            <circle cx="32" cy="32" r="28" fill="none" stroke="#e5e0d8" stroke-width="4" />
            <circle
              cx="32"
              cy="32"
              r="28"
              fill="none"
              stroke="#c9a961"
              stroke-width="4"
              stroke-linecap="round"
              stroke-dasharray="175.9"
              :stroke-dashoffset="dashOffset"
              transform="rotate(-90 32 32)"
              style="transition: stroke-dashoffset 1s linear"
            />
          </svg>
          <span
            class="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 font-display text-lg font-bold text-foreground"
          >
            {{ countdown }}
          </span>
        </div>
        <p class="mt-3 text-xs text-muted-foreground">
          Tự động chuyển hướng sau {{ countdown }} giây
        </p>
      </div>

      <!-- Button -->
      <RouterLink
        to="/login"
        class="inline-flex items-center justify-center rounded-full bg-ink px-8 py-3 text-sm font-medium text-ivory transition-colors hover:bg-ink/90"
      >
        Đăng nhập ngay
      </RouterLink>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const countdown = ref(5)
const totalSeconds = 5

const dashOffset = computed(() => {
  const circumference = 2 * Math.PI * 28 // 175.9
  return circumference * (1 - countdown.value / totalSeconds)
})

onMounted(() => {
  const interval = setInterval(() => {
    countdown.value--
    if (countdown.value === 0) {
      clearInterval(interval)
      router.push('/login')
    }
  }, 1000)
})
</script>

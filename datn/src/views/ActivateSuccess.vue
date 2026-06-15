<template>
  <div class="min-vh-100 d-flex align-items-center justify-content-center bg-light">
    <div class="card shadow-lg border-0 text-center p-5" style="max-width: 480px; width: 100%">
      <!-- Content -->
      <h4 class="fw-bold text-dark mb-2">Kích hoạt thành công!</h4>
      <p class="text-muted mb-4">
        Tài khoản của bạn đã được kích hoạt. <br />
        Bạn có thể đăng nhập ngay bây giờ.
      </p>

      <!-- Countdown -->
      <div class="mb-4">
        <div class="position-relative d-inline-block">
          <svg width="64" height="64" class="text-success">
            <circle cx="32" cy="32" r="28" fill="none" stroke="#e9ecef" stroke-width="4" />
            <circle
              cx="32"
              cy="32"
              r="28"
              fill="none"
              stroke="#198754"
              stroke-width="4"
              stroke-linecap="round"
              stroke-dasharray="175.9"
              :stroke-dashoffset="dashOffset"
              transform="rotate(-90 32 32)"
              style="transition: stroke-dashoffset 1s linear"
            />
          </svg>
          <span class="position-absolute top-50 start-50 translate-middle fw-bold text-dark">
            {{ countdown }}
          </span>
        </div>
        <p class="text-muted small mt-2">Tự động chuyển hướng sau {{ countdown }} giây</p>
      </div>

      <!-- Button -->
      <a href="/login" class="btn btn-success px-5 rounded-pill"> Đăng nhập ngay </a>
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

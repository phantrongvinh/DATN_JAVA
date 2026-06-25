<template>
  <!-- Content -->
  <section>
    <!-- Profile Card -->
    <div class="border border-border p-8">
      <h2 class="font-display text-2xl">Thông tin cá nhân</h2>

      <div class="mt-6 grid gap-6 md:grid-cols-2">
        <Info label="Họ và tên" :value="user.fullName || 'Chưa cập nhật'" />

        <Info label="Email" :value="user.email" />

        <Info label="Số điện thoại" :value="user.phone || 'Chưa cập nhật'" />

        <Info label="Ngày sinh" :value="user.birthDay || 'Chưa cập nhật'" />
      </div>

      <button
        class="mt-8 border border-foreground px-6 py-2.5 text-xs uppercase tracking-widest transition-colors hover:bg-foreground hover:text-background"
      >
        Chỉnh sửa
      </button>
    </div>

    <!-- Stats -->
    <div class="mt-6 grid gap-4 md:grid-cols-3">
      <div v-for="stat in stats" :key="stat.label" class="border border-border p-6">
        <p class="text-[11px] uppercase tracking-widest text-muted-foreground">
          {{ stat.label }}
        </p>

        <p class="mt-2 font-display text-3xl">
          {{ stat.value }}
        </p>
      </div>
    </div>
  </section>
</template>

<script setup>
import Info from '@/components/site/Info.vue'
import { useAuthStore } from '@/stores/useAuthStore'
import { storeToRefs } from 'pinia'
import { watch } from 'vue'
import { useRouter } from 'vue-router'

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

const stats = [
  {
    label: 'Đơn hàng',
    value: '0',
  },
  {
    label: 'Điểm tích lũy',
    value: '120',
  },
  {
    label: 'Hạng thành viên',
    value: 'Silver',
  },
]
</script>

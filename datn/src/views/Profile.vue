<script setup>
import { ref, computed, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/useAuthStore'
import { useNotificationStore } from '@/stores/useNotificationStore'
import Info from '@/components/site/Info.vue'
import ulti from '@/ulti/ulti'
import EditProfileModal from '@/components/EditProfileModal.vue'

const router = useRouter()
const authStore = useAuthStore()
const notification = useNotificationStore()

const { user } = storeToRefs(authStore)

watch(
  user,
  (newUser) => {
    if (!newUser) router.push('/login')
  },
  { immediate: true },
)

const showEditModal = ref(false)

const handleSave = async (payload) => {
  try {
    await authStore.updateProfile(payload)
    notification.notify('Cập nhật hồ sơ thành công', 'success')
    showEditModal.value = false
  } catch (err) {
    notification.notify(err.response?.data?.message ?? 'Có lỗi xảy ra', 'error')
  }
}

const stats = computed(() => [
  { label: 'Đơn hàng', value: user.value?.totalOrders ?? 0 },
  { label: 'Điểm tích lũy', value: user.value?.loyaltyPoints ?? 0 },
  { label: 'Hạng thành viên', value: user.value?.memberTier ?? 'Đồng' },
])

const nextTierMessage = computed(() => {
  if (!user.value?.nextTierName || !user.value?.amountToNextTier) return null
  return `Cần thêm ${ulti.formatVND(user.value.amountToNextTier)} để lên hạng ${user.value.nextTierName}`
})
</script>

<template>
  <section v-if="user">
    <div class="border border-border p-8">
      <h2 class="font-display text-2xl">Thông tin cá nhân</h2>

      <div class="mt-6 grid gap-6 md:grid-cols-2">
        <Info label="Họ và tên" :value="user.fullName || 'Chưa cập nhật'" />
        <Info label="Email" :value="user.email" />
        <Info label="Số điện thoại" :value="user.phone || 'Chưa cập nhật'" />
        <Info
          label="Ngày sinh"
          :value="user.birthDay ? ulti.formatBirthDay(user.birthDay) : 'Chưa cập nhật'"
        />
      </div>

      <button
        @click="showEditModal = true"
        class="mt-8 border border-foreground px-6 py-2.5 text-xs uppercase tracking-widest transition-colors hover:bg-foreground hover:text-background"
      >
        Chỉnh sửa
      </button>
    </div>

    <div class="mt-6 grid gap-4 md:grid-cols-3">
      <div v-for="stat in stats" :key="stat.label" class="border border-border p-6">
        <p class="text-[11px] uppercase tracking-widest text-muted-foreground">{{ stat.label }}</p>
        <p class="mt-2 font-display text-3xl">{{ stat.value }}</p>

        <p
          v-if="stat.label === 'Hạng thành viên' && nextTierMessage"
          class="mt-2 text-xs text-gold"
        >
          {{ nextTierMessage }}
        </p>
        <p
          v-else-if="stat.label === 'Hạng thành viên' && !nextTierMessage"
          class="mt-2 text-xs text-muted-foreground"
        >
          Đã đạt hạng cao nhất
        </p>
      </div>
    </div>
  </section>

  <EditProfileModal v-model:show="showEditModal" :user="user" @save="handleSave" />
</template>

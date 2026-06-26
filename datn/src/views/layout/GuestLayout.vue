<script setup>
import { useNotificationStore } from '@/stores/useNotificationStore'
import { computed } from 'vue'
import { RouterView } from 'vue-router'
import Header from './Header.vue'
import Footer from './Footer.vue'

const notification = useNotificationStore()

const icon = computed(() => {
  switch (notification.type) {
    case 'success':
      return 'check_circle'
    case 'error':
      return 'error'
    case 'warning':
      return 'warning'
    default:
      return 'info'
  }
})

const iconClass = computed(() => {
  switch (notification.type) {
    case 'success':
      return 'text-success'
    case 'error':
      return 'text-danger'
    case 'warning':
      return 'text-warning'
    default:
      return 'text-info'
  }
})
</script>

<template>
  <Transition name="toast">
    <div v-if="notification.show" class="toast-notification bg-white shadow rounded-3">
      <div class="d-flex align-items-center px-3 py-2">
        <i class="material-symbols-rounded me-2" :class="iconClass">
          {{ icon }}
        </i>

        <div class="flex-grow-1">
          <div class="fw-bold">Notification</div>
          <small class="text-muted">
            {{ notification.message }}
          </small>
        </div>

        <button class="btn-close ms-3" @click="notification.show = false"></button>
      </div>

      <hr class="m-0" />

      <div class="toast-progress" :class="notification.type"></div>
    </div>
  </Transition>
  <div class="flex min-h-screen flex-col">
    <Header></Header>

    <div class="flex-1">
      <RouterView />
    </div>

    <Footer></Footer>
  </div>
</template>

<style scoped>
.toast-notification {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 340px;
  z-index: 9999;
  overflow: hidden;
}

.toast-progress {
  height: 4px;
}

.toast-progress.success {
  background: #4caf50;
}

.toast-progress.error {
  background: #f44335;
}

.toast-progress.warning {
  background: #fb8c00;
}

.toast-progress.info {
  background: #1a73e8;
}

/* Animation */
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s ease;
}

.toast-enter-from,
.toast-leave-to {
  transform: translateX(100%);
  opacity: 0;
}
</style>

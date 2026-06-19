<script setup>
import { RouterView } from 'vue-router'
import Header from './views/layout/Header.vue'
import Footer from './views/layout/Footer.vue'
import { useNotificationStore } from './stores/useNotificationStore.js'

const notification = useNotificationStore()
</script>

<template>
  <div
    v-if="notification.show"
    class="toast-notification shadow-lg rounded overflow-hidden border fst-italic d-flex flex-column align-items-center"
  >
    <div class="m-3">
      {{ notification.message }}
    </div>
    <div class="py-2 w-100 m-0" :class="notification.type"></div>
  </div>
  <div class="d-flex flex-column">
    <Header></Header>

    <div id="main" class="flex-grow-1">
      <RouterView />
    </div>

    <Footer></Footer>
  </div>
</template>

<style scoped></style>

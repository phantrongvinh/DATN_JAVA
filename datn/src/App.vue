<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Header from './views/layout/Header.vue'
import Footer from './views/layout/Footer.vue'
import { useAuthStore } from './stores/useAuthStore'
import { onMounted } from 'vue'
import { storeToRefs } from 'pinia'
</script>

<template>
  <Header></Header>

  <div id="main">
    <RouterView />
  </div>

  <Footer></Footer>
</template>

<style scoped>
#main {
  min-height: 800px;
}
</style>

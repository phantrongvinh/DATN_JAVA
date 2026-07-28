<script setup>
import { RouterView } from 'vue-router'
import Header from './Header.vue'
import Footer from './Footer.vue'
</script>

<template>
  <div class="flex min-h-screen flex-col">
    <Header></Header>

    <div class="flex-1">
      <RouterView />
    </div>

    <Footer></Footer>
  </div>
</template>

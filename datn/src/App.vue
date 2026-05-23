<script setup>
import { RouterLink, RouterView } from 'vue-router'
import Header from './views/layout/Header.vue'
import Footer from './views/layout/Footer.vue'
import { useGenderStore } from './stores/useGenderStore'
import { onMounted } from 'vue'

const genderStore = useGenderStore()

onMounted(async () => {
  await genderStore.getAllGender()
})
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

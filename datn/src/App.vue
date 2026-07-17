<script setup>
import { RouterView } from 'vue-router'
import Toast from './components/Toast.vue'
import { useSiteSettingsStore } from './stores/useSiteSettingsStore.js'
import { onMounted } from 'vue'

const siteSettingsStore = useSiteSettingsStore()

onMounted(async () => {
  await siteSettingsStore.fetchFromServer()
})
</script>

<template>
  <RouterView />
  <Toast />
</template>

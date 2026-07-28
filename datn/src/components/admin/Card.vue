<script setup>
defineProps({
  title: { type: String, required: true },
})
</script>

<template>
  <div class="border border-border bg-background p-6">
    <h3 class="mb-4 font-display text-lg">{{ title }}</h3>
    <div><slot /></div>
  </div>
</template>

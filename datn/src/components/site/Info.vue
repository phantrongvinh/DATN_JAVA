<template>
  <div>
    <p class="text-[11px] uppercase tracking-widest text-muted-foreground">{{ label }}</p>
    <p class="mt-1.5 text-sm">{{ value }}</p>
  </div>
</template>

<script setup>
const props = defineProps({
  label: String,
  value: String,
})
</script>

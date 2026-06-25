<script setup>
defineProps({
  title: String,
})
</script>

<template>
  <div>
    <h3 class="mb-3 text-[11px] font-semibold uppercase tracking-[0.2em]">
      {{ title }}
    </h3>

    <div class="space-y-2">
      <slot></slot>
    </div>
  </div>
</template>

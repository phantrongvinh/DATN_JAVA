<script setup>
defineProps({
  label: String,
  value: String,
  bold: Boolean,
})
</script>

<template>
  <div class="flex justify-between" :class="bold ? 'font-display text-lg' : ''">
    <dt :class="bold ? '' : 'text-muted-foreground'">
      {{ label }}
    </dt>

    <dd>
      {{ value }}
    </dd>
  </div>
</template>

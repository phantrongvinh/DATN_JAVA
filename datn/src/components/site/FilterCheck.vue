<script setup>
defineProps({
  label: String,
  checked: Boolean,
  value: Number,
})

const emit = defineEmits(['change'])
</script>

<template>
  <label
    class="flex cursor-pointer items-center gap-2.5 text-sm transition-colors hover:text-foreground"
  >
    <span
      class="flex h-4 w-4 items-center justify-center border"
      :class="checked ? 'border-foreground bg-foreground' : 'border-border'"
    >
      <span v-if="checked" class="h-1.5 w-1.5 bg-background"></span>
    </span>

    <input class="sr-only" type="checkbox" :checked="checked" @change="emit('change', value)" />

    <span :class="checked ? 'text-foreground' : 'text-muted-foreground'">
      {{ label }}
    </span>
  </label>
</template>

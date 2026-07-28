<script setup>
const props = defineProps({
  label: String,
  type: {
    type: String,
    default: 'text',
  },
  modelValue: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:modelValue', 'focus'])
</script>

<template>
  <label class="block">
    <span class="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">
      {{ label }}
    </span>

    <input
      :type="type"
      :value="modelValue"
      @input="emit('update:modelValue', $event.target.value)"
      @focus="emit('focus')"
      class="mt-1 w-full border-b border-border bg-transparent py-2.5 text-sm outline-none transition-colors focus:border-foreground"
    />
  </label>
</template>

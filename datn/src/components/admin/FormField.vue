<script setup>
defineProps({
  label: { type: String, required: true },
  multiline: { type: Boolean, default: false },
})
const model = defineModel({ type: String, default: '' })
</script>

<template>
  <label class="block">
    <span class="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">{{
      label
    }}</span>

    <textarea
      v-if="multiline"
      rows="3"
      v-model="model"
      class="mt-1 w-full border border-border bg-transparent px-3 py-2 text-sm outline-none focus:border-foreground"
    />
    <input
      v-else
      v-model="model"
      class="mt-1 w-full border border-border bg-transparent px-3 py-2 text-sm outline-none focus:border-foreground"
    />
  </label>
</template>

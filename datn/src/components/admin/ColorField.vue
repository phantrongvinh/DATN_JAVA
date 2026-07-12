<script setup>
defineProps({
  label: { type: String, required: true },
})
const model = defineModel({ type: String, default: '' })
</script>

<template>
  <label class="block">
    <span class="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground">{{
      label
    }}</span>

    <div class="mt-1 flex items-center gap-2">
      <input
        type="color"
        v-model="model"
        class="h-10 w-14 shrink-0 cursor-pointer border border-border"
      />
      <input
        type="text"
        v-model="model"
        class="w-full border border-border bg-transparent px-3 py-2 font-mono text-xs outline-none focus:border-foreground"
      />
    </div>
  </label>
</template>

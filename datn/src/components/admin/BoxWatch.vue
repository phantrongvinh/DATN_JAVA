<script setup>
import {
  Package,
  ShoppingCart,
  Users,
  DollarSign,
  ArrowDownRight,
  ArrowUpRight,
} from 'lucide-vue-next'

const props = defineProps({
  data: Array,
})
</script>

<template>
  <div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
    <div v-for="s in props.data" :key="s.label" class="border border-border bg-background p-5">
      <div class="flex items-center justify-between">
        <p class="text-[11px] uppercase tracking-widest text-muted-foreground">
          {{ s.label }}
        </p>

        <component :is="s.icon" class="h-4 w-4 text-gold" :stroke-width="1.5" />
      </div>

      <p class="mt-3 font-display text-3xl">
        {{ s.value }}
      </p>

      <p
        class="mt-1 flex items-center gap-1 text-xs"
        :class="s.positive ? 'text-emerald-700' : 'text-red-600'"
      >
        <component :is="s.positive ? ArrowUpRight : ArrowDownRight" class="h-3 w-3" />

        {{ Math.abs(s.trend) }}% so với tháng trước
      </p>
    </div>
  </div>
</template>

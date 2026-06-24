<script setup>
import { ArrowRight } from 'lucide-vue-next'
import { useRouter } from 'vue-router'

const props = defineProps({
  eyebrow: String,
  title: String,
  link: Object,
})

const router = useRouter()

const go = () => {
  if (!props.link) return
  router.push({
    path: props.link.to,
    query: props.link.query || {},
  })
}
</script>

<template>
  <div class="flex items-end justify-between gap-4">
    <div>
      <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">
        {{ eyebrow }}
      </p>
      <h2 class="mt-2 font-display text-3xl md:text-4xl">
        {{ title }}
      </h2>
    </div>

    <button
      v-if="link"
      @click="go"
      class="hidden md:inline-flex items-center gap-1 border-b border-foreground pb-1 text-xs uppercase tracking-widest"
    >
      {{ link.label }}
      <ArrowRight class="h-3 w-3" />
    </button>
  </div>
</template>

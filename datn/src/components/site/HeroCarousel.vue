<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { RouterLink } from 'vue-router'
import { ArrowRight, Sparkles } from 'lucide-vue-next'

const props = defineProps({
  slides: { type: Array, required: true },
  autoplayMs: { type: Number, default: 10000 },
})

const current = ref(0)
let intervalId = null

const activeSlide = computed(() => props.slides[current.value])

const goTo = (idx) => {
  current.value = idx
  startAutoplay()
}

const startAutoplay = () => {
  if (intervalId) clearInterval(intervalId)
  if (props.slides.length <= 1) return
  intervalId = setInterval(() => {
    current.value = (current.value + 1) % props.slides.length
  }, props.autoplayMs)
}

onMounted(startAutoplay)
onUnmounted(() => {
  if (intervalId) clearInterval(intervalId)
})
</script>

<template>
  <section
    class="relative h-[70vh] min-h-[480px] w-full overflow-hidden border-b border-border md:h-[85vh]"
  >
    <Transition name="fade" mode="out-in">
      <div :key="activeSlide?.id" class="absolute inset-0">
        <!-- Full-bleed background image -->
        <img
          :src="activeSlide?.image"
          :alt="activeSlide?.title"
          class="h-full w-full object-cover"
        />

        <!-- Overlay gradient để chữ dễ đọc -->
        <div class="absolute inset-0 bg-gradient-to-t from-black/70 via-black/20 to-black/10" />

        <!-- Text nổi trên ảnh -->
        <div class="container-x absolute inset-0 flex items-end pb-16 md:items-center md:pb-0">
          <div class="max-w-lg space-y-6 text-ivory">
            <p class="flex items-center gap-2 text-xs uppercase tracking-[0.25em] text-ivory/80">
              <Sparkles class="h-3 w-3 text-gold" />
              {{ activeSlide?.eyebrow }}
            </p>

            <h1 class="font-display text-5xl leading-[1.05] md:text-7xl">
              {{ activeSlide?.title }} <br />
              <span class="italic text-gold">{{ activeSlide?.titleItalic }}</span>
            </h1>

            <p class="max-w-md text-base text-ivory/80">
              {{ activeSlide?.description }}
            </p>

            <div class="flex flex-wrap items-center gap-3 pt-2">
              <RouterLink :to="activeSlide?.ctaTo ?? '/products'">
                <button
                  class="group inline-flex items-center gap-2 bg-ivory px-7 py-3.5 text-sm font-medium tracking-wide text-ink transition-colors hover:bg-ivory/85"
                >
                  {{ activeSlide?.ctaLabel ?? 'Khám phá' }}
                  <ArrowRight class="h-4 w-4 transition-transform group-hover:translate-x-1" />
                </button>
              </RouterLink>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <!-- Dots -->
    <div
      v-if="slides.length > 1"
      class="absolute bottom-6 left-1/2 z-10 flex -translate-x-1/2 gap-2"
    >
      <button
        v-for="(s, idx) in slides"
        :key="s.id"
        @click="goTo(idx)"
        class="h-1.5 rounded-full transition-all"
        :class="idx === current ? 'w-6 bg-gold' : 'w-1.5 bg-ivory/50'"
        :aria-label="`Slide ${idx + 1}`"
      />
    </div>
  </section>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

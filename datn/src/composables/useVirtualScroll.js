import { ref, computed, onMounted, onUnmounted } from 'vue'

export function useVirtualScroll(items, options = {}) {
  const { itemHeight = 50, containerHeight = 500, overscan = 3 } = options

  const scrollTop = ref(0)
  const containerRef = ref(null)

  const visibleCount = computed(() => Math.ceil(containerHeight / itemHeight))

  const startIndex = computed(() =>
    Math.max(0, Math.floor(scrollTop.value / itemHeight) - overscan),
  )

  const endIndex = computed(() =>
    Math.min(items.value.length, startIndex.value + visibleCount.value + overscan * 2),
  )

  const visibleItems = computed(() =>
    items.value.slice(startIndex.value, endIndex.value).map((item, i) => ({
      ...item,
      _index: startIndex.value + i,
    })),
  )

  const totalHeight = computed(() => items.value.length * itemHeight)

  const offsetY = computed(() => startIndex.value * itemHeight)

  const onScroll = (e) => {
    scrollTop.value = e.target.scrollTop
  }

  onMounted(() => {
    containerRef.value?.addEventListener('scroll', onScroll)
  })

  onUnmounted(() => {
    containerRef.value?.removeEventListener('scroll', onScroll)
  })

  return {
    containerRef,
    visibleItems,
    totalHeight,
    offsetY,
  }
}

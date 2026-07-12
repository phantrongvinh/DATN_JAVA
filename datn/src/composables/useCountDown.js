// composables/useCountdown.js
import { ref, computed, onMounted, onUnmounted } from 'vue'

function parseTimeOn(timeStr, baseDate) {
  const [hh, mm] = timeStr.split(':').map(Number)
  const d = new Date(baseDate)
  d.setHours(hh, mm, 0, 0)
  return d
}

export function useFlashSaleCountdown(promotionsRef) {
  const now = ref(new Date())
  let intervalId = null

  onMounted(() => {
    intervalId = setInterval(() => {
      now.value = new Date()
    }, 1000)
  })

  onUnmounted(() => {
    if (intervalId) clearInterval(intervalId)
  })

  const result = computed(() => {
    const promos = promotionsRef.value ?? []
    if (!promos.length) {
      return { status: 'none', promo: null, h: 0, m: 0, s: 0 }
    }

    const nowVal = now.value

    // gắn thêm start/end (Date hôm nay) cho từng promo để so sánh
    const enriched = promos
      .filter((p) => p.startTime && p.endTime)
      .map((p) => ({
        promo: p,
        start: parseTimeOn(p.startTime, nowVal),
        end: parseTimeOn(p.endTime, nowVal),
      }))
      .sort((a, b) => a.start - b.start)

    // 1. Tìm promo đang active
    const activeItem = enriched.find((x) => nowVal >= x.start && nowVal < x.end)
    if (activeItem) {
      const diff = activeItem.end.getTime() - nowVal.getTime()
      return {
        status: 'active',
        promo: activeItem.promo,
        ...toHMS(diff),
      }
    }

    // 2. Tìm promo sắp tới gần nhất hôm nay
    const upcomingItem = enriched.find((x) => x.start > nowVal)
    if (upcomingItem) {
      const diff = upcomingItem.start.getTime() - nowVal.getTime()
      return {
        status: 'upcoming',
        promo: upcomingItem.promo,
        ...toHMS(diff),
      }
    }

    // 3. Tất cả đã kết thúc hôm nay -> đếm ngược tới promo sớm nhất ngày mai
    if (enriched.length) {
      const first = enriched[0]
      const tomorrowStart = new Date(first.start)
      tomorrowStart.setDate(tomorrowStart.getDate() + 1)
      const diff = tomorrowStart.getTime() - nowVal.getTime()
      return {
        status: 'ended-today',
        promo: first.promo,
        ...toHMS(diff),
      }
    }

    return { status: 'none', promo: null, h: 0, m: 0, s: 0 }
  })

  return result
}

function toHMS(diffMs) {
  const diff = Math.max(0, diffMs)
  const h = Math.floor(diff / 3_600_000)
  const m = Math.floor((diff % 3_600_000) / 60_000)
  const s = Math.floor((diff % 60_000) / 1000)
  return { h, m, s }
}

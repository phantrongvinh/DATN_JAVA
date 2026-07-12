import { ref, watch } from 'vue'
import { defineStore } from 'pinia'

const STORAGE_KEY = 'site-settings'

const DEFAULT_THEME = {
  headerBg: '#0a0a0a',
  headerText: '#f5f5f0',
  headerAccent: '#c9a961',
  footerBg: '#0a0a0a',
  footerText: '#f5f5f0',
  announcementBg: '#c9a961',
  announcementText: '#0a0a0a',
}

export const SEASONAL_PRESETS = [
  {
    id: 'classic',
    name: 'Cổ điển',
    emoji: '⚽',
    primaryColor: '#c9a961',
    colors: { ...DEFAULT_THEME },
  },
  {
    id: 'summer',
    name: 'Hè sôi động',
    emoji: '☀️',
    primaryColor: '#ff6b35',
    colors: {
      headerBg: '#0a2540',
      headerText: '#ffffff',
      headerAccent: '#ff6b35',
      footerBg: '#0a2540',
      footerText: '#ffffff',
      announcementBg: '#ff6b35',
      announcementText: '#ffffff',
    },
  },
  {
    id: 'worldcup',
    name: 'World Cup',
    emoji: '🏆',
    primaryColor: '#ffd700',
    colors: {
      headerBg: '#0b3d2e',
      headerText: '#ffffff',
      headerAccent: '#ffd700',
      footerBg: '#0b3d2e',
      footerText: '#ffffff',
      announcementBg: '#ffd700',
      announcementText: '#0b3d2e',
    },
  },
]

const DEFAULT_SLIDES = [
  {
    id: 's-1',
    eyebrow: 'Bộ sưu tập Hè 2026',
    title: 'Bóng đá',
    titleItalic: 'tinh tuyển.',
    description: 'Mỗi sản phẩm là sự kết hợp giữa công nghệ đỉnh cao và thiết kế tinh tế.',
    image: '/images/hero.png',
    ctaLabel: 'Khám phá bộ sưu tập',
    ctaTo: '/products',
  },
]

const DEFAULT_LANDING = {
  seasonalPresetId: 'classic',
  primaryColor: '#c9a961',
  theme: { ...DEFAULT_THEME },
  announcement: '',
  slides: DEFAULT_SLIDES,
  slideAutoplayMs: 10000,
}

function loadLanding() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

function saveLanding(value) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(value))
  } catch {}
}

export const useSiteSettingsStore = defineStore('siteSettings', () => {
  const landing = ref(loadLanding() ?? { ...DEFAULT_LANDING })
  const posts = ref([])

  watch(landing, (value) => saveLanding(value), { deep: true })

  window.addEventListener('storage', (e) => {
    if (e.key === STORAGE_KEY && e.newValue) {
      try {
        landing.value = JSON.parse(e.newValue)
      } catch {}
    }
  })

  function updateLanding(next) {
    landing.value = { ...next }
  }

  function resetLanding() {
    landing.value = { ...DEFAULT_LANDING }
  }

  function applySeasonalPreset(id) {
    const preset = SEASONAL_PRESETS.find((p) => p.id === id)
    if (!preset) return
    landing.value = {
      ...landing.value,
      seasonalPresetId: preset.id,
      primaryColor: preset.primaryColor,
      theme: { ...preset.colors },
    }
  }

  function upsertSlide(slide) {
    const slides = [...landing.value.slides]
    const idx = slides.findIndex((s) => s.id === slide.id)
    if (idx >= 0) slides[idx] = slide
    else slides.push(slide)
    landing.value = { ...landing.value, slides }
  }

  function removeSlide(id) {
    landing.value = {
      ...landing.value,
      slides: landing.value.slides.filter((s) => s.id !== id),
    }
  }

  function moveSlide(id, direction) {
    const slides = [...landing.value.slides]
    const idx = slides.findIndex((s) => s.id === id)
    if (idx < 0) return
    const target = idx + direction
    if (target < 0 || target >= slides.length) return
    ;[slides[idx], slides[target]] = [slides[target], slides[idx]]
    landing.value = { ...landing.value, slides }
  }

  return {
    landing,
    posts,
    updateLanding,
    resetLanding,
    applySeasonalPreset,
    upsertSlide,
    removeSlide,
    moveSlide,
  }
})

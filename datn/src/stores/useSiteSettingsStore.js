import { ref, toRaw, unref, watch } from 'vue'
import { defineStore } from 'pinia'
import axiosClient from '@/api/axiosClient'

const STORAGE_KEY = 'site-settings'

const DEFAULT_THEME = {
  headerBg: '#0a0a0a',
  headerText: '#f5f5f0',
  subHeaderText: '#0a0a0a',
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
    id: 'worldcup',
    name: 'World Cup',
    emoji: '🏆',
    primaryColor: '#c9a34e',
    colors: {
      headerBg: '#0b1d3a',
      headerText: '#f5f0e6',
      subHeaderText: '#0b1d3a',
      headerAccent: '#c9a34e',
      footerBg: '#0b1d3a',
      footerText: '#e8e2d3',
      announcementBg: '#c9a34e',
      announcementText: '#0b1d3a',
    },
  },
  {
    id: 'champions',
    name: 'Champions League',
    emoji: '⭐',
    primaryColor: '#7ac8ff',
    colors: {
      headerBg: '#020617',
      headerText: '#e6edff',
      subHeaderText: '#020617',
      headerAccent: '#7ac8ff',
      footerBg: '#020617',
      footerText: '#c7d2fe',
      announcementBg: '#1e293b',
      announcementText: '#7ac8ff',
    },
  },
  {
    id: 'summer',
    name: 'Hè sôi động',
    emoji: '🌞',
    primaryColor: '#f97316',
    colors: {
      headerBg: '#fff8ec',
      headerText: '#7c2d12',
      subHeaderText: '#f97316',
      headerAccent: '#c2410c',
      footerBg: '#7c2d12',
      footerText: '#fed7aa',
      announcementBg: '#f97316',
      announcementText: '#431407',
    },
  },
  {
    id: 'derby',
    name: 'Derby đỏ',
    emoji: '🔥',
    primaryColor: '#dc2626',
    colors: {
      headerBg: '#1a0505',
      headerText: '#fee2e2',
      subHeaderText: '#1a0505',
      headerAccent: '#f87171',
      footerBg: '#1a0505',
      footerText: '#fecaca',
      announcementBg: '#dc2626',
      announcementText: '#ffffff',
    },
  },
  {
    id: 'premier',
    name: 'Premier xanh',
    emoji: '💚',
    primaryColor: '#10b981',
    colors: {
      headerBg: '#022c22',
      headerText: '#d1fae5',
      subHeaderText: '#022c22',
      headerAccent: '#34d399',
      footerBg: '#022c22',
      footerText: '#a7f3d0',
      announcementBg: '#10b981',
      announcementText: '#022c22',
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
  const loading = ref(false)

  // ─── Sync localStorage khi landing thay đổi ──────
  watch(landing, (value) => saveLanding(value), { deep: true })

  window.addEventListener('storage', (e) => {
    if (e.key === STORAGE_KEY && e.newValue) {
      try {
        landing.value = JSON.parse(e.newValue)
      } catch {}
    }
  })

  // ─── Load từ server ───────────────────────────────
  async function fetchFromServer() {
    loading.value = true
    try {
      const res = await axiosClient.get('/site-setting')
      if (res.data && Object.keys(res.data).length > 0) {
        landing.value = {
          ...DEFAULT_LANDING,
          ...res.data,
          theme: {
            ...DEFAULT_THEME,
            ...(res.data.theme ?? {}),
          },
          slides: res.data.slides ?? DEFAULT_SLIDES,
        }
        saveLanding(landing.value)
      }
    } catch {
      console.warn('Không thể load settings từ server, dùng cache local')
    } finally {
      loading.value = false
    }
  }

  // ─── Save lên server ──────────────────────────────
  async function saveToServer(next) {
    loading.value = true
    try {
      const plainData = structuredClone(next)
      const res = await axiosClient.put('/admin/site-setting', plainData)
      landing.value = res.data
      saveLanding(res.data)
    } catch (err) {
      console.error('Lỗi save settings:', err)
    } finally {
      loading.value = false
    }
  }

  // ─── Actions (giữ nguyên logic, thêm sync server) ─
  async function updateLanding(next) {
    const plain = JSON.parse(JSON.stringify(next))

    landing.value = plain

    await saveToServer(plain)
  }

  function resetLanding() {
    landing.value = { ...DEFAULT_LANDING }
    saveToServer({ ...DEFAULT_LANDING })
  }

  function applySeasonalPreset(id) {
    const preset = SEASONAL_PRESETS.find((p) => p.id === id)
    if (!preset) return
    const next = {
      ...landing.value,
      seasonalPresetId: preset.id,
      primaryColor: preset.primaryColor,
      theme: { ...preset.colors },
    }
    landing.value = next
    saveToServer(next)
  }

  function upsertSlide(slide) {
    const slides = [...(landing.value.slides ?? [])]
    const idx = slides.findIndex((s) => s.id === slide.id)
    if (idx >= 0) slides[idx] = slide
    else slides.push(slide)
    const next = { ...landing.value, slides }
    landing.value = next
    saveToServer(next)
  }

  function removeSlide(id) {
    const next = {
      ...landing.value,
      slides: (landing.value.slides ?? []).filter((s) => s.id !== id),
    }
    landing.value = next
    saveToServer(next)
  }

  function moveSlide(id, direction) {
    const slides = [...(landing.value.slides ?? [])]
    const idx = slides.findIndex((s) => s.id === id)
    if (idx < 0) return
    const target = idx + direction
    if (target < 0 || target >= slides.length) return
    ;[slides[idx], slides[target]] = [slides[target], slides[idx]]
    const next = { ...landing.value, slides }
    landing.value = next
    saveToServer(next)
  }

  return {
    landing,
    posts,
    loading,
    fetchFromServer,
    updateLanding,
    resetLanding,
    applySeasonalPreset,
    upsertSlide,
    removeSlide,
    moveSlide,
    DEFAULT_LANDING,
    DEFAULT_SLIDES,
    DEFAULT_THEME,
  }
})

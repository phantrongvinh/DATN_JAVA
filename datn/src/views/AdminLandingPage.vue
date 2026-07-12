<script setup>
import { ref, reactive } from 'vue'
import { storeToRefs } from 'pinia'
import {
  RotateCcw,
  Save,
  Plus,
  Trash2,
  ArrowUp,
  ArrowDown,
  Image as ImageIcon,
} from 'lucide-vue-next'

import Card from '@/components/admin/Card.vue'
import FormField from '@/components/admin/FormField.vue'
import ColorField from '@/components/admin/ColorField.vue'
import SlideEditor from '@/components/admin/SlideEditor.vue'

import { useSiteSettingsStore, SEASONAL_PRESETS } from '@/stores/useSiteSettingsStore'
import { useNotificationStore } from '@/stores/useNotificationStore'

const siteSettingsStore = useSiteSettingsStore()
const notificationStore = useNotificationStore()
const { landing } = storeToRefs(siteSettingsStore)

// draft: bản nháp để user chỉnh trước khi bấm "Lưu"
const draft = reactive(JSON.parse(JSON.stringify(landing.value)))

const save = () => {
  siteSettingsStore.updateLanding({ ...draft })
  notificationStore.notify('Đã lưu cấu hình trang chủ', 'success')
}

const reset = () => {
  siteSettingsStore.resetLanding()
  Object.assign(draft, structuredClone(siteSettingsStore.landing))
  notificationStore.notify('Đã khôi phục mặc định', 'success')
}

const applyPreset = (preset) => {
  siteSettingsStore.applySeasonalPreset(preset.id)
  draft.seasonalPresetId = preset.id
  draft.primaryColor = preset.primaryColor
  draft.theme = { ...preset.colors }
  notificationStore.notify(`Áp dụng chủ đề: ${preset.name}`, 'success')
}

const editing = ref(null)

const openNewSlide = () => {
  editing.value = {
    id: `s-${Date.now()}`,
    eyebrow: 'Bộ sưu tập mới',
    title: 'Tiêu đề',
    titleItalic: 'in nghiêng.',
    description: 'Mô tả ngắn cho slide.',
    image:
      'https://images.unsplash.com/photo-1517649763962-0c623066013b?auto=format&fit=crop&w=1400&q=80',
    ctaLabel: 'Khám phá',
    ctaTo: '/products',
  }
}

const moveSlideDraft = (id, dir) => {
  siteSettingsStore.moveSlide(id, dir)
  const idx = draft.slides.findIndex((s) => s.id === id)
  const target = idx + dir
  if (idx < 0 || target < 0 || target >= draft.slides.length) return
  ;[draft.slides[idx], draft.slides[target]] = [draft.slides[target], draft.slides[idx]]
}

const removeSlideDraft = (id) => {
  siteSettingsStore.removeSlide(id)
  const i = draft.slides.findIndex((s) => s.id === id)
  if (i >= 0) draft.slides.splice(i, 1)
  notificationStore.notify('Đã xóa slide', 'success')
}

const saveSlide = (slide) => {
  siteSettingsStore.upsertSlide(slide)
  const idx = draft.slides.findIndex((s) => s.id === slide.id)
  if (idx >= 0) draft.slides[idx] = slide
  else draft.slides.push(slide)
  editing.value = null
  notificationStore.notify('Đã lưu slide', 'success')
}
</script>

<template>
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Quản lý trang chủ</h1>
    </header>
    <div class="space-y-6 p-6">
      <!-- SEASONAL THEMES -->
      <Card title="Chủ đề theo mùa bóng đá">
        <p class="mb-3 text-xs text-muted-foreground">
          Chọn một chủ đề có sẵn để đổi màu header, footer, banner thông báo và điểm nhấn.
        </p>

        <div class="grid grid-cols-2 gap-3 md:grid-cols-3">
          <button
            v-for="p in SEASONAL_PRESETS"
            :key="p.id"
            @click="applyPreset(p)"
            class="group flex items-center gap-3 border p-3 text-left transition-colors cursor-pointer"
            :class="
              draft.seasonalPresetId === p.id
                ? 'border-foreground'
                : 'border-border hover:bg-secondary'
            "
          >
            <div
              class="flex h-12 w-12 shrink-0 items-center justify-center text-2xl"
              :style="{ background: p.colors.headerBg, color: p.colors.headerAccent }"
            >
              {{ p.emoji }}
            </div>

            <div class="min-w-0 flex-1">
              <p class="truncate text-sm font-medium">{{ p.name }}</p>
              <div class="mt-1.5 flex gap-1">
                <span
                  v-for="(c, i) in [
                    p.colors.headerBg,
                    p.colors.headerAccent,
                    p.colors.footerBg,
                    p.primaryColor,
                  ]"
                  :key="i"
                  class="h-3 w-3 border border-border"
                  :style="{ background: c }"
                />
              </div>
            </div>
          </button>
        </div>
      </Card>

      <!-- CUSTOM COLORS -->
      <Card title="Tùy chỉnh màu sắc Header / Footer / Banner">
        <div class="grid gap-4 md:grid-cols-2">
          <ColorField label="Nền header" v-model="draft.theme.headerBg" />
          <ColorField label="Chữ header" v-model="draft.theme.headerText" />
          <ColorField label="Điểm nhấn header (logo, badge)" v-model="draft.theme.headerAccent" />
          <ColorField label="Màu chủ đạo (gold)" v-model="draft.primaryColor" />
          <ColorField label="Nền footer" v-model="draft.theme.footerBg" />
          <ColorField label="Chữ footer" v-model="draft.theme.footerText" />
          <ColorField label="Nền banner thông báo" v-model="draft.theme.announcementBg" />
          <ColorField label="Chữ banner thông báo" v-model="draft.theme.announcementText" />
        </div>

        <div class="mt-4">
          <FormField label="Thông báo (announcement bar)" v-model="draft.announcement" />
        </div>
      </Card>

      <!-- SLIDES -->
      <Card title="Slide banner trang chủ">
        <div class="mb-3 flex items-center justify-between">
          <p class="text-xs text-muted-foreground">
            Kéo dài slideshow: <b>{{ Math.round(draft.slideAutoplayMs / 1000) }}s</b> / slide
          </p>

          <div class="flex items-center gap-2">
            <input
              type="range"
              min="3000"
              max="10000"
              step="500"
              v-model.number="draft.slideAutoplayMs"
            />

            <button
              @click="openNewSlide"
              class="inline-flex items-center gap-1 border border-border px-3 py-2 text-xs uppercase tracking-widest hover:bg-secondary cursor-pointer"
            >
              <Plus class="h-3 w-3" /> Thêm slide
            </button>
          </div>
        </div>

        <div class="space-y-2">
          <div
            v-for="(s, idx) in draft.slides"
            :key="s.id"
            class="flex items-center gap-3 border border-border p-2"
          >
            <div class="h-16 w-24 shrink-0 overflow-hidden bg-secondary">
              <img
                v-if="s.image"
                :src="s.image"
                :alt="s.title"
                class="h-full w-full object-cover"
              />
              <ImageIcon v-else class="m-auto h-6 w-6 text-muted-foreground" />
            </div>

            <div class="min-w-0 flex-1">
              <p class="text-[10px] uppercase tracking-widest text-muted-foreground">
                {{ s.eyebrow }}
              </p>
              <p class="truncate text-sm font-medium">
                {{ s.title }} <span class="italic text-gold">{{ s.titleItalic }}</span>
              </p>
              <p class="truncate text-xs text-muted-foreground">{{ s.description }}</p>
            </div>

            <div class="flex items-center gap-1">
              <button
                @click="moveSlideDraft(s.id, -1)"
                :disabled="idx === 0"
                class="border border-border p-1.5 disabled:opacity-30 hover:bg-secondary cursor-pointer"
              >
                <ArrowUp class="h-3 w-3" />
              </button>
              <button
                @click="moveSlideDraft(s.id, 1)"
                :disabled="idx === draft.slides.length - 1"
                class="border border-border p-1.5 disabled:opacity-30 hover:bg-secondary cursor-pointer"
              >
                <ArrowDown class="h-3 w-3" />
              </button>
              <button
                @click="editing = { ...s }"
                class="border border-border px-3 py-1.5 text-xs hover:bg-secondary cursor-pointer"
              >
                Sửa
              </button>
              <button
                @click="removeSlideDraft(s.id)"
                class="border border-border p-1.5 text-destructive hover:bg-destructive/10 cursor-pointer"
              >
                <Trash2 class="h-3 w-3" />
              </button>
            </div>
          </div>

          <p
            v-if="draft.slides.length === 0"
            class="border border-dashed border-border px-4 py-8 text-center text-sm text-muted-foreground"
          >
            Chưa có slide nào. Nhấn "Thêm slide" để bắt đầu.
          </p>
        </div>
      </Card>

      <div class="flex gap-2">
        <button
          @click="save"
          class="inline-flex items-center gap-2 bg-ink px-5 py-3 text-xs uppercase tracking-widest text-ivory hover:bg-ink/85 cursor-pointer"
        >
          <Save class="h-3.5 w-3.5" /> Lưu thay đổi
        </button>
        <button
          @click="reset"
          class="inline-flex items-center gap-2 border border-border px-5 py-3 text-xs uppercase tracking-widest hover:bg-secondary cursor-pointer"
        >
          <RotateCcw class="h-3.5 w-3.5" /> Khôi phục mặc định
        </button>
      </div>
    </div>
  </div>

  <SlideEditor v-if="editing" :slide="editing" @cancel="editing = null" @save="saveSlide" />
</template>

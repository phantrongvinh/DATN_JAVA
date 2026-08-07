<script setup>
import { reactive } from 'vue'
import FormField from '@/components/admin/FormField.vue'

const props = defineProps({
  slide: { type: Object, required: true },
})
const emit = defineEmits(['cancel', 'save'])

const d = reactive({ ...props.slide })
</script>

<template>
  <div
    class="fixed inset-0 z-50 flex items-center overflow-y-auto justify-center bg-black/60 p-4"
    @click="emit('cancel')"
  >
    <div class="w-full max-w-2xl max-h-[90vh] overflow-hidden rounded-lg bg-background" @click.stop>
      <div class="border-b p-6">
        <h3 class="font-display text-xl">Chỉnh sửa slide</h3>
      </div>

      <div class="max-h-[calc(90vh-140px)] overflow-y-auto p-6">
        <div class="grid gap-4 md:grid-cols-2">
          <FormField label="Eyebrow" v-model="d.eyebrow" />
          <FormField label="Nhãn CTA" v-model="d.ctaLabel" />
          <FormField label="Tiêu đề" v-model="d.title" />
          <FormField label="Chữ in nghiêng" v-model="d.titleItalic" />

          <div class="md:col-span-2">
            <FormField label="Mô tả" v-model="d.description" multiline />
          </div>

          <div class="md:col-span-2">
            <FormField label="Ảnh (URL)" v-model="d.image" />
          </div>

          <FormField label="Đường dẫn CTA" v-model="d.ctaTo" />
        </div>

        <div v-if="d.image" class="mt-4 aspect-[16/6] overflow-hidden bg-secondary">
          <img :src="d.image" alt="preview" class="h-full w-full object-cover" />
        </div>
      </div>

      <div class="flex justify-end gap-2 border-t p-4">
        <button
          @click="emit('cancel')"
          class="border border-border px-4 py-2 text-xs uppercase tracking-widest hover:bg-secondary cursor-pointer"
        >
          Hủy
        </button>
        <button
          @click="emit('save', { ...d })"
          class="bg-ink px-4 py-2 text-xs uppercase tracking-widest text-ivory hover:bg-ink/85 cursor-pointer"
        >
          Lưu slide
        </button>
      </div>
    </div>
  </div>
</template>

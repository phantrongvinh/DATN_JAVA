<script setup>
import { ref } from 'vue'
import { X, Upload } from 'lucide-vue-next'
import { useNotificationStore } from '@/stores/useNotificationStore'
import returnAPI from '@/api/returnAPI'

const props = defineProps({ show: Boolean, order: Object })
const emit = defineEmits(['update:show'])

const notification = useNotificationStore()
const reason = ref('')
const files = ref([])
const submitting = ref(false)

const close = () => emit('update:show', false)

const handleFileChange = (e) => {
  files.value = Array.from(e.target.files)
}

const handleSubmit = async () => {
  if (!reason.value.trim()) {
    notification.notify('Vui lòng nhập lý do trả hàng', 'error')
    return
  }
  submitting.value = true
  try {
    const formData = new FormData()
    formData.append('reason', reason.value)
    files.value.forEach((f) => formData.append('images', f))

    await returnAPI.requestReturn(props.order.id, formData)
    notification.notify('Đã gửi yêu cầu trả hàng', 'success')
    close()
  } catch (err) {
    notification.notify(err.response?.data?.message ?? 'Có lỗi xảy ra', 'error')
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div
    v-if="show && order"
    class="fixed inset-0 z-50 flex items-center justify-center bg-ink/60 p-4"
  >
    <div class="w-full max-w-md border border-border bg-background">
      <div class="flex items-center justify-between border-b border-border px-6 py-4">
        <h2 class="font-display text-xl">Yêu cầu trả hàng #{{ order.id }}</h2>
        <button @click="close" class="p-1 hover:bg-secondary"><X class="h-4 w-4" /></button>
      </div>

      <div class="space-y-4 p-6">
        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground"
            >Lý do trả hàng</span
          >
          <textarea
            v-model="reason"
            rows="4"
            placeholder="Mô tả lý do bạn muốn trả sản phẩm..."
            class="w-full border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
          />
        </label>

        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground"
            >Hình ảnh minh chứng</span
          >
          <div class="flex items-center gap-2 border border-dashed border-border p-3">
            <Upload class="h-4 w-4 text-muted-foreground" />
            <input
              type="file"
              multiple
              accept="image/*"
              @change="handleFileChange"
              class="text-sm"
            />
          </div>
          <p v-if="files.length" class="text-xs text-muted-foreground">
            {{ files.length }} ảnh đã chọn
          </p>
        </label>
      </div>

      <div class="flex justify-end gap-2 border-t border-border px-6 py-4">
        <button @click="close" class="border border-border px-4 py-2 text-sm hover:bg-secondary">
          Hủy
        </button>
        <button
          @click="handleSubmit"
          :disabled="submitting"
          class="bg-ink px-4 py-2 text-sm text-ivory hover:bg-ink/90 disabled:opacity-50"
        >
          {{ submitting ? 'Đang gửi...' : 'Gửi yêu cầu' }}
        </button>
      </div>
    </div>
  </div>
</template>

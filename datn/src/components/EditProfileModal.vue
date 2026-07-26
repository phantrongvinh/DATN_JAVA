<script setup>
import { ref, computed, watch } from 'vue'
import { X } from 'lucide-vue-next'

const props = defineProps({
  show: Boolean,
  user: Object,
})
const emit = defineEmits(['update:show', 'save'])

const form = ref({ fullName: '', phone: '' })

// Ngày sinh tách riêng 3 phần để hiển thị đẹp
const birthDay = ref(null)
const birthMonth = ref(null)
const birthYear = ref(null)

const days = Array.from({ length: 31 }, (_, i) => i + 1)
const months = [
  { value: 1, label: 'Tháng 1' },
  { value: 2, label: 'Tháng 2' },
  { value: 3, label: 'Tháng 3' },
  { value: 4, label: 'Tháng 4' },
  { value: 5, label: 'Tháng 5' },
  { value: 6, label: 'Tháng 6' },
  { value: 7, label: 'Tháng 7' },
  { value: 8, label: 'Tháng 8' },
  { value: 9, label: 'Tháng 9' },
  { value: 10, label: 'Tháng 10' },
  { value: 11, label: 'Tháng 11' },
  { value: 12, label: 'Tháng 12' },
]
const currentYear = new Date().getFullYear()
const years = Array.from({ length: 100 }, (_, i) => currentYear - i) // 100 năm gần nhất

watch(
  () => props.show,
  (val) => {
    if (val && props.user) {
      form.value = {
        fullName: props.user.fullName ?? '',
        phone: props.user.phone ?? '',
      }

      if (props.user.birthDay) {
        const d = new Date(props.user.birthDay)
        birthDay.value = d.getDate()
        birthMonth.value = d.getMonth() + 1
        birthYear.value = d.getFullYear()
      } else {
        birthDay.value = null
        birthMonth.value = null
        birthYear.value = null
      }
    }
  },
)

const canPickBirthDay = computed(() => props.user?.birthDayEditable)

const birthDayComplete = computed(() => birthDay.value && birthMonth.value && birthYear.value)

const close = () => emit('update:show', false)

const save = () => {
  const payload = {
    fullName: form.value.fullName,
    phone: form.value.phone,
  }

  if (canPickBirthDay.value && birthDayComplete.value) {
    const mm = String(birthMonth.value).padStart(2, '0')
    const dd = String(birthDay.value).padStart(2, '0')
    payload.birthDay = `${birthYear.value}-${mm}-${dd}`
  }

  emit('save', payload)
}
</script>

<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center bg-ink/60 p-4">
    <div class="w-full max-w-md border border-border bg-background">
      <div class="flex items-center justify-between border-b border-border px-6 py-4">
        <h2 class="font-display text-xl">Chỉnh sửa hồ sơ</h2>
        <button @click="close" class="p-1 hover:bg-secondary"><X class="h-4 w-4" /></button>
      </div>

      <div class="space-y-5 p-6">
        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground">Họ và tên</span>
          <input
            v-model="form.fullName"
            class="w-full border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
          />
        </label>

        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground"
            >Số điện thoại</span
          >
          <input
            v-model="form.phone"
            class="w-full border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
          />
        </label>

        <div class="space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground">
            Ngày sinh
            <span v-if="!canPickBirthDay" class="normal-case text-muted-foreground/70"
              >(đã thiết lập, không thể sửa)</span
            >
          </span>

          <div class="grid grid-cols-3 gap-2">
            <select
              v-model="birthDay"
              :disabled="!canPickBirthDay"
              class="border border-border bg-background px-2 py-2 text-sm outline-none focus:border-foreground disabled:cursor-not-allowed disabled:bg-secondary disabled:text-muted-foreground"
            >
              <option :value="null" disabled>Ngày</option>
              <option v-for="d in days" :key="d" :value="d">{{ d }}</option>
            </select>

            <select
              v-model="birthMonth"
              :disabled="!canPickBirthDay"
              class="border border-border bg-background px-2 py-2 text-sm outline-none focus:border-foreground disabled:cursor-not-allowed disabled:bg-secondary disabled:text-muted-foreground"
            >
              <option :value="null" disabled>Tháng</option>
              <option v-for="m in months" :key="m.value" :value="m.value">{{ m.label }}</option>
            </select>

            <select
              v-model="birthYear"
              :disabled="!canPickBirthDay"
              class="border border-border bg-background px-2 py-2 text-sm outline-none focus:border-foreground disabled:cursor-not-allowed disabled:bg-secondary disabled:text-muted-foreground"
            >
              <option :value="null" disabled>Năm</option>
              <option v-for="y in years" :key="y" :value="y">{{ y }}</option>
            </select>
          </div>

          <p v-if="canPickBirthDay" class="text-xs text-muted-foreground">
            Lưu ý: ngày sinh chỉ có thể thiết lập một lần duy nhất, không thể chỉnh sửa sau đó.
          </p>
        </div>
      </div>

      <div class="flex justify-end gap-2 border-t border-border px-6 py-4">
        <button @click="close" class="border border-border px-4 py-2 text-sm hover:bg-secondary">
          Hủy
        </button>
        <button @click="save" class="bg-ink px-4 py-2 text-sm text-ivory hover:bg-ink/90">
          Lưu
        </button>
      </div>
    </div>
  </div>
</template>

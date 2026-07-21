<script setup>
import { ref, watch } from 'vue'
import { X } from 'lucide-vue-next'

const props = defineProps({
  show: Boolean,
  voucher: Object,
})
const emit = defineEmits(['update:show', 'save'])

const defaultForm = () => ({
  code: '',
  description: '',
  discountType: 'PERCENT',
  discountValue: 10,
  minOrderValue: 0,
  maxDiscount: null,
  quantity: 100,
  stackable: false,
  startDate: new Date().toISOString().slice(0, 16),
  endDate: new Date(Date.now() + 14 * 86400000).toISOString().slice(0, 16),
  active: true,
})

const form = ref(defaultForm())

watch(
  () => props.show,
  (val) => {
    if (val) {
      form.value = props.voucher ? { ...props.voucher } : defaultForm()
    }
  },
)

const close = () => emit('update:show', false)
const save = () => emit('save', form.value)
</script>

<template>
  <div v-if="show" class="fixed inset-0 z-50 flex items-center justify-center bg-ink/60 p-4">
    <div class="w-full max-w-lg border border-border bg-background">
      <div class="flex items-center justify-between border-b border-border px-5 py-4">
        <h2 class="font-display text-xl">{{ voucher ? 'Sửa voucher' : 'Tạo voucher' }}</h2>
        <button @click="close" class="p-1 hover:bg-secondary"><X class="h-4 w-4" /></button>
      </div>

      <div class="grid gap-4 p-5 md:grid-cols-2">
        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground">Mã</span>
          <input
            v-model="form.code"
            class="w-full border border-border bg-background px-3 py-2 text-sm uppercase"
          />
        </label>

        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground">Loại giảm</span>
          <select
            v-model="form.discountType"
            class="w-full border border-border bg-background px-3 py-2 text-sm"
          >
            <option value="PERCENT">Phần trăm</option>
            <option value="FIXED">Cố định</option>
          </select>
        </label>

        <label class="block space-y-1.5 md:col-span-2">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground">Mô tả</span>
          <input
            v-model="form.description"
            class="w-full border border-border bg-background px-3 py-2 text-sm"
          />
        </label>

        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground"
            >Giá trị giảm</span
          >
          <input
            v-model.number="form.discountValue"
            type="number"
            class="w-full border border-border bg-background px-3 py-2 text-sm"
          />
        </label>

        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground"
            >Đơn tối thiểu</span
          >
          <input
            v-model.number="form.minOrderValue"
            type="number"
            class="w-full border border-border bg-background px-3 py-2 text-sm"
          />
        </label>

        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground"
            >Giảm tối đa</span
          >
          <input
            v-model.number="form.maxDiscount"
            type="number"
            class="w-full border border-border bg-background px-3 py-2 text-sm"
          />
        </label>

        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground">Số lượng</span>
          <input
            v-model.number="form.quantity"
            type="number"
            class="w-full border border-border bg-background px-3 py-2 text-sm"
          />
        </label>

        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground">Bắt đầu</span>
          <input
            v-model="form.startDate"
            type="datetime-local"
            class="w-full border border-border bg-background px-3 py-2 text-sm"
          />
        </label>

        <label class="block space-y-1.5">
          <span class="text-[11px] uppercase tracking-widest text-muted-foreground">Kết thúc</span>
          <input
            v-model="form.endDate"
            type="datetime-local"
            class="w-full border border-border bg-background px-3 py-2 text-sm"
          />
        </label>

        <label class="flex items-center gap-2 md:col-span-2">
          <input v-model="form.stackable" type="checkbox" />
          <span class="text-sm">Cho phép cộng dồn với voucher khác</span>
        </label>

        <label class="flex items-center gap-2 md:col-span-2">
          <input v-model="form.active" type="checkbox" />
          <span class="text-sm">Kích hoạt ngay</span>
        </label>
      </div>

      <div class="flex justify-end gap-2 border-t border-border px-5 py-4">
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

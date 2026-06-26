<template>
  <Transition>
    <div
      v-if="props.showProductModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm"
    >
      <div class="w-full max-w-4xl rounded border border-border bg-background shadow-2xl">
        <div class="border-b border-border px-6 py-5">
          <h2 class="font-display text-2xl">
            {{ props.product ? 'Cập nhật sản phẩm' : 'Thêm sản phẩm' }}
          </h2>
        </div>

        <!-- Body -->
        <div class="max-h-[70vh] overflow-y-auto p-6">
          <div class="grid grid-cols-2 gap-5">
            <div class="col-span-2">
              <label class="text-sm font-medium"> Tên sản phẩm </label>

              <input
                v-model="product.name"
                class="mt-2 w-full border border-border bg-background px-3 py-2 outline-none focus:border-foreground"
              />
              <p class="mt-1 text-xs text-red-500">
                <!-- {{ errors.name }} -->
              </p>
            </div>
          </div>
        </div>
        <!-- Footer -->
        <div class="flex justify-end gap-3 border-t border-border bg-secondary/20 px-6 py-4">
          <button class="border border-border px-5 py-2 hover:bg-secondary" @click="closeModal">
            Huỷ
          </button>

          <button class="bg-ink px-6 py-2 text-ivory hover:bg-ink/90" @click="">
            {{ props.product ? 'Cập nhật' : 'Thêm' }}
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>
<script setup>
import { onMounted } from 'vue'

const props = defineProps({
  showProductModal: {
    type: Boolean,
    default: false,
  },
  product: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['update:showProductModal'])

const closeModal = () => {
  emit('update:showProductModal', false)
}
</script>

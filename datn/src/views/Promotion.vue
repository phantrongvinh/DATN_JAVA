<template>
  <div class="flex-1">
    <!-- Header -->
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Quản lý khuyến mãi</h1>
    </header>

    <main class="p-6">
      <div class="border border-border bg-background">
        <!-- Toolbar -->
        <div class="flex flex-wrap items-center justify-between gap-3 border-b border-border p-4">
          <div class="relative">
            <Search
              class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
            />

            <input
              v-model="keyword"
              placeholder="Tìm khuyến mãi..."
              class="w-72 border border-border bg-background py-2 pl-9 pr-3 text-sm outline-none focus:border-foreground"
            />
          </div>

          <button
            class="flex items-center gap-2 bg-ink px-4 py-2 text-sm text-white hover:bg-ink/90"
            @click="handleOpenNewModal"
          >
            <Plus class="h-4 w-4" />
            Tạo khuyến mãi
          </button>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr
                class="border-b border-border bg-secondary/40 text-left text-[11px] uppercase tracking-widest text-muted-foreground"
              >
                <th class="px-4 py-3">Tên</th>
                <th>Loại</th>
                <th>Giá trị</th>
                <th>Bắt đầu</th>
                <th>Kết thúc</th>
                <th>Trạng thái</th>
                <th class="pr-4 text-right">Thao tác</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="promotion in promotions"
                :key="promotion.id"
                class="border-b border-border last:border-0 hover:bg-secondary/30"
              >
                <td class="px-4 py-3">
                  <div class="flex items-center gap-2">
                    <Tag class="h-4 w-4 text-yellow-500" />

                    <span class="font-medium">
                      {{ promotion.name }}
                    </span>
                  </div>
                </td>

                <td>
                  {{ promotion.discountType }}
                </td>

                <td class="font-medium">
                  <span v-if="promotion.discountType === 'PERCENT'">
                    {{ promotion.discountValue }}%
                  </span>

                  <span v-else> {{ ulti.formatVND(promotion.discountValue) }} </span>
                </td>

                <td>
                  {{ ulti.formatDate(promotion.startAt) }}
                </td>

                <td>
                  {{ ulti.formatDate(promotion.endAt) }}
                </td>

                <td>
                  <span
                    class="inline-flex rounded bg-green-100 px-2 py-1 text-[10px] uppercase tracking-widest text-green-700"
                  >
                    Đang chạy
                  </span>
                </td>

                <td class="pr-4">
                  <div class="flex justify-end gap-2">
                    <button
                      class="border border-border p-1.5 hover:bg-secondary"
                      @click="handleOpenEditModal(promotion)"
                    >
                      <Pencil class="h-4 w-4" />
                    </button>

                    <button class="border border-red-200 p-1.5 text-red-500 hover:bg-red-50">
                      <Trash2 class="h-4 w-4" />
                    </button>
                  </div>
                </td>
              </tr>

              <tr v-if="promotions.length === 0">
                <td colspan="7" class="py-8 text-center text-muted-foreground">
                  Chưa có khuyến mãi nào.
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div
          class="flex flex-col gap-4 border-t border-border px-6 py-4 md:flex-row md:items-center md:justify-between"
        >
          <div class="text-sm text-gray-500">
            Hiển thị
            {{ (currentPage - 1) * size + 1 }}
            -
            {{ Math.min(currentPage * size, totalElements) }}
            /
            {{ totalElements }}
            khuyến mãi
          </div>

          <div class="flex flex-wrap items-center gap-2">
            <button
              class="rounded border px-3 py-2 disabled:cursor-not-allowed disabled:opacity-50 hover:bg-secondary"
              :disabled="currentPage === 1"
              @click="changePage(currentPage - 1)"
            >
              Trước
            </button>

            <button
              v-for="pageNumber in totalPages"
              :key="pageNumber"
              @click="changePage(pageNumber)"
              class="flex h-9 w-9 items-center justify-center rounded border text-sm transition"
              :class="currentPage === pageNumber ? 'bg-black text-white' : 'hover:bg-secondary'"
            >
              {{ pageNumber }}
            </button>

            <button
              class="rounded border px-3 py-2 disabled:cursor-not-allowed disabled:opacity-50 hover:bg-secondary"
              :disabled="currentPage === totalPages"
              @click="changePage(currentPage + 1)"
            >
              Sau
            </button>
          </div>
        </div>
      </div>

      <!-- Modal -->
      <PromotionModal
        v-model:showPromotionModal="showModal"
        :promotion="editingPromotion"
        @save="handleSavePromotion"
      />
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import { Search, Plus, Pencil, Trash2, Tag } from 'lucide-vue-next'
import PromotionModal from '@/components/admin/PromotionModal.vue'
import { usePromotionStore } from '@/stores/usePromotionStore'
import { storeToRefs } from 'pinia'
import ulti from '@/ulti/ulti'

const keyword = ref('')

const showModal = ref(false)
const editingPromotion = ref(null)
const editingPromotionId = ref(null)

const promotionStore = usePromotionStore()

const { promotions, page, size, totalElements, totalPages, currentPage } =
  storeToRefs(promotionStore)

onMounted(async () => {
  await promotionStore.fetchAllPromotion({
    newPage: page.value,
    newSize: size.value,
  })
})

// fetch lại khi đổi trang
watch(currentPage, async (curPage) => {
  await promotionStore.fetchAllPromotion({
    newPage: curPage,
    newSize: size.value,
  })
})

// handle thêm khuyến mãi
const handleOpenNewModal = () => {
  showModal.value = true
  editingPromotion.value = null
}

// handle chinh sua khuyen mai
const handleOpenEditModal = (p) => {
  showModal.value = true
  editingPromotion.value = p
  editingPromotionId.value = p.id
}

const handleSavePromotion = async () => {
  if (editingPromotionId.value) {
    console.log('edit')
    showModal.value = false
    editingPromotion.value = null
    editingPromotionId.value = null
  } else {
    console.log('new')
    showModal.value = false
  }
}
</script>

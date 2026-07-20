<template>
  <div class="flex-1">
    <!-- Header -->
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Quản lý khuyến mãi</h1>
    </header>

    <main class="p-6">
      <div class="border border-border bg-background">
        <div
          class="flex flex-col gap-4 border-b border-border p-4 md:flex-row md:items-center md:justify-between"
        >
          <div class="flex flex-col gap-3 md:flex-row md:items-center md:flex-wrap">
            <!-- Search -->
            <div class="relative w-full md:w-64">
              <Search
                class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
              />
              <input
                type="text"
                v-model="keyword"
                placeholder="Tìm khuyến mãi..."
                class="w-full border border-border bg-background py-2 pl-10 pr-3 text-sm outline-none focus:border-foreground"
              />
            </div>

            <!-- Filter loại giảm giá -->
            <select
              v-model="discountTypeFilter"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option :value="null">Tất cả loại giảm</option>
              <option value="PERCENT">Giảm theo %</option>
              <option value="FIXED">Giảm số tiền cố định</option>
            </select>

            <!-- Filter trạng thái -->
            <select
              v-model="statusFilter"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option :value="null">Tất cả trạng thái</option>
              <option value="ACTIVE">Đang chạy</option>
              <option value="UPCOMING">Sắp diễn ra</option>
              <option value="ENDED">Đã hết hạn</option>
            </select>

            <!-- Reset -->
            <button
              @click="handleResetFilters"
              class="flex items-center justify-center gap-2 border border-border px-3 py-2 text-sm hover:bg-secondary"
            >
              <RotateCcw class="h-4 w-4" />
              Đặt lại
            </button>
          </div>

          <button
            class="flex items-center justify-center gap-2 bg-ink px-4 py-2 text-sm text-white hover:bg-ink/90"
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
                <th>Sản phẩm áp dụng</th>
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
                    class="inline-flex rounded px-2 py-1 text-[10px] uppercase tracking-widest"
                    :class="STATUS_CONFIG[getPromotionStatus(promotion)].class"
                  >
                    {{ STATUS_CONFIG[getPromotionStatus(promotion)].label }}
                  </span>
                </td>
                <td class="px-4 py-3">
                  <div class="max-w-[240px]">
                    <p class="text-sm font-medium">{{ promotion.productCount }} sản phẩm</p>
                    <p
                      v-if="promotion.productCount > 0"
                      class="truncate text-xs text-muted-foreground"
                      :title="promotion.productNames.join(', ')"
                    >
                      {{ promotion.productNames.slice(0, 3).join(', ') }}
                      <span v-if="promotion.productCount > 3">...</span>
                    </p>
                  </div>
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
import { Search, Plus, Pencil, Trash2, Tag, RotateCcw } from 'lucide-vue-next'
import PromotionModal from '@/components/admin/PromotionModal.vue'
import { usePromotionStore } from '@/stores/usePromotionStore'
import { storeToRefs } from 'pinia'
import ulti from '@/ulti/ulti'
import { useDebounce } from '@/composables/useDebounce'
import { useNotificationStore } from '@/stores/useNotificationStore'

const showModal = ref(false)
const editingPromotion = ref(null)
const editingPromotionId = ref(null)

const promotionStore = usePromotionStore()
const notificationStore = useNotificationStore()

const {
  promotions,
  page,
  size,
  totalElements,
  totalPages,
  currentPage,
  keyword,
  statusFilter,
  discountTypeFilter,
} = storeToRefs(promotionStore)

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
  notificationStore.notify('Đang mở bảng THÊM khuyến mãi', 'success')
}

// handle chinh sua khuyen mai
const handleOpenEditModal = (p) => {
  showModal.value = true
  editingPromotion.value = p
  editingPromotionId.value = p.id
  notificationStore.notify('Đang mở bảng SỬA khuyến mãi', 'success')
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

// handle status
const getPromotionStatus = (item) => {
  const now = new Date()
  const start = new Date(item.startAt)
  const end = new Date(item.endAt)

  if (now < start) return 'UPCOMING'
  if (now > end) return 'ENDED'
  return 'ACTIVE'
}

const STATUS_CONFIG = {
  ACTIVE: { label: 'Đang chạy', class: 'bg-green-100 text-green-700' },
  UPCOMING: { label: 'Sắp diễn ra', class: 'bg-blue-100 text-blue-700' },
  ENDED: { label: 'Đã hết hạn', class: 'bg-gray-100 text-gray-500' },
}

// handle filter
const debouncedKeyword = useDebounce(keyword, 500)

watch(debouncedKeyword, async () => {
  await promotionStore.fetchAllPromotion({ newPage: 1 })
})

watch([discountTypeFilter, statusFilter], async () => {
  await promotionStore.fetchAllPromotion({ newPage: 1 })
})

const handleResetFilters = () => {
  promotionStore.resetFilters()
  notificationStore.notify('Đặt lại bộ lọc', 'success')
}

// handle pagination
const changePage = async (p) => {
  if (p < 1 || p > totalPages.value) return
  await promotionStore.fetchAllPromotion({ newPage: p, newSize: size.value })
}
</script>

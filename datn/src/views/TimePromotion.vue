<script setup>
import { ref, onMounted, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { Plus, Search, Pencil, Trash2, Clock, RotateCcw } from 'lucide-vue-next'

import ulti from '@/ulti/ulti'

import TimePromotionModal from '@/components/admin/TimePromotionModal.vue'
import { useTimePromotionStore } from '@/stores/useTimePromotionStore'
import { useDebounce } from '@/composables/useDebounce'
import { useNotificationStore } from '@/stores/useNotificationStore'

const timePromotionStore = useTimePromotionStore()
const notificationStore = useNotificationStore()

const {
  timePromotions,
  currentPage,
  totalPages,
  totalElements,
  size,
  page,
  loading,
  error,
  message,
  keyword,
  activeFilter,
} = storeToRefs(timePromotionStore)

// Load data
onMounted(async () => {
  await timePromotionStore.fetchAllTimePromotion({
    newPage: page.value,
    newSize: size.value,
  })
})

// Search
const searchQuery = useDebounce(keyword, 500)

watch(searchQuery, async () => {
  await timePromotionStore.fetchAllTimePromotion({ newPage: 1 })
})

// Filter
watch(activeFilter, async () => {
  await timePromotionStore.fetchAllTimePromotion({ newPage: 1 })
})

const handleResetFilters = () => {
  timePromotionStore.resetFilters()
  notificationStore.notify('Đặt lại bộ lọc', 'success')
}

// Pagination
const changePage = async (page) => {
  if (page < 1 || page > totalPages.value) return

  await timePromotionStore.fetchAllTimePromotion({
    newPage: page,
    newSize: size.value,
  })
}

// Modal
const showModal = ref(false)

const editingPromotion = ref(null)

const handleOpenNewModal = () => {
  editingPromotion.value = null
  showModal.value = true
  notificationStore.notify('Đang mở bảng THÊM khung giờ khuyến mãi', 'success')
}

const handleEdit = (promotion) => {
  editingPromotion.value = promotion
  showModal.value = true
  notificationStore.notify('Đang mở bảng SỬA khung giờ khuyến mãi', 'success')
}

// Save
const handleSavePromotion = async (values) => {
  try {
    if (editingPromotion.value) {
      await timePromotionStore.updateTimePromotion(editingPromotion.value.id, values)
      console.log(message.value)
    } else {
      await timePromotionStore.createTimePromotion(values)
      console.log(message.value)
    }
  } catch (err) {
    console.log(error.value)
  }

  showModal.value = false

  await timePromotionStore.fetchAllTimePromotion({
    page: currentPage.value,
    size: size.value,
  })
}

// handle active
const toggleStatus = async (promotion) => {
  await timePromotionStore.toggleTimePromotion(promotion.id)
  await timePromotionStore.fetchAllTimePromotion({
    newPage: page.value,
    newSize: size.value,
  })
  console.log(message.value)
}

// Delete
const handleDelete = async (id) => {
  await timePromotionStore.deleteTimePromotion(id)

  await timePromotionStore.fetchAllTimePromotion({
    newPage: page.value,
    newSize: size.value,
  })
}
</script>

<template>
  <div class="flex-1">
    <!-- Header -->
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Quản lý khung giờ vàng</h1>
    </header>

    <main class="p-6">
      <div class="border border-border bg-background">
        <!-- Toolbar -->
        <div
          class="flex flex-col gap-4 border-b border-border p-4 md:flex-row md:items-center md:justify-between"
        >
          <div class="flex flex-col gap-3 md:flex-row md:items-center">
            <!-- Search -->
            <div class="relative w-full md:w-80">
              <Search
                class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
              />
              <input
                type="text"
                v-model="keyword"
                placeholder="Tìm khung giờ..."
                class="w-full border border-border bg-background py-2 pl-10 pr-3 text-sm outline-none focus:border-foreground"
              />
            </div>

            <!-- Filter trạng thái -->
            <select
              v-model="activeFilter"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option :value="null">Tất cả trạng thái</option>
              <option :value="true">Đang hiệu lực</option>
              <option :value="false">Hết hiệu lực</option>
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
            Tạo khung giờ
          </button>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto">
          <table class="min-w-full text-sm">
            <thead>
              <tr
                class="border-b border-border bg-secondary/40 text-left text-[11px] uppercase tracking-widest text-muted-foreground"
              >
                <th class="px-4 py-3">Tên</th>
                <th>Loại</th>
                <th>Giảm</th>
                <th>Bắt đầu</th>
                <th>Kết thúc</th>
                <th>Trạng thái</th>
                <th class="pr-4 text-right">Thao tác</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="item in timePromotions"
                :key="item.id"
                class="border-b border-border last:border-0 hover:bg-secondary/30"
              >
                <!-- Name -->
                <td class="px-4 py-3">
                  <div class="flex items-center gap-2">
                    <Clock class="h-4 w-4 text-yellow-500" />

                    <span class="font-medium">
                      {{ item.name }}
                    </span>
                  </div>
                </td>

                <!-- Type -->
                <td>
                  {{ item.discountType }}
                </td>

                <!-- Value -->
                <td class="font-medium">
                  <span v-if="item.discountType === 'PERCENT'"> {{ item.discountValue }}% </span>

                  <span v-else> {{ ulti.formatVND(item.discountValue) }}</span>
                </td>

                <!-- Start -->
                <td>
                  {{ item.startTime }}
                </td>

                <!-- End -->
                <td>
                  {{ item.endTime }}
                </td>

                <!-- Status -->
                <td class="px-4">
                  <button
                    @click="toggleStatus(item)"
                    :class="[
                      'relative h-5 w-9 rounded-full transition-colors duration-300',
                      item.active ? 'bg-emerald-500' : 'bg-gray-300',
                    ]"
                  >
                    <span
                      :class="[
                        'absolute top-0.5 h-4 w-4 rounded-full bg-white shadow transition-all duration-300',
                        item.active ? 'left-[18px]' : 'left-0.5',
                      ]"
                    />
                  </button>
                </td>

                <!-- Action -->
                <td class="px-4">
                  <div class="flex justify-end gap-2">
                    <button
                      class="border border-border p-1.5 hover:bg-secondary"
                      @click="handleEdit(item)"
                    >
                      <Pencil class="h-4 w-4" />
                    </button>

                    <button
                      class="border border-red-200 p-1.5 text-red-500 hover:bg-red-50"
                      @click="handleDelete(item.id)"
                    >
                      <Trash2 class="h-4 w-4" />
                    </button>
                  </div>
                </td>
              </tr>

              <tr v-if="!timePromotions || timePromotions.length === 0">
                <td colspan="7" class="py-12 text-center text-muted-foreground">
                  Chưa có khung giờ vàng nào.
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

            khung giờ
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
    </main>

    <TimePromotionModal
      v-model:show="showModal"
      :promotion="editingPromotion"
      @save="handleSavePromotion"
    />
  </div>
</template>

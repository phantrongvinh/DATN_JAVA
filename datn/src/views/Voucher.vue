<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { storeToRefs } from 'pinia'
import {
  Cake,
  Send,
  Tag,
  Plus,
  Trash2,
  Pencil,
  Search,
  RotateCcw,
  X,
  AlertTriangle,
} from 'lucide-vue-next'
import ulti from '@/ulti/ulti'
import { useVoucherStore } from '@/stores/useVoucherStore'
import { useDebounce } from '@/composables/useDebounce'
import VoucherModal from '@/components/admin/VoucherModal.vue'
import { useNotificationStore } from '@/stores/useNotificationStore'

const voucherStore = useVoucherStore()
const notification = useNotificationStore()

const {
  vouchers,
  currentPage,
  totalPages,
  totalElements,
  size,
  page,
  loading,
  error,
  keyword,
  discountTypeFilter,
  activeFilter,
  stackableFilter,
  personalFilter,
  birthdayPreview,
  birthdayLoading,
} = storeToRefs(voucherStore)

onMounted(async () => {
  await Promise.allSettled([
    voucherStore.fetchAll({ newPage: page.value, newSize: size.value }),
    voucherStore.fetchBirthdayPreview(),
  ])
})

const debouncedKeyword = useDebounce(keyword, 500)
watch(debouncedKeyword, () => voucherStore.fetchAll({ newPage: 1 }))
watch([discountTypeFilter, activeFilter, stackableFilter, personalFilter], () =>
  voucherStore.fetchAll({ newPage: 1 }),
)

const handleResetFilters = () => voucherStore.resetFilters()

const changePage = async (p) => {
  if (p < 1 || p > totalPages.value) return
  await voucherStore.fetchAll({ newPage: p, newSize: size.value })
}

// Birthday
const showBirthdayConfirm = ref(false)

const totalBirthdayUsers = computed(
  () =>
    (birthdayPreview.value?.willCreate.length ?? 0) +
    (birthdayPreview.value?.alreadyCreated.length ?? 0),
)
const canSendBirthday = computed(() => (birthdayPreview.value?.willCreate.length ?? 0) > 0)

const openBirthdayConfirm = () => {
  showBirthdayConfirm.value = true
}

const confirmSendBirthday = async () => {
  await voucherStore.generateBirthdayVouchers()
  showBirthdayConfirm.value = false
  await Promise.allSettled([
    voucherStore.fetchAll({ newPage: page.value, newSize: size.value }),
    voucherStore.fetchBirthdayPreview(),
  ])
}

// CRUD Modal
const showModal = ref(false)
const editingVoucher = ref(null)

const handleOpenNewModal = () => {
  editingVoucher.value = null
  showModal.value = true
}

const handleEdit = (v) => {
  editingVoucher.value = v
  showModal.value = true
}

const handleSave = async (values) => {
  if (editingVoucher.value) {
    await voucherStore.updateVoucher(editingVoucher.value.id, values)
  } else {
    await voucherStore.createVoucher(values)
  }
  showModal.value = false
  await voucherStore.fetchAll({ newPage: currentPage.value, newSize: size.value })
}

const handleDelete = async (id) => {
  await voucherStore.deleteVoucher(id)
  await voucherStore.fetchAll({ newPage: page.value, newSize: size.value })
}
</script>

<template>
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Voucher & Ưu đãi</h1>
    </header>

    <main class="space-y-6 p-6">
      <!-- Birthday generator -->
      <section v-if="totalBirthdayUsers > 0" class="border border-border bg-background p-6">
        <div class="flex flex-wrap items-start justify-between gap-4">
          <div class="flex gap-4">
            <div
              class="flex h-12 w-12 shrink-0 items-center justify-center rounded-full bg-gold/15 text-gold"
            >
              <Cake class="h-6 w-6" stroke-width="1.5" />
            </div>
            <div>
              <h2 class="font-display text-xl">
                Voucher sinh nhật {{ birthdayPreview?.monthLabel }}
              </h2>

              <p v-if="canSendBirthday" class="mt-1 text-sm text-muted-foreground">
                Tự động tạo voucher giảm 20% cho {{ birthdayPreview.willCreate.length }} khách hàng
                có sinh nhật trong tháng, hạn dùng đến cuối tháng, gửi email ngay khi xác nhận.
              </p>
              <p v-else class="mt-1 text-sm text-muted-foreground">
                Đã gửi voucher sinh nhật tháng này cho toàn bộ {{ totalBirthdayUsers }} khách hàng.
              </p>

              <!-- Danh sách khách hàng sinh nhật tháng này -->
              <div class="mt-3 flex flex-wrap gap-1.5">
                <span
                  v-for="u in birthdayPreview.willCreate"
                  :key="u.id"
                  class="inline-flex items-center gap-1 border border-border bg-secondary/50 px-2 py-0.5 text-[11px]"
                >
                  {{ u.fullName }}
                  <span class="text-muted-foreground">· {{ u.birthDay }}</span>
                </span>

                <span
                  v-for="u in birthdayPreview.alreadyCreated"
                  :key="u.id"
                  class="inline-flex items-center gap-1 border border-emerald-200 bg-emerald-50 px-2 py-0.5 text-[11px] text-emerald-700"
                >
                  {{ u.fullName }}
                  <span class="opacity-70">· {{ u.birthDay }} · đã gửi</span>
                </span>
              </div>
            </div>
          </div>

          <button
            v-if="canSendBirthday"
            @click="openBirthdayConfirm"
            :disabled="birthdayLoading"
            class="inline-flex items-center gap-2 bg-ink px-4 py-2 text-sm text-ivory hover:bg-ink/90 disabled:opacity-50"
          >
            <Send class="h-4 w-4" />
            {{
              birthdayLoading
                ? 'Đang tải...'
                : `Gửi cho ${birthdayPreview.willCreate.length} khách hàng`
            }}
          </button>
        </div>
      </section>

      <!-- Toolbar -->
      <section class="border border-border bg-background">
        <div
          class="flex flex-col gap-4 border-b border-border p-4 md:flex-row md:items-center md:justify-between"
        >
          <div class="flex flex-col gap-3 md:flex-row md:flex-wrap md:items-center">
            <div class="relative w-full md:w-64">
              <Search
                class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
              />
              <input
                v-model="keyword"
                type="text"
                placeholder="Tìm theo mã hoặc mô tả..."
                class="w-full border border-border bg-background py-2 pl-10 pr-3 text-sm outline-none focus:border-foreground"
              />
            </div>

            <select
              v-model="discountTypeFilter"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option :value="null">Tất cả loại giảm</option>
              <option value="PERCENT">Phần trăm</option>
              <option value="FIXED">Cố định</option>
            </select>

            <select
              v-model="activeFilter"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option :value="null">Tất cả trạng thái</option>
              <option :value="true">Đang hoạt động</option>
              <option :value="false">Ngừng hoạt động</option>
            </select>

            <select
              v-model="personalFilter"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option :value="null">Tất cả phạm vi</option>
              <option :value="true">Cá nhân</option>
              <option :value="false">Công khai</option>
            </select>

            <button
              @click="handleResetFilters"
              class="flex items-center gap-2 border border-border px-3 py-2 text-sm hover:bg-secondary"
            >
              <RotateCcw class="h-4 w-4" /> Đặt lại
            </button>
          </div>

          <button
            @click="handleOpenNewModal"
            class="inline-flex items-center gap-2 bg-ink px-4 py-2 text-sm text-ivory hover:bg-ink/90"
          >
            <Plus class="h-4 w-4" /> Voucher mới
          </button>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr
                class="border-b border-border bg-secondary/40 text-left text-[11px] uppercase tracking-widest text-muted-foreground"
              >
                <th class="px-4 py-3">Mã</th>
                <th>Mô tả</th>
                <th>Phạm vi</th>
                <th>Giá trị</th>
                <th>Hạn</th>
                <th>Đã dùng</th>
                <th>Trạng thái</th>
                <th class="pr-4 text-right">Thao tác</th>
              </tr>
            </thead>
            <tbody>
              <tr
                v-for="v in vouchers"
                :key="v.id"
                class="border-b border-border last:border-0 hover:bg-secondary/30"
                :class="{ 'opacity-60': !v.active }"
              >
                <td class="px-4 py-3 font-mono text-xs">{{ v.code }}</td>
                <td>
                  <div class="flex items-center gap-2">
                    <Tag class="h-3.5 w-3.5 text-gold" />
                    <span class="max-w-[200px] truncate">{{ v.description }}</span>
                  </div>
                  <p v-if="v.userEmail" class="mt-0.5 text-[11px] text-muted-foreground">
                    {{ v.userEmail }}
                  </p>
                </td>
                <td>
                  <span
                    class="inline-flex px-2 py-0.5 text-[10px] uppercase tracking-widest"
                    :class="
                      v.userId ? 'bg-purple-100 text-purple-800' : 'bg-blue-100 text-blue-800'
                    "
                  >
                    {{ v.userId ? 'Cá nhân' : 'Công khai' }}
                  </span>
                </td>
                <td class="font-medium text-sm">
                  {{
                    v.discountType === 'PERCENT'
                      ? `${v.discountValue}%`
                      : ulti.formatVND(v.discountValue)
                  }}
                </td>
                <td class="text-muted-foreground text-sm">
                  {{ ulti.formatDate(v.endDate) }}
                </td>
                <td class="text-muted-foreground text-sm">{{ v.usedCount }}/{{ v.quantity }}</td>
                <td>
                  <span
                    class="inline-flex px-2 py-0.5 text-[10px] uppercase tracking-widest"
                    :class="
                      v.active
                        ? 'bg-emerald-100 text-emerald-800'
                        : 'bg-secondary text-muted-foreground'
                    "
                  >
                    {{ v.active ? 'Đang hoạt động' : 'Ngừng hoạt động' }}
                  </span>
                </td>
                <td class="pr-4">
                  <div class="flex justify-end gap-1">
                    <button
                      @click="handleEdit(v)"
                      class="border border-border p-1.5 hover:bg-secondary"
                    >
                      <Pencil class="h-3.5 w-3.5" />
                    </button>
                    <button
                      @click="handleDelete(v.id)"
                      class="border border-red-200 p-1.5 text-red-600 hover:bg-red-50"
                    >
                      <Trash2 class="h-3.5 w-3.5" />
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="!vouchers.length">
                <td colspan="8" class="p-8 text-center text-sm text-muted-foreground">
                  Chưa có voucher.
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div
          class="flex flex-col gap-4 border-t border-border px-6 py-4 md:flex-row md:items-center md:justify-between"
        >
          <div class="text-sm text-muted-foreground">
            Hiển thị {{ (currentPage - 1) * size + 1 }} -
            {{ Math.min(currentPage * size, totalElements) }} / {{ totalElements }} voucher
          </div>
          <div class="flex flex-wrap items-center gap-2">
            <button
              class="rounded border px-3 py-2 disabled:opacity-50 hover:bg-secondary"
              :disabled="currentPage === 1"
              @click="changePage(currentPage - 1)"
            >
              Trước
            </button>
            <button
              v-for="p in totalPages"
              :key="p"
              @click="changePage(p)"
              class="flex h-9 w-9 items-center justify-center rounded border text-sm"
              :class="currentPage === p ? 'bg-black text-white' : 'hover:bg-secondary'"
            >
              {{ p }}
            </button>
            <button
              class="rounded border px-3 py-2 disabled:opacity-50 hover:bg-secondary"
              :disabled="currentPage === totalPages"
              @click="changePage(currentPage + 1)"
            >
              Sau
            </button>
          </div>
        </div>
      </section>
    </main>

    <!-- Birthday confirm dialog -->
    <div
      v-if="showBirthdayConfirm"
      class="fixed inset-0 z-50 flex items-center justify-center bg-ink/60 p-4"
    >
      <div class="w-full max-w-lg border border-border bg-background">
        <div class="flex items-center justify-between border-b border-border px-5 py-4">
          <h2 class="font-display text-xl">
            Xác nhận gửi voucher sinh nhật {{ birthdayPreview?.monthLabel }}
          </h2>
          <button @click="showBirthdayConfirm = false" class="p-1 hover:bg-secondary">
            <X class="h-4 w-4" />
          </button>
        </div>

        <div class="space-y-4 p-5 text-sm">
          <p class="text-muted-foreground">
            Sẽ tạo
            <span class="font-medium text-foreground">{{
              birthdayPreview?.willCreate.length ?? 0
            }}</span>
            voucher mới (giảm 20%, hạn đến cuối tháng) và gửi email đến:
          </p>

          <ul class="max-h-56 space-y-1 overflow-auto text-xs">
            <li
              v-for="u in birthdayPreview?.willCreate"
              :key="u.id"
              class="flex justify-between border-b border-border/50 py-1.5"
            >
              <span class="font-medium">{{ u.fullName }}</span>
              <span class="text-muted-foreground">{{ u.email }} · {{ u.birthDay }}</span>
            </li>
          </ul>

          <div
            v-if="birthdayPreview?.alreadyCreated.length"
            class="flex gap-3 border border-amber-300 bg-amber-50 p-3 text-amber-900"
          >
            <AlertTriangle class="h-5 w-5 shrink-0" />
            <p class="text-xs">
              {{ birthdayPreview.alreadyCreated.length }} khách hàng khác đã nhận voucher tháng này,
              sẽ không gửi lại.
            </p>
          </div>
        </div>

        <div class="flex justify-end gap-2 border-t border-border px-5 py-4">
          <button
            @click="showBirthdayConfirm = false"
            class="border border-border px-4 py-2 text-sm hover:bg-secondary"
          >
            Hủy
          </button>
          <button
            @click="confirmSendBirthday"
            class="bg-ink px-4 py-2 text-sm text-ivory hover:bg-ink/90"
          >
            Xác nhận & gửi email
          </button>
        </div>
      </div>
    </div>

    <VoucherModal v-model:show="showModal" :voucher="editingVoucher" @save="handleSave" />
  </div>
</template>

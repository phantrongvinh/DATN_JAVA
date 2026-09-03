<script setup>
import { ref, onMounted, computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useReturnStore } from '@/stores/useReturnStore'
import { useNotificationStore } from '@/stores/useNotificationStore'
import ulti from '@/ulti/ulti'

const store = useReturnStore()
const notification = useNotificationStore()

const { returns } = storeToRefs(store)

const statusFilter = ref('PENDING')

const showDetail = ref(false)
const selected = ref(null)

const openDetail = (rr) => {
  selected.value = rr
  showDetail.value = true
}
// CONFIRM MODAL
const showConfirmModal = ref(false)
const confirmType = ref(null)
const selectedReturnId = ref(null)
const rejectReason = ref('')
const processing = ref(false)

const openRejectModal = (id) => {
  selectedReturnId.value = id
  confirmType.value = 'reject'
  rejectReason.value = ''
  showConfirmModal.value = true
}

const openCompleteModal = (id) => {
  selectedReturnId.value = id
  confirmType.value = 'complete'
  showConfirmModal.value = true
}

const closeConfirmModal = () => {
  if (processing.value) return

  showConfirmModal.value = false
  confirmType.value = null
  selectedReturnId.value = null
  rejectReason.value = ''
}
const handleConfirm = async () => {
  if (!selectedReturnId.value) return

  if (confirmType.value === 'reject' && !rejectReason.value.trim()) {
    notification.notify('Vui lòng nhập lý do từ chối', 'error')
    return
  }

  processing.value = true

  try {
    // TỪ CHỐI
    if (confirmType.value === 'reject') {
      await store.resolveReturn(selectedReturnId.value, false, rejectReason.value.trim())

      notification.notify('Đã từ chối yêu cầu trả hàng', 'success')
    }

    // HOÀN TẤT TRẢ HÀNG
    if (confirmType.value === 'complete') {
      await store.manualComplete(selectedReturnId.value)

      notification.notify('Đã xác nhận hoàn tất trả hàng và hoàn tiền', 'success')
    }
    processing.value = false

    closeConfirmModal()

    await store.fetchReturns(statusFilter.value)
  } catch (error) {
    notification.notify(
      error?.response?.data?.message ||
        (confirmType.value === 'reject'
          ? 'Từ chối yêu cầu thất bại'
          : 'Xác nhận hoàn hàng thất bại'),
      'error',
    )
  } finally {
    processing.value = false
  }
}

// STATUS LABEL

const STATUS_LABEL = {
  PENDING: {
    label: 'Chờ xác nhận',
    class: 'bg-yellow-100 text-yellow-700',
  },

  APPROVED: {
    label: 'Đã duyệt',
    class: 'bg-blue-100 text-blue-700',
  },

  PICKED: {
    label: 'Shipper đã lấy',
    class: 'bg-indigo-100 text-indigo-700',
  },

  DELIVERING: {
    label: 'Đang hoàn về shop',
    class: 'bg-orange-100 text-orange-700',
  },

  COMPLETED: {
    label: 'Đã hoàn tất',
    class: 'bg-green-100 text-green-700',
  },

  REJECTED: {
    label: 'Đã từ chối',
    class: 'bg-red-100 text-red-700',
  },
}

const getStatusLabel = (status) => {
  return STATUS_LABEL[status]?.label || status
}

const getStatusClass = (status) => {
  return STATUS_LABEL[status]?.class || 'bg-secondary text-foreground'
}

// APPROVE

const handleApprove = async (id) => {
  try {
    await store.resolveReturn(id, true, '')

    notification.notify('Đã duyệt — vận đơn trả hàng đang được tạo', 'success')

    await store.fetchReturns(statusFilter.value)
  } catch (error) {
    notification.notify(error?.response?.data?.message || 'Duyệt yêu cầu thất bại', 'error')
  }
}

// FILTER

const handleFilter = async (status) => {
  statusFilter.value = status
  await store.fetchReturns(status)
}

onMounted(() => {
  store.fetchReturns(statusFilter.value)
})
</script>

<template>
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Yêu cầu trả hàng</h1>
    </header>

    <main class="p-6">
      <div class="border border-border bg-background">
        <div class="flex flex-wrap gap-2 border-b border-border p-4">
          <button
            v-for="s in ['PENDING', 'DELIVERING', 'PICKED', 'APPROVED', 'REJECTED', 'COMPLETED']"
            :key="s"
            @click="handleFilter(s)"
            class="border px-4 py-2 text-sm transition"
            :class="
              statusFilter === s
                ? 'border-black bg-black text-white'
                : 'border-border hover:bg-secondary'
            "
          >
            {{ getStatusLabel(s) }}
          </button>
        </div>

        <div
          v-if="!returns || returns.length === 0"
          class="flex min-h-[300px] flex-col items-center justify-center px-6 py-12 text-center"
        >
          <div class="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-secondary">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              class="h-8 w-8 text-muted-foreground"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
              stroke-width="1.5"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0l-2.586 2.586a2 2 0 01-1.414.586H10a2 2 0 01-1.414-.586L6 13m14 0H4"
              />
            </svg>
          </div>

          <h3 class="text-base font-medium">Không có yêu cầu trả hàng</h3>

          <p class="mt-1 max-w-sm text-sm text-muted-foreground">
            Hiện tại chưa có yêu cầu ở trạng thái
            <span class="font-medium">
              {{ getStatusLabel(statusFilter) }}
            </span>
          </p>
        </div>

        <div v-else>
          <div
            v-for="rr in returns"
            :key="rr.id"
            class="border-b border-border p-5 transition hover:bg-secondary/30"
            :class="{
              'border-l-4 border-l-yellow-500 bg-yellow-50/40': rr.status === 'PENDING',

              'border-l-4 border-l-emerald-500 bg-emerald-50/40': rr.status === 'DELIVERING',
            }"
          >
            <div class="flex items-center justify-between gap-6">
              <!-- INFO -->
              <div class="min-w-0 flex-1 cursor-pointer" @click="openDetail(rr)">
                <div class="flex items-center gap-3">
                  <p class="font-medium">Đơn #{{ rr.orderId }}</p>

                  <span
                    class="rounded-full px-3 py-1 text-[10px] font-semibold"
                    :class="getStatusClass(rr.status)"
                  >
                    {{ getStatusLabel(rr.status) }}
                  </span>
                </div>

                <p class="mt-2 text-sm text-muted-foreground line-clamp-1">
                  {{ rr.reason }}
                </p>

                <p class="mt-1 text-xs text-muted-foreground">
                  {{ ulti.formatDate(rr.createdAt) }}
                </p>
              </div>

              <!-- ACTION -->
              <div class="flex shrink-0 gap-2">
                <!-- PENDING -->
                <template v-if="rr.status === 'PENDING'">
                  <button
                    @click.stop="handleApprove(rr.id)"
                    class="bg-emerald-600 px-4 py-2 text-xs font-medium text-white hover:bg-emerald-700"
                  >
                    Duyệt
                  </button>

                  <button
                    @click.stop="openRejectModal(rr.id)"
                    class="border border-red-300 px-4 py-2 text-xs font-medium text-red-600 hover:bg-red-50"
                  >
                    Từ chối
                  </button>
                </template>

                <!-- DELIVERED TO SHOP -->
                <template v-else-if="rr.status === 'DELIVERING'">
                  <button
                    @click.stop="openCompleteModal(rr.id)"
                    class="bg-emerald-600 px-4 py-2 text-xs font-medium text-white hover:bg-emerald-700"
                  >
                    Xác nhận hoàn hàng
                  </button>
                </template>

                <!-- OTHER -->
                <span v-else class="rounded-full bg-secondary px-3 py-1 text-xs">
                  {{ getStatusLabel(rr.status) }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Modal chi tiết -->
    <div
      v-if="showDetail && selected"
      class="fixed inset-0 z-50 flex items-center justify-center bg-ink/60 p-4"
    >
      <div class="w-full max-w-lg border border-border bg-background p-6">
        <div class="flex items-start justify-between">
          <div>
            <p class="text-xs text-muted-foreground">Yêu cầu trả hàng</p>

            <h3 class="mt-1 font-display text-xl">#{{ selected.id }}</h3>
          </div>

          <span
            class="rounded-full px-3 py-1 text-[10px] font-semibold"
            :class="getStatusClass(selected.status)"
          >
            {{ getStatusLabel(selected.status) }}
          </span>
        </div>

        <div class="mt-6 space-y-4">
          <div>
            <p class="text-xs uppercase tracking-widest text-muted-foreground">Đơn hàng</p>

            <p class="mt-1 font-medium">#{{ selected.orderId }}</p>
          </div>

          <div>
            <p class="text-xs uppercase tracking-widest text-muted-foreground">Lý do trả hàng</p>

            <p class="mt-1 text-sm">
              {{ selected.reason }}
            </p>
          </div>

          <div v-if="selected.adminNote">
            <p class="text-xs uppercase tracking-widest text-muted-foreground">Ghi chú của Admin</p>

            <div class="mt-2 rounded-md bg-yellow-50 p-3">
              <p class="text-sm text-yellow-800">
                {{ selected.adminNote }}
              </p>
            </div>
          </div>

          <div v-if="selected.images?.length">
            <p class="text-xs uppercase tracking-widest text-muted-foreground">Hình ảnh</p>

            <div class="mt-3 flex flex-wrap gap-2">
              <img
                v-for="img in selected.images"
                :key="img"
                :src="img"
                class="h-20 w-20 rounded object-cover"
              />
            </div>
          </div>
        </div>

        <button
          @click="showDetail = false"
          class="mt-6 w-full border border-border py-2 text-sm hover:bg-secondary"
        >
          Đóng
        </button>
      </div>
    </div>

    <div
      v-if="showConfirmModal"
      class="fixed inset-0 z-[100] flex items-center justify-center bg-black/50 px-4"
      @click.self="closeConfirmModal"
    >
      <div class="w-full max-w-md rounded-xl bg-background p-6 shadow-2xl">
        <!-- HEADER -->
        <div class="flex items-start gap-4">
          <!-- ICON -->
          <div
            class="flex h-11 w-11 shrink-0 items-center justify-center rounded-full"
            :class="
              confirmType === 'reject'
                ? 'bg-red-100 text-red-600'
                : 'bg-emerald-100 text-emerald-600'
            "
          >
            <svg
              v-if="confirmType === 'reject'"
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M6 18L18 6M6 6l12 12"
              />
            </svg>

            <svg
              v-else
              xmlns="http://www.w3.org/2000/svg"
              class="h-5 w-5"
              fill="none"
              viewBox="0 0 24 24"
              stroke="currentColor"
            >
              <path
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="M5 13l4 4L19 7"
              />
            </svg>
          </div>

          <div class="flex-1">
            <h3 class="text-lg font-semibold">
              {{
                confirmType === 'reject' ? 'Từ chối yêu cầu trả hàng' : 'Xác nhận hoàn tất trả hàng'
              }}
            </h3>

            <p class="mt-1 text-sm text-muted-foreground">
              {{
                confirmType === 'reject'
                  ? 'Bạn có chắc chắn muốn từ chối yêu cầu trả hàng này?'
                  : 'Shop đã nhận và kiểm tra hàng trả về?'
              }}
            </p>
          </div>

          <!-- CLOSE -->
          <button
            type="button"
            :disabled="processing"
            @click="closeConfirmModal"
            class="text-xl text-muted-foreground hover:text-foreground"
          >
            ×
          </button>
        </div>

        <!-- REJECT REASON -->
        <div v-if="confirmType === 'reject'" class="mt-5">
          <label class="mb-2 block text-sm font-medium">
            Lý do từ chối
            <span class="text-red-500">*</span>
          </label>

          <textarea
            v-model="rejectReason"
            rows="4"
            placeholder="Nhập lý do từ chối yêu cầu..."
            class="w-full resize-none rounded-lg border border-border bg-background px-3 py-2 text-sm outline-none focus:border-red-400 focus:ring-1 focus:ring-red-400"
          ></textarea>
        </div>

        <!-- COMPLETE WARNING -->
        <div v-else class="mt-5 rounded-lg border border-emerald-200 bg-emerald-50 p-4">
          <p class="text-sm leading-6 text-emerald-800">Sau khi xác nhận:</p>

          <ul class="mt-2 list-disc space-y-1 pl-5 text-sm text-emerald-800">
            <li>Yêu cầu trả hàng sẽ chuyển sang <b>Đã hoàn tất</b></li>
            <li>Đơn hàng sẽ được đánh dấu <b>Đã trả hàng</b></li>
            <li>Nếu khách đã thanh toán, hệ thống sẽ ghi nhận <b>Đã hoàn tiền</b></li>
          </ul>
        </div>

        <!-- ACTIONS -->
        <div class="mt-6 flex justify-end gap-3">
          <button
            type="button"
            :disabled="processing"
            @click="closeConfirmModal"
            class="rounded-lg border border-border px-4 py-2 text-sm font-medium hover:bg-secondary disabled:opacity-50"
          >
            Hủy
          </button>

          <button
            type="button"
            :disabled="processing || (confirmType === 'reject' && !rejectReason.trim())"
            @click="handleConfirm"
            class="rounded-lg px-5 py-2 text-sm font-medium text-white disabled:cursor-not-allowed disabled:opacity-50"
            :class="
              confirmType === 'reject'
                ? 'bg-red-600 hover:bg-red-700'
                : 'bg-emerald-600 hover:bg-emerald-700'
            "
          >
            <span v-if="processing"> Đang xử lý... </span>

            <span v-else>
              {{ confirmType === 'reject' ? 'Xác nhận từ chối' : 'Xác nhận hoàn hàng' }}
            </span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

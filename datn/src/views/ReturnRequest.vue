<script setup>
import { ref, onMounted } from 'vue'
import { storeToRefs } from 'pinia'
import { useReturnStore } from '@/stores/useReturnStore'
import { useNotificationStore } from '@/stores/useNotificationStore'
import ulti from '@/ulti/ulti'

const store = useReturnStore()
const notification = useNotificationStore()
const { returns } = storeToRefs(store)

const statusFilter = ref('PENDING')
onMounted(() => store.fetchReturns(statusFilter.value))

const showDetail = ref(false)
const selected = ref(null)
const openDetail = (rr) => {
  selected.value = rr
  showDetail.value = true
}

const handleApprove = async (id) => {
  await store.resolveReturn(id, true, '')
  notification.notify('Đã duyệt — vận đơn trả hàng đang được tạo', 'success')
  store.fetchReturns(statusFilter.value)
}

const handleReject = async (id) => {
  const note = prompt('Lý do từ chối:')
  if (!note) return
  await store.resolveReturn(id, false, note)
  notification.notify('Đã từ chối yêu cầu', 'success')
  store.fetchReturns(statusFilter.value)
}

const handleManualComplete = async (id) => {
  if (!confirm('Xác nhận đã nhận được hàng trả về? (dùng khi GHN không tự động cập nhật)')) return
  await store.manualComplete(id)
  notification.notify('Đã xác nhận hoàn tất trả hàng', 'success')
  store.fetchReturns(statusFilter.value)
}

const handleFilter = async (status) => {
  statusFilter.value = status
  await store.fetchReturns(status)
}
</script>

<template>
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Yêu cầu trả hàng</h1>
    </header>

    <main class="p-6">
      <div class="border border-border bg-background">
        <div class="flex gap-2 border-b border-border p-4">
          <button
            v-for="s in ['PENDING', 'APPROVED', 'COMPLETED', 'REJECTED']"
            :key="s"
            @click="handleFilter(s)"
            class="border px-3 py-2 text-sm"
            :class="statusFilter === s ? 'border-black bg-black text-white' : 'border-border'"
          >
            {{ s }}
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
            Hiện tại chưa có yêu cầu trả hàng nào ở trạng thái
            <span class="font-medium">{{ statusFilter }}</span
            >.
          </p>
        </div>

        <div
          v-else
          v-for="rr in returns"
          :key="rr.id"
          class="flex items-center justify-between border-b border-border p-5"
        >
          <div class="cursor-pointer" @click="openDetail(rr)">
            <p class="font-medium">Đơn #{{ rr.orderId }} — {{ rr.receiverName }}</p>
            <p class="mt-1 text-sm text-muted-foreground line-clamp-1">{{ rr.reason }}</p>
            <p class="mt-1 text-xs text-muted-foreground">{{ ulti.formatDate(rr.createdAt) }}</p>
          </div>

          <div class="flex gap-2">
            <template v-if="rr.status === 'PENDING'">
              <button
                @click="handleApprove(rr.id)"
                class="bg-emerald-600 px-4 py-2 text-xs text-white"
              >
                Duyệt
              </button>
              <button
                @click="handleReject(rr.id)"
                class="border border-red-300 px-4 py-2 text-xs text-red-600"
              >
                Từ chối
              </button>
            </template>
            <button
              v-else-if="rr.status === 'APPROVED'"
              @click="handleManualComplete(rr.id)"
              class="border border-border px-4 py-2 text-xs hover:bg-secondary"
            >
              Xác nhận đã nhận hàng
            </button>
            <span v-else class="rounded-full bg-secondary px-3 py-1 text-xs">{{ rr.status }}</span>
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
        <h3 class="font-display text-lg">Chi tiết yêu cầu trả hàng #{{ selected.id }}</h3>
        <p class="mt-3 text-sm">{{ selected.reason }}</p>
        <div v-if="selected.images.length" class="mt-4 flex flex-wrap gap-2">
          <img
            v-for="img in selected.images"
            :key="img"
            :src="img"
            class="h-20 w-20 object-cover"
          />
        </div>
        <button
          @click="showDetail = false"
          class="mt-6 w-full border border-border py-2 text-sm hover:bg-secondary"
        >
          Đóng
        </button>
      </div>
    </div>
  </div>
</template>

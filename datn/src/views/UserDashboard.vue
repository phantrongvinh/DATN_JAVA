<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { RotateCcw, Search, Trash2, Undo2 } from 'lucide-vue-next'
import UserModal from '@/components/admin/UserModal.vue'
import { useUserStore } from '@/stores/useUserStore'
import { useNotificationStore } from '@/stores/useNotificationStore'
import { useDebounce } from '@/composables/useDebounce'
import ulti from '@/ulti/ulti'

const userStore = useUserStore()
const notificationStore = useNotificationStore()

const { users, currentPage, totalPages, totalElements, size } = storeToRefs(userStore)

// Filter state
const filter = ref({
  search: '',
  isActive: null,
  isDeleted: false,
  sortBy: 'newest',
  birthDayFrom: '',
  birthDayTo: '',
  createdAtFrom: '',
  createdAtTo: '',
})

const buildParams = (page = 1) => ({
  page,
  size: size.value,
  ...filter.value,
  birthDayFrom: filter.value.birthDayFrom || null,
  birthDayTo: filter.value.birthDayTo || null,
  createdAtFrom: filter.value.createdAtFrom ? `${filter.value.createdAtFrom}T00:00:00` : null,
  createdAtTo: filter.value.createdAtTo ? `${filter.value.createdAtTo}T23:59:59` : null,
})

const applyFilter = () => userStore.fetchAllUsers(buildParams(1))

const debouncedSearch = useDebounce(
  computed(() => filter.value.search),
  500,
)
watch(debouncedSearch, applyFilter)

watch(
  () => [
    filter.value.isActive,
    filter.value.isDeleted,
    filter.value.sortBy,
    filter.value.birthDayFrom,
    filter.value.birthDayTo,
    filter.value.createdAtFrom,
    filter.value.createdAtTo,
  ],
  applyFilter,
)

const resetFilter = () => {
  filter.value = {
    search: '',
    isActive: null,
    isDeleted: false,
    sortBy: 'newest',
    birthDayFrom: '',
    birthDayTo: '',
    createdAtFrom: '',
    createdAtTo: '',
  }
  notificationStore.notify('Đặt lại bộ lọc', 'success')
}

// API
onMounted(async () => {
  await userStore.fetchAllUsers({ page: 1, size: 5, isDeleted: false })
})

const changePage = async (page) => {
  if (page < 1 || page > totalPages.value) return
  await userStore.fetchAllUsers(buildParams(page))
}

// Actions
const handleDisable = async (user) => {
  try {
    await userStore.softDeleteUser(user.id)
    notificationStore.notify('Đã vô hiệu hóa tài khoản', 'success')
  } catch (err) {
    notificationStore.notify(err.response?.data?.message ?? 'Có lỗi xảy ra', 'error')
  } finally {
    await userStore.fetchAllUsers(buildParams(currentPage.value))
    show.value = false
    selectUser.value = null
  }
}

const handleRestore = async (user) => {
  try {
    await userStore.restoreUser(user.id)
    notificationStore.notify('Đã kích hoạt lại tài khoản', 'success')
  } catch (err) {
    notificationStore.notify(err.response?.data?.message ?? 'Có lỗi xảy ra', 'error')
  } finally {
    await userStore.fetchAllUsers(buildParams(currentPage.value))
    show.value = false
    selectUser.value = null
  }
}

// Modal
const show = ref(false)
const selectUser = ref(null)

const openUser = (u) => {
  show.value = true
  selectUser.value = u
}
</script>

<template>
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Quản lý khách hàng</h1>
    </header>
    <main class="p-6">
      <div class="border border-border bg-background">
        <!-- Toolbar -->
        <div class="space-y-4 border-b border-border p-4">
          <div class="relative">
            <Search
              class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
            />
            <input
              v-model="filter.search"
              placeholder="Tên khách hàng, SĐT hoặc mã đơn..."
              class="w-full rounded border border-border bg-background py-2 pl-10 pr-3 text-sm outline-none"
            />
          </div>

          <div class="flex flex-col gap-3 md:flex-row md:flex-wrap md:items-center">
            <select
              v-model="filter.isActive"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option :value="null">Tất cả (xác thực email)</option>
              <option :value="true">Đã xác thực</option>
              <option :value="false">Chưa xác thực</option>
            </select>

            <select
              v-model="filter.isDeleted"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option :value="false">Đang hoạt động</option>
              <option :value="true">Đã vô hiệu hóa</option>
            </select>

            <select
              v-model="filter.sortBy"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            >
              <option value="newest">Mới tham gia nhất</option>
              <option value="oldest">Cũ nhất</option>
            </select>

            <input
              v-model="filter.birthDayFrom"
              type="date"
              placeholder="Sinh nhật từ"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            />
            <input
              v-model="filter.birthDayTo"
              type="date"
              placeholder="Sinh nhật đến"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            />

            <button
              @click="resetFilter"
              class="flex items-center text-sm justify-center gap-2 rounded border border-border px-4 py-2 hover:bg-secondary"
            >
              <RotateCcw class="h-4 w-4" /> Đặt lại
            </button>
          </div>

          <div class="flex flex-col gap-3 md:flex-row md:flex-wrap md:items-center">
            <h1 class="font-display">Thời gian tạo tài khoản:</h1>
            <input
              v-model="filter.createdAtFrom"
              type="date"
              placeholder="Tham gia từ ngày"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            />
            <input
              v-model="filter.createdAtTo"
              type="date"
              placeholder="Tham gia đến ngày"
              class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
            />
          </div>
        </div>

        <!-- Table -->
        <div class="overflow-auto">
          <table class="w-full text-sm">
            <thead
              class="border-b border-border bg-secondary/30 text-left text-xs uppercase tracking-widest text-muted-foreground"
            >
              <tr>
                <th class="p-4">ID</th>
                <th>Khách hàng</th>
                <th>Email</th>
                <th>SĐT</th>
                <th>Provider</th>
                <th>Tổng đơn</th>
                <th>Chi tiêu</th>
                <th>Xác thực</th>
                <th>Trạng thái</th>
                <th class="pr-4 text-right">Thao tác</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="user in users"
                :key="user.id"
                class="border-b border-border hover:bg-secondary/20"
                :class="{ 'opacity-50': user.deletedAt }"
              >
                <td class="cursor-pointer p-4" @click="openUser(user)">#{{ user.id }}</td>
                <td class="cursor-pointer font-medium" @click="openUser(user)">
                  {{ user.fullName }}
                </td>
                <td class="cursor-pointer" @click="openUser(user)">{{ user.email }}</td>
                <td class="cursor-pointer" @click="openUser(user)">{{ user.phone }}</td>
                <td class="cursor-pointer" @click="openUser(user)">
                  <span class="border px-2 py-1 text-xs">{{ user.provider }}</span>
                </td>
                <td class="cursor-pointer" @click="openUser(user)">{{ user.totalOrders }}</td>
                <td class="cursor-pointer" @click="openUser(user)">
                  {{ ulti.formatVND(user.totalSpending) }}
                </td>

                <!-- Xác thực email -->
                <td class="cursor-pointer" @click="openUser(user)">
                  <span
                    :class="[
                      'px-2 py-1 text-xs',
                      user.actived ? 'bg-blue-100 text-blue-600' : 'bg-gray-100 text-gray-500',
                    ]"
                  >
                    {{ user.actived ? 'Đã xác thực' : 'Chưa xác thực' }}
                  </span>
                </td>

                <!-- Trạng thái tài khoản (dựa deletedAt) -->
                <td class="cursor-pointer" @click="openUser(user)">
                  <span
                    :class="[
                      'px-3 py-1 text-xs',
                      user.deletedAt ? 'bg-red-100 text-red-500' : 'bg-green-100 text-green-600',
                    ]"
                  >
                    {{ user.deletedAt ? 'Đã vô hiệu hóa' : 'Hoạt động' }}
                  </span>
                </td>

                <td class="pr-4">
                  <div class="flex justify-end gap-1">
                    <button
                      v-if="!user.deletedAt"
                      @click="handleDisable(user)"
                      class="border border-red-200 p-1.5 text-red-600 hover:bg-red-50"
                      title="Vô hiệu hóa"
                    >
                      <Trash2 class="h-3.5 w-3.5" />
                    </button>
                    <button
                      v-else
                      @click="handleRestore(user)"
                      class="border border-emerald-200 p-1.5 text-emerald-600 hover:bg-emerald-50"
                      title="Kích hoạt lại"
                    >
                      <Undo2 class="h-3.5 w-3.5" />
                    </button>
                  </div>
                </td>
              </tr>

              <tr v-if="!users?.length">
                <td colspan="9" class="p-8 text-center text-muted-foreground">
                  Không có khách hàng.
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
            Hiển thị {{ (currentPage - 1) * size + 1 }} -
            {{ Math.min(currentPage * size, totalElements) }} / {{ totalElements }} Khách hàng
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
              v-for="pageNumber in totalPages"
              :key="pageNumber"
              @click="changePage(pageNumber)"
              class="flex h-9 w-9 items-center justify-center rounded border text-sm transition"
              :class="currentPage === pageNumber ? 'bg-black text-white' : 'hover:bg-secondary'"
            >
              {{ pageNumber }}
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
        <UserModal
          v-model:show="show"
          :user="selectUser"
          @disable="handleDisable(selectUser)"
          @restore="handleRestore(selectUser)"
        />
      </div>
    </main>
  </div>
</template>

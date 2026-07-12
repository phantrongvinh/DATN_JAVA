<script setup>
import UserModal from '@/components/admin/UserModal.vue'
import { useUserStore } from '@/stores/useUserStore'
import ulti from '@/ulti/ulti'
import { storeToRefs } from 'pinia'
import { onMounted, ref } from 'vue'

const userStore = useUserStore()

const { users, currentPage, totalPages, totalElements, size } = storeToRefs(userStore)

const keyword = ref('')

onMounted(async () => {
  await userStore.fetchAllUsers({ page: 1, size: 5 })
})

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
        <!-- Header -->

        <div class="flex items-center justify-between border-b border-border p-6">
          <input
            v-model="keyword"
            placeholder="Tìm tên, email..."
            class="w-72 border border-border px-4 py-2 outline-none"
          />
        </div>

        <!-- Table -->

        <div class="overflow-auto">
          <table class="w-full">
            <thead
              class="border-b border-border bg-secondary/30 text-left text-xs uppercase tracking-widest"
            >
              <tr>
                <th class="p-4">ID</th>

                <th>Khách hàng</th>

                <th>Email</th>

                <th>SĐT</th>

                <th>Provider</th>

                <th>Tổng đơn</th>

                <th>Chi tiêu</th>

                <th>Trạng thái</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="user in users"
                :key="user.id"
                class="border-b border-border hover:bg-secondary/20 cursor-pointer"
                @click="openUser(user)"
              >
                <td class="p-4">#{{ user.id }}</td>

                <td class="font-medium">
                  {{ user.fullName }}
                </td>

                <td>
                  {{ user.email }}
                </td>

                <td>
                  {{ user.phone }}
                </td>

                <td>
                  <span class="border px-2 py-1 text-xs">
                    {{ user.provider }}
                  </span>
                </td>

                <td>
                  {{ user.totalOrders }}
                </td>

                <td>
                  {{ ulti.formatVND(user.totalSpending) }}
                </td>

                <td>
                  <span
                    :class="[
                      'px-3 py-1 text-xs',
                      user.actived ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-500',
                    ]"
                  >
                    {{ user.actived ? 'Hoạt động' : 'Đã khóa' }}
                  </span>
                </td>
              </tr>

              <tr v-if="!users && users?.length === 0">
                <td colspan="8" class="p-8 text-center text-muted-foreground">
                  Không có đơn hàng.
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

            Khách hàng
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

        <UserModal v-model:show="show" :user="selectUser" />
      </div>
    </main>
  </div>
</template>

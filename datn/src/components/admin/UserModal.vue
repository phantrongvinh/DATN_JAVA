<template>
  <Transition name="fade">
    <div
      v-if="show"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      @click.self="closeModal"
    >
      <div class="flex max-h-[90vh] flex-col bg-background">
        <!-- Header -->
        <div class="flex items-center justify-between border-b border-border px-6 py-4">
          <div>
            <h2 class="font-display text-2xl">Chi tiết khách hàng</h2>

            <p class="mt-1 text-sm text-muted-foreground">Khách hàng #{{ user?.id }}</p>
          </div>

          <button @click="closeModal" class="text-2xl text-gray-500 hover:text-black">×</button>
        </div>

        <!-- Body -->
        <div class="flex-1 overflow-y-auto px-6 py-6">
          <!-- ================== THÔNG TIN ================== -->

          <section>
            <h3
              class="mb-4 text-xs font-semibold uppercase tracking-[0.25em] text-muted-foreground"
            >
              Thông tin khách hàng
            </h3>

            <div class="grid grid-cols-2 gap-5">
              <div>
                <label class="mb-2 block text-sm font-medium"> Họ tên </label>

                <input
                  :value="user?.fullName"
                  disabled
                  class="w-full rounded border border-border bg-secondary px-3 py-2"
                />
              </div>

              <div>
                <label class="mb-2 block text-sm font-medium"> Email </label>

                <input
                  :value="user?.email"
                  disabled
                  class="w-full rounded border border-border bg-secondary px-3 py-2"
                />
              </div>

              <div>
                <label class="mb-2 block text-sm font-medium"> Số điện thoại </label>

                <input
                  :value="user?.phone"
                  disabled
                  class="w-full rounded border border-border bg-secondary px-3 py-2"
                />
              </div>

              <div>
                <label class="mb-2 block text-sm font-medium"> Provider </label>

                <input
                  :value="user?.provider"
                  disabled
                  class="w-full rounded border border-border bg-secondary px-3 py-2"
                />
              </div>

              <div>
                <label class="mb-2 block text-sm font-medium"> Ngày tham gia </label>

                <input
                  :value="ulti.formatDate(user?.createdAt)"
                  disabled
                  class="w-full rounded border border-border bg-secondary px-3 py-2"
                />
              </div>

              <div>
                <label class="mb-2 block text-sm font-medium"> Trạng thái </label>

                <input
                  :value="user?.actived ? 'Hoạt động' : 'Đã khóa'"
                  disabled
                  class="w-full rounded border border-border bg-secondary px-3 py-2"
                />
              </div>
            </div>
          </section>

          <!-- ================== THỐNG KÊ ================== -->

          <section class="mt-10">
            <h3
              class="mb-4 text-xs font-semibold uppercase tracking-[0.25em] text-muted-foreground"
            >
              Thống kê
            </h3>

            <div class="grid grid-cols-3 gap-4">
              <div class="rounded border border-border p-5 text-center">
                <p class="text-xs uppercase text-muted-foreground">Tổng đơn</p>

                <p class="mt-3 text-3xl font-semibold">
                  {{ user?.totalOrders }}
                </p>
              </div>

              <div class="rounded border border-border p-5 text-center">
                <p class="text-xs uppercase text-muted-foreground">Hoàn thành</p>

                <p class="mt-3 text-3xl font-semibold">
                  {{ user?.completedOrders }}
                </p>
              </div>

              <div class="rounded border border-border p-5 text-center">
                <p class="text-xs uppercase text-muted-foreground">Chi tiêu</p>

                <p class="mt-3 text-xl font-semibold">
                  {{ ulti.formatVND(user?.totalSpending) }}
                </p>
              </div>
            </div>
          </section>

          <!-- ================== ĐƠN HÀNG ================== -->

          <section class="mt-10">
            <h3
              class="mb-4 text-xs font-semibold uppercase tracking-[0.25em] text-muted-foreground"
            >
              Đơn hàng đã giao
            </h3>

            <div
              v-if="!user?.deliveredOrders?.length"
              class="rounded border border-dashed border-border py-10 text-center text-muted-foreground"
            >
              Khách hàng chưa có đơn hàng hoàn thành
            </div>

            <div v-else class="space-y-4">
              <div
                v-for="order in user.deliveredOrders"
                :key="order.id"
                class="rounded border border-border p-5"
              >
                <div class="flex items-start justify-between">
                  <div>
                    <h4 class="font-semibold">Đơn hàng #{{ order.id }}</h4>

                    <p class="mt-2 text-sm text-muted-foreground">
                      {{ ulti.formatDate(order.createdAt) }}
                    </p>
                  </div>

                  <div class="text-right">
                    <span class="rounded bg-green-100 px-3 py-1 text-xs uppercase text-green-700">
                      {{ order.status }}
                    </span>

                    <p class="mt-3 text-lg font-semibold">
                      {{ ulti.formatVND(order.finalPrice) }}
                    </p>
                  </div>
                </div>

                <div class="mt-5 flex justify-end">
                  <button
                    class="rounded border border-border px-4 py-2 text-sm hover:bg-secondary"
                    @click="$emit('view-order', order.id)"
                  >
                    Xem chi tiết
                  </button>
                </div>
              </div>
            </div>
          </section>
        </div>

        <!-- Footer -->

        <div class="flex items-center justify-between border-t border-border px-6 py-4">
          <div>
            <button
              v-if="user?.actived"
              class="rounded bg-red-500 px-5 py-2 text-sm text-white hover:bg-red-600"
              @click="toggleActive(false)"
            >
              Khóa tài khoản
            </button>

            <button
              v-else
              class="rounded bg-green-600 px-5 py-2 text-sm text-white hover:bg-green-700"
              @click="toggleActive(true)"
            >
              Mở khóa tài khoản
            </button>
          </div>

          <div class="flex gap-3">
            <button
              class="rounded border border-border px-5 py-2 hover:bg-secondary"
              @click="closeModal"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script setup>
import ulti from '@/ulti/ulti'

const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
  user: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['update:show'])

const closeModal = () => {
  emit('update:show', false)
}
</script>

<script setup>
import { onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { Logs, Search, RotateCcw, CornerDownLeft, Truck } from 'lucide-vue-next'

import { useNotificationStore } from '@/stores/useNotificationStore'
import { useShippingSimulatorStore } from '@/stores/userShippingSimulatorStore'

const activeTab = ref('DELIVERY')

const store = useShippingSimulatorStore()
const notification = useNotificationStore()

const {
  orders,
  orderLoading,
  orderError,
  processingId,
  orderFilter,
  returns,
  returnLoading,
  returnError,
} = storeToRefs(store)

const statusOptions = [
  {
    value: '',
    label: 'Tất cả',
  },
  {
    value: 'CONFIRMED',
    label: 'Đã xác nhận',
  },
  {
    value: 'PICKED',
    label: 'Đã lấy hàng',
  },
  {
    value: 'SHIPPING',
    label: 'Đang giao',
  },
]

const fetchOrders = async () => {
  try {
    await store.fetchOrders()
  } catch (error) {
    notification.notify(
      error.response?.data?.message || 'Không thể tải danh sách đơn hàng',
      'error',
    )
  }
}

const handleFilter = async () => {
  await fetchOrders()
}

const resetFilter = async () => {
  try {
    await store.resetFilter()
  } catch (error) {
    notification.notify('Không thể tải danh sách đơn hàng', 'error')
  }
}

const updateStatus = async (orderId, action) => {
  try {
    await store.updateStatus(orderId, action)

    const statusText = {
      picked: 'Đã lấy hàng',
      delivering: 'Đang giao',
      delivered: 'Giao thành công',
    }

    notification.notify(`Đơn #${orderId}: ${statusText[action]}`, 'success')
  } catch (error) {
    console.log(error)

    notification.notify(
      error.response?.data?.message || 'Không thể cập nhật trạng thái đơn hàng',
      'error',
    )
  }
}

const canPicked = (order) => {
  return order.status === 'CONFIRMED'
}

const canDelivering = (order) => {
  return order.status === 'PICKED'
}

const canDelivered = (order) => {
  return order.status === 'SHIPPING'
}

const getStatusLabel = (status) => {
  const labels = {
    CONFIRMED: 'Đã xác nhận',
    PICKED: 'Đã lấy hàng',
    SHIPPING: 'Đang giao',
  }

  return labels[status] || status
}

onMounted(fetchOrders)

// return
const processingReturnId = ref(null)

const getReturnStatusLabel = (status) => {
  const labels = {
    PENDING: 'Chờ duyệt',
    APPROVED: 'Đã duyệt',
    PICKED: 'Đã lấy hàng',
    DELIVERING: 'Đang trả hàng',
    COMPLETED: 'Hoàn thành',
    REJECTED: 'Đã từ chối',
  }

  return labels[status] || status
}

const fetchReturns = async () => {
  try {
    await store.fetchReturns()
  } catch (error) {
    notification.notify(
      error.response?.data?.message || 'Không thể tải danh sách yêu cầu trả hàng',
      'error',
    )
  }
}

const updateReturnStatus = async (returnId, action) => {
  try {
    processingReturnId.value = returnId

    await store.updateReturnStatus(returnId, action)

    const statusText = {
      picked: 'Đã lấy hàng trả',
      delivering: 'Đang vận chuyển về shop',
    }
    message.value = `Yêu cầu trả hàng #${returnId}: ${statusText[action]}`
  } catch (err) {
    console.log(err)
    returnError.value = err.response?.data?.message || 'Không thể cập nhật trạng thái trả hàng'
  } finally {
    processingReturnId.value = null
  }
}

const canReturnPicked = (returnOrder) => {
  return returnOrder.status === 'APPROVED'
}

const canReturnDelivering = (returnOrder) => {
  return returnOrder.status === 'PICKED'
}

const canReturnDelivered = (returnOrder) => {
  return returnOrder.status === 'SHIPPING'
}

onMounted(async () => {
  await fetchReturns()
})
</script>

<template>
  <div class="flex-1">
    <!-- Header -->
    <header class="border-b border-border bg-background px-6 py-4">
      <div class="flex items-center justify-between">
        <div>
          <h1 class="font-display text-2xl">Shipper</h1>

          <p class="mt-1 text-sm text-muted-foreground">Quá trình giao hàng của shipper</p>
        </div>

        <button
          @click="
            () => {
              fetchOrders()
              fetchReturns()
            }
          "
          :disabled="orderLoading || returnLoading"
          class="border border-border px-4 py-2 text-sm hover:bg-secondary"
        >
          {{ orderLoading || returnLoading ? 'Đang tải...' : '↻ Làm mới' }}
        </button>
      </div>
    </header>

    <!-- Tabs -->
    <div class="border-b border-border bg-background px-6">
      <div class="flex gap-8">
        <button
          @click="activeTab = 'DELIVERY'"
          class="relative py-4 text-sm font-medium transition"
          :class="
            activeTab === 'DELIVERY'
              ? 'text-foreground'
              : 'text-muted-foreground hover:text-foreground'
          "
        >
          <div class="flex items-center gap-2">
            <Truck class="h-4 w-4" />
            <span>Giao hàng</span>
          </div>

          <div
            v-if="activeTab === 'DELIVERY'"
            class="absolute bottom-0 left-0 right-0 h-0.5 bg-foreground"
          />
        </button>

        <button
          @click="activeTab = 'RETURN'"
          class="relative py-4 text-sm font-medium transition"
          :class="
            activeTab === 'RETURN'
              ? 'text-foreground'
              : 'text-muted-foreground hover:text-foreground'
          "
        >
          <div class="flex items-center gap-2">
            <CornerDownLeft class="h-4 w-4" />
            <span>Trả hàng</span>
          </div>

          <div
            v-if="activeTab === 'RETURN'"
            class="absolute bottom-0 left-0 right-0 h-0.5 bg-foreground"
          />
        </button>
      </div>
    </div>

    <main class="p-6">
      <!-- Filter -->
      <div v-if="activeTab === 'DELIVERY'">
        <div class="mb-6 border border-border bg-background p-4">
          <div class="flex flex-col gap-3 md:flex-row md:items-end">
            <!-- Order ID -->
            <div class="flex-1">
              <label
                class="mb-1.5 block text-xs font-medium uppercase tracking-widest text-muted-foreground"
              >
                Mã đơn hàng
              </label>

              <div class="relative">
                <Search
                  class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
                />

                <input
                  v-model="orderFilter.search"
                  type="number"
                  min="1"
                  placeholder="Nhập mã đơn hàng..."
                  class="w-full border border-border bg-background py-2.5 pl-9 pr-3 text-sm outline-none focus:border-foreground"
                  @keyup.enter="handleFilter"
                />
              </div>
            </div>

            <!-- Status -->
            <div class="w-full md:w-56">
              <label
                class="mb-1.5 block text-xs font-medium uppercase tracking-widest text-muted-foreground"
              >
                Trạng thái
              </label>

              <select
                v-model="orderFilter.status"
                class="w-full border border-border bg-background px-3 py-2.5 text-sm outline-none focus:border-foreground"
              >
                <option v-for="item in statusOptions" :key="item.value" :value="item.value">
                  {{ item.label }}
                </option>
              </select>
            </div>

            <!-- Search -->
            <button
              @click="handleFilter"
              :disabled="orderLoading"
              class="inline-flex items-center justify-center gap-2 bg-ink px-5 py-2.5 text-xs uppercase tracking-widest text-ivory hover:bg-ink/90 disabled:opacity-50"
            >
              <Search class="h-4 w-4" />
              Tìm kiếm
            </button>

            <!-- Reset -->
            <button
              @click="resetFilter"
              :disabled="orderLoading"
              class="inline-flex items-center justify-center gap-2 border border-border px-5 py-2.5 text-xs uppercase tracking-widest hover:bg-secondary disabled:opacity-50"
            >
              <RotateCcw class="h-4 w-4" />
              Đặt lại
            </button>
          </div>
        </div>
        <!-- Message -->
        <div
          v-if="message"
          class="mb-4 border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700"
        >
          {{ message }}
        </div>

        <div
          v-if="orderError"
          class="mb-4 border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700"
        >
          {{ error }}
        </div>

        <!-- Loading -->
        <div
          v-if="orderLoading && orders.length === 0"
          class="py-10 text-center text-sm text-muted-foreground"
        >
          Đang tải danh sách đơn hàng...
        </div>

        <!-- Empty -->
        <div
          v-else-if="orders && orders.length === 0"
          class="border border-border bg-background py-16 text-center"
        >
          <div class="flex justify-center">
            <Logs class="h-12 w-12 text-muted-foreground" />
          </div>

          <h3 class="mt-4 font-medium">Không có đơn hàng</h3>

          <p class="mt-1 text-sm text-muted-foreground">
            Hiện tại không có đơn hàng nào để giao hàng.
          </p>
        </div>

        <!-- Orders -->
        <div v-else class="space-y-4">
          <div
            v-for="order in orders"
            :key="order.id"
            class="border border-border bg-background p-5"
          >
            <!-- Order information -->
            <div class="flex items-start justify-between">
              <div>
                <div class="flex items-center gap-3">
                  <h3 class="font-medium">Đơn hàng #{{ order.id }}</h3>

                  <span class="rounded-full bg-secondary px-3 py-1 text-xs">
                    {{ getStatusLabel(order.status) }}
                  </span>
                </div>

                <div class="mt-3 space-y-1 text-sm text-muted-foreground">
                  <p>
                    Người nhận:
                    {{ order.receiverName || order.user?.fullName || '---' }}
                  </p>

                  <p>
                    SDT
                    {{ order.receiverPhone || '---' }}
                  </p>

                  <p>
                    Địa chỉ giao hàng
                    {{ order.shippingDetail || '---' }}
                  </p>

                  <p v-if="order.trackingCode">
                    Mã vận đơn:
                    <strong class="text-foreground">
                      {{ order.trackingCode }}
                    </strong>
                  </p>
                </div>
              </div>

              <!-- Status -->
              <div class="text-right text-sm">
                <p class="text-muted-foreground">Trạng thái</p>

                <p class="mt-1 font-medium">
                  {{ getStatusLabel(order.status) }}
                </p>
              </div>
            </div>

            <!-- Progress -->
            <div class="mt-6">
              <div class="flex items-center">
                <!-- Confirmed -->
                <div class="flex flex-1 items-center">
                  <div
                    class="flex h-9 w-9 items-center justify-center rounded-full text-sm"
                    :class="
                      ['CONFIRMED', 'PICKED', 'SHIPPING', 'DELIVERED'].includes(order.status)
                        ? 'bg-black text-white'
                        : 'bg-secondary'
                    "
                  >
                    ✓
                  </div>

                  <div class="ml-2 text-xs">Đã xác nhận</div>

                  <div class="mx-3 h-px flex-1 bg-border"></div>
                </div>

                <!-- Picked -->
                <div class="flex flex-1 items-center">
                  <div
                    class="flex h-9 w-9 items-center justify-center rounded-full text-sm"
                    :class="
                      ['PICKED', 'SHIPPING', 'DELIVERED'].includes(order.status)
                        ? 'bg-black text-white'
                        : 'bg-secondary'
                    "
                  >
                    <span v-if="['PICKED', 'SHIPPING', 'DELIVERED'].includes(order.status)">
                      ✓
                    </span>
                  </div>

                  <div class="ml-2 text-xs">Đã lấy hàng</div>

                  <div class="mx-3 h-px flex-1 bg-border"></div>
                </div>

                <!-- Shipping -->
                <div class="flex flex-1 items-center">
                  <div
                    class="flex h-9 w-9 items-center justify-center rounded-full text-sm"
                    :class="
                      ['SHIPPING', 'DELIVERED'].includes(order.status)
                        ? 'bg-black text-white'
                        : 'bg-secondary'
                    "
                  >
                    <span v-if="['SHIPPING', 'DELIVERED'].includes(order.status)"> ✓ </span>
                  </div>

                  <div class="ml-2 text-xs">Đang giao</div>

                  <div class="mx-3 h-px flex-1 bg-border"></div>
                </div>

                <!-- Delivered -->
                <div>
                  <div
                    class="flex h-9 w-9 items-center justify-center rounded-full text-sm"
                    :class="
                      order.status === 'DELIVERED' ? 'bg-green-600 text-white' : 'bg-secondary'
                    "
                  >
                    <span v-if="order.status === 'DELIVERED'">✓</span>
                  </div>

                  <div class="mt-2 text-xs">Hoàn thành</div>
                </div>
              </div>
            </div>

            <!-- Actions -->
            <div class="mt-6 flex gap-2 border-t border-border pt-4">
              <button
                v-if="canPicked(order)"
                @click="updateStatus(order.id, 'picked')"
                :disabled="processingId === order.id"
                class="bg-blue-600 px-4 py-2 text-sm text-white disabled:opacity-50"
              >
                {{ processingId === order.id ? 'Đang xử lý...' : 'Shipper đã lấy hàng' }}
              </button>

              <button
                v-if="canDelivering(order)"
                @click="updateStatus(order.id, 'delivering')"
                :disabled="processingId === order.id"
                class="bg-orange-500 px-4 py-2 text-sm text-white disabled:opacity-50"
              >
                {{ processingId === order.id ? 'Đang xử lý...' : 'Bắt đầu giao hàng' }}
              </button>

              <button
                v-if="canDelivered(order)"
                @click="updateStatus(order.id, 'delivered')"
                :disabled="processingId === order.id"
                class="bg-green-600 px-4 py-2 text-sm text-white disabled:opacity-50"
              >
                {{ processingId === order.id ? 'Đang xử lý...' : 'Giao thành công' }}
              </button>

              <div
                v-if="order.status === 'DELIVERED'"
                class="flex items-center text-sm text-green-600"
              >
                ✓ Đơn hàng đã hoàn thành
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="activeTab === 'RETURN'">
        <div class="mb-6 border border-border bg-background p-4">
          <!-- filter trả hàng -->
        </div>

        <!-- ================= RETURN ORDERS ================= -->

        <div
          v-if="returns && returns.length === 0"
          class="border border-border bg-background py-16 text-center"
        >
          <div class="flex justify-center">
            <Logs class="h-12 w-12 text-muted-foreground" />
          </div>

          <h3 class="mt-4 font-medium">Không có đơn trả hàng</h3>

          <p class="mt-1 text-sm text-muted-foreground">
            Hiện tại không có yêu cầu trả hàng nào cần xử lý.
          </p>
        </div>

        <!-- Return List -->
        <div v-else class="space-y-4">
          <div
            v-for="returnOrder in returns"
            :key="returnOrder.id"
            class="border border-border bg-background p-5"
          >
            <!-- ================= HEADER ================= -->

            <div class="flex items-start justify-between">
              <div>
                <div class="flex items-center gap-3">
                  <h3 class="font-medium">Yêu cầu trả hàng #{{ returnOrder.id }}</h3>

                  <span class="rounded-full bg-secondary px-3 py-1 text-xs">
                    {{ getReturnStatusLabel(returnOrder.status) }}
                  </span>
                </div>

                <div class="mt-3 space-y-1 text-sm text-muted-foreground">
                  <p>
                    Đơn hàng:
                    <strong class="text-foreground"> #{{ returnOrder.orderId }} </strong>
                  </p>

                  <p>
                    Người nhận:
                    {{ returnOrder.receiverName || '---' }}
                  </p>

                  <p>
                    SĐT người trả hàng:
                    {{ returnOrder.fromPhone || '---' }}
                  </p>

                  <p>
                    SĐT người nhận:
                    {{ returnOrder.toPhone || '---' }}
                  </p>

                  <p>
                    Địa chỉ lấy hàng:
                    {{ returnOrder.address || '---' }}
                  </p>

                  <p v-if="returnOrder.ghnReturnCode">
                    Mã vận đơn trả:
                    <strong class="text-foreground">
                      {{ returnOrder.ghnReturnCode }}
                    </strong>
                  </p>
                </div>
              </div>

              <!-- Status -->

              <div class="text-right text-sm">
                <p class="text-muted-foreground">Trạng thái</p>

                <p class="mt-1 font-medium">
                  {{ getReturnStatusLabel(returnOrder.status) }}
                </p>
              </div>
            </div>

            <!-- ================= RETURN REASON ================= -->

            <div v-if="returnOrder.reason" class="mt-5 border border-border bg-secondary/30 p-4">
              <p class="text-xs text-muted-foreground">Lý do trả hàng</p>

              <p class="mt-1 text-sm">
                {{ returnOrder.reason }}
              </p>
            </div>

            <!-- ================= IMAGES ================= -->

            <div v-if="returnOrder.images?.length" class="mt-5">
              <p class="mb-2 text-sm font-medium">Hình ảnh đính kèm</p>

              <div class="flex flex-wrap gap-2">
                <img
                  v-for="image in returnOrder.images"
                  :key="image"
                  :src="image"
                  class="h-20 w-20 border border-border object-cover"
                />
              </div>
            </div>

            <!-- ================= PROGRESS ================= -->

            <div class="mt-6">
              <div class="flex items-center">
                <!-- Approved -->

                <div class="flex flex-1 items-center">
                  <div
                    class="flex h-9 w-9 items-center justify-center rounded-full text-sm"
                    :class="
                      ['APPROVED', 'PICKED', 'DELIVERING', 'COMPLETED'].includes(returnOrder.status)
                        ? 'bg-black text-white'
                        : 'bg-secondary'
                    "
                  >
                    <span
                      v-if="
                        ['APPROVED', 'PICKED', 'DELIVERING', 'COMPLETED'].includes(
                          returnOrder.status,
                        )
                      "
                    >
                      ✓
                    </span>
                  </div>

                  <div class="ml-2 text-xs">Đã duyệt</div>

                  <div class="mx-3 h-px flex-1 bg-border"></div>
                </div>

                <!-- Picked -->

                <div class="flex flex-1 items-center">
                  <div
                    class="flex h-9 w-9 items-center justify-center rounded-full text-sm"
                    :class="
                      ['PICKED', 'DELIVERING', 'COMPLETED'].includes(returnOrder.status)
                        ? 'bg-black text-white'
                        : 'bg-secondary'
                    "
                  >
                    <span v-if="['PICKED', 'DELIVERING', 'COMPLETED'].includes(returnOrder.status)">
                      ✓
                    </span>
                  </div>

                  <div class="ml-2 text-xs">Đã lấy hàng</div>

                  <div class="mx-3 h-px flex-1 bg-border"></div>
                </div>

                <!-- Delivering -->

                <div class="flex flex-1 items-center">
                  <div
                    class="flex h-9 w-9 items-center justify-center rounded-full text-sm"
                    :class="
                      ['DELIVERING', 'COMPLETED'].includes(returnOrder.status)
                        ? 'bg-black text-white'
                        : 'bg-secondary'
                    "
                  >
                    <span v-if="['DELIVERING', 'COMPLETED'].includes(returnOrder.status)"> ✓ </span>
                  </div>

                  <div class="ml-2 text-xs">Đang giao</div>

                  <div class="mx-3 h-px flex-1 bg-border"></div>
                </div>

                <!-- Completed -->

                <div>
                  <div
                    class="flex h-9 w-9 items-center justify-center rounded-full text-sm"
                    :class="
                      returnOrder.status === 'COMPLETED'
                        ? 'bg-green-600 text-white'
                        : 'bg-secondary'
                    "
                  >
                    <span v-if="returnOrder.status === 'COMPLETED'"> ✓ </span>
                  </div>

                  <div class="mt-2 text-xs">Hoàn thành</div>
                </div>
              </div>
            </div>

            <!-- ================= ACTION ================= -->

            <div class="mt-6 flex gap-2 border-t border-border pt-4">
              <!-- APPROVED -->

              <button
                v-if="canReturnPicked(returnOrder)"
                @click="updateReturnStatus(returnOrder.id, 'picked')"
                :disabled="processingReturnId === returnOrder.id"
                class="bg-blue-600 px-4 py-2 text-sm text-white disabled:opacity-50"
              >
                {{
                  processingReturnId === returnOrder.id ? 'Đang xử lý...' : 'Shipper đã lấy hàng'
                }}
              </button>

              <!-- PICKED -->

              <button
                v-if="canReturnDelivering(returnOrder)"
                @click="updateReturnStatus(returnOrder.id, 'delivering')"
                :disabled="processingReturnId === returnOrder.id"
                class="bg-orange-500 px-4 py-2 text-sm text-white disabled:opacity-50"
              >
                {{
                  processingReturnId === returnOrder.id ? 'Đang xử lý...' : 'Bắt đầu giao về shop'
                }}
              </button>

              <!-- DELIVERING -->
              <div
                v-if="returnOrder.status === 'DELIVERING'"
                class="flex items-center text-sm text-orange-600"
              >
                Đang vận chuyển hàng về cửa hàng
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

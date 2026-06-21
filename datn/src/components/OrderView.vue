<template>
  <div class="d-flex justify-content-between">
    <div class="w-75 d-flex justify-content-between gap-3">
      <div class="w-40">
        <div class="input-group mb-3">
          <span class="input-group-text bg-white border-end-0" id="basic-addon1">
            <i class="fa-solid fa-magnifying-glass"></i>
          </span>
          <input
            type="text"
            class="form-control border-start-0 fst-italic py-2"
            placeholder="Tìm kiếm mã đơn, khách hàng..."
          />
        </div>
      </div>
      <div class="w-20">
        <select class="form-select" aria-label="Default select example">
          <option selected disabled>Trạng thái đơn</option>
          <option value="1">Chờ xác nhận</option>
          <option value="2">Đang giao</option>
          <option value="3">Hoàn thành</option>
          <option value="4">Đã hủy</option>
        </select>
      </div>
      <div class="w-20">
        <select class="form-select" aria-label="Default select example">
          <option selected disabled>Thanh toán</option>
          <option value="1">Tiền mặt</option>
          <option value="2">Chuyển khoản</option>
        </select>
      </div>
      <div class="w-20">
        <select class="form-select" aria-label="Default select example">
          <option selected disabled>Thời gian</option>
          <option value="1">Hôm nay</option>
          <option value="2">Tháng này</option>
        </select>
      </div>
    </div>
    <div class="w-25 text-end">
      <button type="button" class="btn btn-primary w-50">Xuất hóa đơn</button>
    </div>
  </div>

  <div class="mt-5">
    <div class="d-flex justify-content-between gap-3">
      <div class="border border-opacity-50 rounded-4 border-secondary w-100">
        <div class="m-4">
          <div class="d-flex gap-4">
            <div class="w-25 d-flex justify-content-center align-items-center">
              <div class="d-inline-block rounded-circle bg-secondary p-4 h-75 w-75"></div>
            </div>
            <div class="w-75 d-flex flex-column gap-2 justify-content-center">
              <div class="fs-5 fw-bold text-secondary">Tổng đơn hàng</div>
              <div class="fs-2 fw-bold">850</div>
              <div class="fs-5 fw-bold text-secondary">Đơn đã đặt</div>
            </div>
          </div>
        </div>
      </div>
      <div class="border border-opacity-50 rounded-4 border-warning w-100">
        <div class="m-4">
          <div class="d-flex gap-4">
            <div class="w-25 d-flex justify-content-center align-items-center">
              <div class="d-inline-block rounded-circle bg-warning p-4 h-75 w-75"></div>
            </div>
            <div class="w-75 d-flex flex-column gap-2 justify-content-center">
              <div class="fs-5 fw-bold text-secondary">Chờ xử lý</div>
              <div class="fs-2 fw-bold">15</div>
              <div class="fs-5 fw-bold text-secondary">Đơn hàng mới</div>
            </div>
          </div>
        </div>
      </div>
      <div class="border border-opacity-50 rounded-4 border-success w-100">
        <div class="m-4">
          <div class="d-flex gap-4">
            <div class="w-25 d-flex justify-content-center align-items-center">
              <div class="d-inline-block rounded-circle bg-success p-4 h-75 w-75"></div>
            </div>
            <div class="w-75 d-flex flex-column gap-2 justify-content-center">
              <div class="fs-5 fw-bold text-secondary">Thành công</div>
              <div class="fs-2 fw-bold">820</div>
              <div class="fs-5 fw-bold text-secondary">Đơn hoàn tất</div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="mt-5 border rounded-4">
      <div class="m-4">
        <div class="text-start fw-bold fs-5 w-100">Danh sách đơn hàng</div>
        <div class="mt-3">
          <Table
            :initialValues="initialOrderList"
            :interactive="true"
            :isBorder="true"
            :orders="orders"
            :handleCancelOrder="handleCancelOrder"
            :handleUpdateOrder="handleUpdateOrder"
          />
        </div>

        <div>
          <ul class="pagination justify-content-end">
            <li class="page-item" :class="{ disabled: page === 1 }">
              <button class="page-link" @click="changePage(page - 1)">Previous</button>
            </li>
            <li v-for="p in totalPages" :key="p" class="page-item" :class="{ active: page === p }">
              <button class="page-link" @click="changePage(p)">{{ p }}</button>
            </li>
            <li class="page-item" :class="{ disabled: page === totalPages }">
              <button class="page-link" @click="changePage(page + 1)">Next</button>
            </li>
          </ul>
        </div>

        <div
          class="modal fade"
          id="orderModal"
          tabindex="-1"
          :class="{ show: isOpen }"
          :style="{ display: isOpen ? 'block' : 'none' }"
        >
          <div class="modal-dialog modal-lg">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title fw-bold" v-if="isDel">Hủy đơn hàng</h5>
                <h5 class="modal-title fw-bold" v-else-if="isUpdate">Cập nhật trạng thái đơn</h5>
                <button type="button" class="btn-close" @click.prevent="handleOpenModal"></button>
              </div>
              <div class="modal-body">
                <div class="fs-5 text-secondary" v-if="isDel">
                  Bạn có chắc muốn hủy đơn hàng của 
                  <strong class="text-danger">"{{ customerName }}"</strong> không?
                </div>
                
                <div class="p-3" v-else-if="isUpdate">
                  <form action="" @submit.prevent="handleSubmit">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Người nhận: </label>
                            <input type="text" class="form-control" v-model="nguoiNhan" />
                            <div class="text-danger">{{ errors.nguoiNhan }}</div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Số điện thoại: </label>
                            <input type="text" class="form-control" v-model="sdt" />
                            <div class="text-danger">{{ errors.sdt }}</div>
                        </div>
                    </div>
                    <div class="mb-3">
                      <label class="form-label">Địa chỉ giao: </label>
                      <textarea class="form-control" v-model="diaChiGiao"></textarea>
                      <div class="text-danger">{{ errors.diaChiGiao }}</div>
                    </div>
                    <div class="mb-3">
                      <label class="form-label">Trạng thái: </label>
                      <select class="form-select" v-model="trangThai">
                        <option value="Chờ xác nhận">Chờ xác nhận</option>
                        <option value="Đang giao">Đang giao</option>
                        <option value="Hoàn thành">Hoàn thành</option>
                        <option value="Đã hủy">Đã hủy</option>
                      </select>
                    </div>
                  </form>
                  <div class="text-danger">{{ errorMessage }}</div>
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" @click.prevent="handleOpenModal">Close</button>
                <button type="button" class="btn btn-danger" @click.prevent="confirmCancel" v-if="isDel">Xác nhận hủy</button>
                <button type="button" class="btn btn-primary" @click.prevent="handleConfirmUpdate" v-else-if="isUpdate">Cập nhật đơn</button>
              </div>
            </div>
          </div>
        </div>
        <div v-if="isOpen" class="modal-backdrop fade show"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useOrderStore } from '@/stores/useOrderStore.js'
import Table from '../Table.vue'
import { storeToRefs } from 'pinia'
import { onMounted, ref } from 'vue'
import * as yup from 'yup'
import { useForm } from 'vee-validate'
import { useNotificationStore } from '@/stores/useNotificationStore.js'

const notification = useNotificationStore()


const initialOrderList = {
  nguoiDat: 'Người đặt',
  thanhToan: 'Thanh toán',
  diaChiGiao: 'Địa chỉ giao',
  nguoiNhan: 'Người nhận',
  sdt: 'Sdt',
  cachThucThanhToan: 'Cách thức thanh toán',
  ngayTao: 'Ngày tạo',
  trangThai: 'Trạng thái',
  thaoTac: 'Thao tác',
}

const orderStore = useOrderStore()
const { orders, page, size, totalPages } = storeToRefs(orderStore)

onMounted(async () => {
  await orderStore.fetchAllOrders({ page: page.value, size: size.value })
})

const changePage = async (p) => {
  if (p < 1 || p > totalPages.value) return
  await orderStore.fetchAllOrders({ newPage: p, newSize: size.value })
}

// 2. Logic Modal (Consistent với Product/User)
const isOpen = ref(false)
const isDel = ref(false)
const isUpdate = ref(false)
const customerName = ref('')
const orderId = ref(null)

const handleOpenModal = () => {
  isOpen.value = !isOpen.value
}

const handleCancelOrder = (id, name) => {
  isDel.value = true
  isUpdate.value = false
  orderId.value = id
  customerName.value = name
  handleOpenModal()
}

// 3. Validation Schema
const schema = yup.object({
  nguoiNhan: yup.string().required('Tên người nhận không được để trống'),
  sdt: yup.string().required('SĐT không được để trống'),
  diaChiGiao: yup.string().required('Địa chỉ không được để trống'),
})

const { handleSubmit, errors, defineField, setValues } = useForm({
  validationSchema: schema,
})

const [nguoiNhan] = defineField('nguoiNhan')
const [sdt] = defineField('sdt')
const [diaChiGiao] = defineField('diaChiGiao')
const [trangThai] = defineField('trangThai')
const originalOrder = ref(null)

const handleUpdateOrder = (order) => {
  isUpdate.value = true
  isDel.value = false
  originalOrder.value = { ...order }

  setValues({
    nguoiNhan: order.nguoiNhan,
    sdt: order.sdt,
    diaChiGiao: order.diaChiGiao,
    trangThai: order.trangThai,
  })
  handleOpenModal()
}

const errorMessage = ref('')
const handleConfirmUpdate = handleSubmit(async (values) => {
  try {
    if (
      values.nguoiNhan === originalOrder.value.nguoiNhan &&
      values.trangThai === originalOrder.value.trangThai
    ) {
      errorMessage.value = 'Không có thay đổi nào'
      return
    }
    await orderStore.updateOrder(originalOrder.value.id, values)
    notification.notify('Cập nhật đơn hàng thành công', 'success')
    handleOpenModal()
  } catch (error) {
    notification.notify('Lỗi cập nhật đơn hàng', 'error')
  }
})

const confirmCancel = async () => {
    // Logic gọi store để hủy đơn
    notification.notify('Đã hủy đơn hàng thành công', 'success')
    handleOpenModal()
}
</script>
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
            placeholder="Tìm kiếm người dùng..."
          />
        </div>
      </div>
      <div class="w-20">
        <select class="form-select" aria-label="Default select example">
          <option selected>Open this select menu</option>
          <option value="1">One</option>
          <option value="2">Two</option>
          <option value="3">Three</option>
        </select>
      </div>
      <div class="w-20">
        <select class="form-select" aria-label="Default select example">
          <option selected>Open this select menu</option>
          <option value="1">One</option>
          <option value="2">Two</option>
          <option value="3">Three</option>
        </select>
      </div>
      <div class="w-20">
        <select class="form-select" aria-label="Default select example">
          <option selected>Open this select menu</option>
          <option value="1">One</option>
          <option value="2">Two</option>
          <option value="3">Three</option>
        </select>
      </div>
    </div>
    <div class="w-25 text-end">
      <button type="button" class="btn btn-primary w-50">Them nguoi dung</button>
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
              <div class="fs-5 fw-bold text-secondary">Tong nguoi dung</div>
              <div class="fs-2 fw-bold">1,250</div>
              <div class="fs-5 fw-bold text-secondary">Nguoi dung trong he thong</div>
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
              <div class="fs-5 fw-bold text-secondary">Sap bi khoa</div>
              <div class="fs-2 fw-bold">43</div>
              <div class="fs-5 fw-bold text-secondary">Nguoi dung</div>
            </div>
          </div>
        </div>
      </div>
      <div class="border border-opacity-50 rounded-4 border-danger w-100">
        <div class="m-4">
          <div class="d-flex gap-4">
            <div class="w-25 d-flex justify-content-center align-items-center">
              <div class="d-inline-block rounded-circle bg-danger p-4 h-75 w-75"></div>
            </div>
            <div class="w-75 d-flex flex-column gap-2 justify-content-center">
              <div class="fs-5 fw-bold text-secondary">Bi vo hieu</div>
              <div class="fs-2 fw-bold">12</div>
              <div class="fs-5 fw-bold text-secondary">Nguoi dung</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="mt-5 border rounded-4">
      <div class="m-4">
        <div class="text-start fw-bold fs-5 w-100">Danh sách người dùng</div>
        <div class="mt-3">
          <Table
            :initialValues="initialUserList"
            :interactive="true"
            :isBorder="true"
            :users="users"
            :handleActiveUser="handleActiveUser"
            :handleUpdateUser="handleUpdateUser"
          />
        </div>
        <div>
          <ul class="pagination justify-content-end">
            <li class="page-item" :class="{ disabled: page === 1 }">
              <button class="page-link" @click="changePage(page - 1)">Previous</button>
            </li>

            <li v-for="p in totalPages" :key="p" class="page-item" :class="{ active: page === p }">
              <button class="page-link" @click="changePage(p)">
                {{ p }}
              </button>
            </li>

            <li class="page-item" :class="{ disabled: page === totalPages }">
              <button class="page-link" @click="changePage(page + 1)">Next</button>
            </li>
          </ul>
        </div>
        <div
          class="modal fade"
          id="userModal"
          tabindex="-1"
          :class="{ show: isOpen }"
          :style="{ display: isOpen ? 'block' : 'none' }"
        >
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title fw-bold" v-if="isDel">Delete user</h5>
                <h5 class="modal-title fw-bold" v-else-if="isUpdate">Update user</h5>

                <button type="button" class="btn-close" @click.prevent="handleOpenModal"></button>
              </div>
              <div class="modal-body">
                <div class="fs-5 text-secondary" v-if="isDel">
                  Bạn chắc muốn {{ isActive ? 'khôi phục' : 'vô hiệu' }} người dùng
                  <strong class="text-danger">"{{ nameDel }}"</strong> không?
                </div>
                <div class="p-3" v-else-if="isUpdate">
                  <form action="" @submit.prevent="handleSubmit">
                    <div class="mb-3">
                      <label for="" class="form-label">Fullname: </label>
                      <input
                        type="text"
                        class="form-control"
                        v-model="fullName"
                        @focus="errorMessage = ''"
                      />
                      <div class="text-danger">{{ errors.fullName }}</div>
                    </div>
                    <div class="mb-3">
                      <label for="" class="form-label">Email: </label>
                      <input
                        type="text"
                        class="form-control"
                        v-model="email"
                        @focus="errorMessage = ''"
                      />
                      <div class="text-danger">{{ errors.email }}</div>
                    </div>
                    <div class="mb-3">
                      <label for="" class="form-label">Phone: </label>
                      <input
                        type="text"
                        class="form-control"
                        v-model="phone"
                        @focus="errorMessage = ''"
                      />
                      <div class="text-danger">{{ errors.phone }}</div>
                    </div>
                    <div class="mb-3">
                      <label for="" class="form-label">Birthday: </label>
                      <input
                        type="date"
                        class="form-control"
                        v-model="birthday"
                        @focus="errorMessage = ''"
                      />
                      <div class="text-danger">{{ errors.birthday }}</div>
                    </div>
                  </form>
                  <div class="text-danger">{{ errorMessage }}</div>
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" @click.prevent="handleOpenModal">
                  Close
                </button>
                <button
                  type="button"
                  class="btn btn-primary"
                  @click.prevent="handleActive"
                  v-if="isDel"
                >
                  {{ isActive ? 'Khôi phục' : 'Vô hiệu' }}
                </button>
                <button
                  type="button"
                  class="btn btn-primary"
                  @click.prevent="handleConfirmUpdate"
                  v-else-if="isUpdate"
                >
                  Cập nhật
                </button>
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
import { useUserStore } from '@/stores/useUserStore.js'
import Table from '../Table.vue'
import { storeToRefs } from 'pinia'
import { onMounted, ref } from 'vue'
import * as yup from 'yup'
import { useForm } from 'vee-validate'
import { useNotificationStore } from '@/stores/useNotificationStore.js'

const notification = useNotificationStore()

const initialUserList = {
  fullName: 'Người dùng',
  email: 'Email',
  phone: 'Số điện thoại',
  birthday: 'Ngày sinh',
  active: 'Trạng thái',
  thaoTac: 'Thao tác',
}

const userStore = useUserStore()

const { users, page, size, totalPages, totalElements } = storeToRefs(userStore)

onMounted(async () => {
  await userStore.fetchAllUsers({ page: page.value, size: size.value })
})

const changePage = async (p) => {
  if (p < 1 || p > totalPages.value) return
  await userStore.fetchAllUsers({ newPage: p, newSize: size.value })
}

// handle interactive
const modal = ref(null)
const isOpen = ref(false)
const isDel = ref(false)
const isUpdate = ref(false)

onMounted(() => {
  modal.value = document.getElementById('userModal')
})

const handleOpenModal = () => {
  isOpen.value = !isOpen.value
}

// handle del
const idDel = ref(null)
const nameDel = ref(null)
const isActive = ref(false)

const handleActiveUser = async (id, name, status) => {
  isDel.value = true
  isUpdate.value = false
  idDel.value = id
  nameDel.value = name
  isActive.value = status
  handleOpenModal()
}

const handleActive = async () => {
  if (isDel.value && idDel.value !== null) {
    try {
      await userStore.deleteUserById(idDel.value)
      isActive.value
        ? notification.notify(`Khôi phục thành công người dùng ${nameDel.value}`, 'success')
        : notification.notify(`Vô hiệu thành công người dùng ${nameDel.value}`, 'success')
    } catch (error) {
      isActive.value
        ? notification.notify(`Khôi phục thất bại người dùng ${nameDel.value}`, 'error')
        : notification.notify(`Vô hiệu thất bại người dùng ${nameDel.value}`, 'error')
    } finally {
      idDel.value = null
    }
  }
  handleOpenModal()
}

// handle upd
const schema = yup.object({
  fullName: yup.string().required('Họ và tên không được để trống'),
  email: yup.string().email('Email không đúng định dạng').required('Email không được để trống'),
  phone: yup
    .string()
    .matches(/^[0-9]{10}$/, 'Số điện thoại phải có 10 chữ số')
    .required('Vui lòng nhập số điện thoại'),
  birthday: yup.string().required('Vui lòng chọn ngày sinh'),
})

const { handleSubmit, errors, defineField, setValues } = useForm({
  validationSchema: schema,
})

const [fullName] = defineField('fullName')
const [email] = defineField('email')
const [phone] = defineField('phone')
const [birthday] = defineField('birthday')
const originalUser = ref(null)

const handleUpdateUser = (user) => {
  isUpdate.value = true
  isDel.value = false

  originalUser.value = { ...user }

  setValues({
    fullName: user.fullName,
    email: user.email,
    phone: user.phone,
    birthday: user.birthday,
  })
  handleOpenModal()
}

// Handle confirm form
const errorMessage = ref('')
const handleConfirmUpdate = handleSubmit(async (values) => {
  if (isUpdate.value) {
    try {
      const currentData = {
        fullName: values.fullName,
        email: values.email,
        phone: values.phone,
        birthday: values.birthday,
      }
      if (
        currentData.fullName === originalUser.value.fullName &&
        currentData.email === originalUser.value.email &&
        currentData.phone === originalUser.value.phone &&
        currentData.birthday === originalUser.value.birthday
      ) {
        errorMessage.value = 'Không có thay đổi'
        return
      }
      await userStore.updateUserById({ id: originalUser.value.id, ...currentData })

      notification.notify(`Cập nhật thành công người dùng ${currentData.fullName}`, 'success')
    } catch (error) {
      notification.notify(`Cập nhật thất bại người dùng ${values.fullName}`, 'error')
    }
  }
  handleOpenModal()
})
</script>
<template>
  <div class="d-flex justify-content-between">
    <div class="w-75 d-flex justify-content-between gap-3">
      <div class="w-40">
        <div class="input-group mb-3">
          <span class="input-group-text bg-white border-end-0" id="basic-addon1"
            ><i class="fa-solid fa-magnifying-glass"></i
          ></span>
          <input
            type="text"
            class="form-control border-start-0 fst-italic py-2"
            placeholder="Tìm kiếm sản phẩm..."
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
      <button type="button" class="btn btn-primary w-50">Them san pham</button>
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
              <div class="fs-5 fw-bold text-secondary">Tong san pham</div>
              <div class="fs-2 fw-bold">1,250</div>
              <div class="fs-5 fw-bold text-secondary">San pham trong kho</div>
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
              <div class="fs-5 fw-bold text-secondary">Sap het hang</div>
              <div class="fs-2 fw-bold">43</div>
              <div class="fs-5 fw-bold text-secondary">San pham</div>
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
              <div class="fs-5 fw-bold text-secondary">Het hang</div>
              <div class="fs-2 fw-bold">12</div>
              <div class="fs-5 fw-bold text-secondary">San pham</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="mt-5 border rounded-4">
      <div class="m-4">
        <div class="text-start fw-bold fs-5 w-100">Danh sach san pham</div>
        <div class="mt-3">
          <Table
            :initialValues="initialProductList"
            :interactive="true"
            :isBorder="true"
            :products="products"
            :handleActiveProduct="handleActiveProduct"
            :handleUpdateProduct="handleUpdateProduct"
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
          id="productModal"
          tabindex="-1"
          :class="{ show: isOpen }"
          :style="{ display: isOpen ? 'block' : 'none' }"
        >
          <div class="modal-dialog">
            <div class="modal-content">
              <div class="modal-header">
                <h5 class="modal-title fw-bold" v-if="isDel">Delete product</h5>
                <h5 class="modal-title fw-bold" v-else-if="isUpdate">Update product</h5>

                <button
                  type="button"
                  class="btn-close"
                  data-bs-dismiss="modal"
                  aria-label="Close"
                ></button>
              </div>
              <div class="modal-body">
                <div class="fs-5 text-secondary" v-if="isDel">
                  Bạn chắc muốn xóa sản phẩm
                  <strong class="text-danger">{{ nameDel }}</strong> không?
                </div>
                <div class="p-3" v-else-if="isUpdate">
                  <form action="" @submit.prevent="handleSubmit">
                    <div class="mb-3">
                      <label for="" class="form-label">Sản phẩm: </label>
                      <input type="text" class="form-control" v-model="form.name" />
                    </div>
                    <div class="mb-3">
                      <label for="" class="form-label">Hãng: </label>
                      <select class="form-select" v-model="form.brandID">
                        <option disabled value="">Chọn thương hiệu</option>
                        <option v-for="b in brands" :value="b.id" :key="b.id">{{ b.name }}</option>
                      </select>
                    </div>
                    <div class="mb-3">
                      <label for="" class="form-label">Loại: </label>
                      <select class="form-select" v-model="form.categoryID">
                        <option disabled value="">Chọn danh mục</option>
                        <option v-for="c in categories" :value="c.id" :key="c.id">
                          {{ c.name }}
                        </option>
                      </select>
                    </div>
                    <div class="mb-3">
                      <label for="" class="form-label">Giá: </label>
                      <input type="text" class="form-control" v-model="form.price" />
                    </div>
                  </form>
                </div>
              </div>
              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" @click.prevent="handleOpenModal">
                  Close
                </button>
                <button
                  type="button"
                  class="btn btn-primary"
                  @click.prevent="handleConfirm"
                  v-if="isDel"
                >
                  Xóa
                </button>
                <button
                  type="button"
                  class="btn btn-primary"
                  @click.prevent="handleConfirm"
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
import { useProductStore } from '@/stores/useProductStore.js'
import Table from '../Table.vue'
import { storeToRefs } from 'pinia'
import { onMounted, reactive, ref } from 'vue'
import { useBrandStore } from '@/stores/useBrandStore.js'
import { useCategoryStore } from '@/stores/useCategoryStore.js'

const initialProductList = {
  sanPham: 'Sản phẩm',
  danhMuc: 'Danh mục',
  hang: 'Hãng',
  giaBan: 'Giá bán',
  bienThe: 'Biến thể',
  trangThai: 'Trạng thái',
  ngayCapNhat: 'Ngày cập nhật',
  thaoTac: 'Thao tác',
}

const productStore = useProductStore()

const { products, page, size, totalPages, totalElements } = storeToRefs(productStore)

onMounted(async () => {
  await productStore.fetchAllProducts({ page: page.value, size: size.value })
})

const changePage = async (p) => {
  if (p < 1 || p > totalPages.value) return
  await productStore.fetchAllProducts({ newPage: p, newSize: size.value })
}

// handle interactive
const modal = ref(null)
const isOpen = ref(false)
const isDel = ref(false)
const isUpdate = ref(false)

onMounted(() => {
  modal.value = document.getElementById('productModal')
})

const handleOpenModal = () => {
  isOpen.value = !isOpen.value
}

// handel del
const idDel = ref(null)
const nameDel = ref(null)

const handleActiveProduct = async (id, name) => {
  isDel.value = true
  isUpdate.value = false
  idDel.value = id
  nameDel.value = name
  handleOpenModal()
}

// handel upd
const productUpdate = ref(null)

const brandStore = useBrandStore()
const categoryStore = useCategoryStore()

const { brands } = storeToRefs(brandStore)
const { categories } = storeToRefs(categoryStore)

onMounted(async () => {
  await Promise.all([categoryStore.fetchCategory(), brandStore.fetchBrand()])
})

const form = reactive({
  id: null,
  name: '',
  categoryID: null,
  brandID: null,
  price: null,
})

const handleUpdateProduct = (product) => {
  isUpdate.value = true
  isDel.value = false
  productUpdate.value = product

  const brand = brands.value.find((b) => b.name === productUpdate.value.brand)
  const category = categories.value.find((c) => c.name === productUpdate.value.category)
  console.log(brand.id)

  Object.assign(form, {
    id: productUpdate.value.id,
    name: productUpdate.value.name,
    categoryID: category?.id,
    brandID: brand?.id,
    price: productUpdate.value.price,
  })
  handleOpenModal()
}

const handleConfirm = async () => {
  if (isDel.value && idDel.value !== null) {
    try {
      await productStore.deleteProductById(idDel.value)
    } catch (error) {
    } finally {
      idDel.value = null
    }
  } else if (isUpdate.value) {
    try {
      console.log(form)
    } catch (error) {
    } finally {
      productUpdate.value = null
    }
  }
  handleOpenModal()
}
</script>

<template>
  <div class="d-flex justify-content-between">
    <div class="w-75 d-flex justify-content-between gap-3">
      <div class="w-40">
        <div class="input-group input-group-outline mb-3">
          <label class="form-label">Tìm kiếm sản phẩm</label>

          <input class="form-control me-2" type="search" aria-label="Search" />
        </div>
      </div>
      <div class="w-20">
        <Select
          :data="categories"
          v-model:selected="selectedCategory"
          :selectedName="selectedCategoryName"
          descript="Chọn danh mục"
        ></Select>
      </div>
      <div class="w-20">
        <Select
          :data="audiences"
          v-model:selected="selectedAudience"
          :selectedName="selectedAudienceName"
          descript="Chọn loại khách hàng"
        ></Select>
      </div>
      <div class="w-20">
        <Select
          :data="brands"
          v-model:selected="selectedBrand"
          :selectedName="selectedBrandName"
          descript="Chọn hãng"
        ></Select>
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
        <div class="text-start fw-bold fs-5 w-100">Danh sách sản phẩm</div>
        <div class="mt-3">
          <Table
            :initialValues="initialProductList"
            :interactive="true"
            :isBorder="true"
            :products="products"
            :handleActiveProduct="handleActiveProduct"
            :handleUpdateProduct="handleUpdateProduct"
            :message="message"
          />
        </div>
        <div v-if="!message">
          <ul class="pagination justify-content-end">
            <li class="page-item" :class="{ disabled: page === 1 }">
              <button class="page-link" @click="changePage(page - 1)"><</button>
            </li>

            <li
              v-for="p in totalPages"
              :key="p"
              class="page-item"
              :class="{ active: page === p, disabled: page === p }"
            >
              <button class="page-link" @click="changePage(p)">
                {{ p }}
              </button>
            </li>

            <li class="page-item" :class="{ disabled: page === totalPages }">
              <button class="page-link" @click="changePage(page + 1)">></button>
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

                <button type="button" class="btn-close" @click.prevent="handleOpenModal"></button>
              </div>
              <div class="modal-body">
                <div class="fs-5 text-secondary" v-if="isDel">
                  Bạn chắc muốn {{ isActive ? 'khôi phục' : 'vô hiệu' }} sản phẩm
                  <strong class="text-danger">"{{ nameDel }}"</strong> không?
                </div>
                <div class="p-3" v-else-if="isUpdate">
                  <form action="" @submit.prevent="handleSubmit">
                    <div class="mb-3">
                      <label for="" class="form-label">Sản phẩm: </label>
                      <input
                        type="text"
                        class="form-control"
                        v-model="name"
                        @focus="errorMessage = ''"
                      />
                      <div class="text-danger">{{ errors.name }}</div>
                    </div>
                    <div class="mb-3">
                      <label for="" class="form-label">Hãng: </label>
                      <select
                        class="form-select"
                        v-model.number="brandID"
                        @focus="errorMessage = ''"
                      >
                        <option disabled :value="null">Chọn thương hiệu</option>
                        <option v-for="b in brands" :value="b.id" :key="b.id">{{ b.name }}</option>
                      </select>
                      <div class="text-danger">{{ errors.brandID }}</div>
                    </div>
                    <div class="mb-3">
                      <label for="" class="form-label">Loại: </label>
                      <select
                        class="form-select"
                        v-model.number="categoryID"
                        @focus="errorMessage = ''"
                      >
                        <option disabled :value="null">Chọn danh mục</option>
                        <option v-for="c in categories" :value="c.id" :key="c.id">
                          {{ c.name }}
                        </option>
                      </select>
                      <div class="text-danger">{{ errors.categoryID }}</div>
                    </div>
                    <div class="mb-3">
                      <label for="" class="form-label">Giá: </label>
                      <input
                        type="text"
                        class="form-control"
                        v-model="price"
                        @focus="errorMessage = ''"
                      />
                      <div class="text-danger">{{ errors.price }}</div>
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
import { useProductStore } from '@/stores/useProductStore.js'
import Table from '../Table.vue'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref, watch } from 'vue'
import { useBrandStore } from '@/stores/useBrandStore.js'
import { useCategoryStore } from '@/stores/useCategoryStore.js'
import * as yup from 'yup'
import { useForm } from 'vee-validate'
import { useNotificationStore } from '@/stores/useNotificationStore.js'
import { useAudienceStore } from '@/stores/useAudienceStore.js'
import { useRoute, useRouter } from 'vue-router'
import Select from '../Select.vue'

const notification = useNotificationStore()

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

const { products, page, size, totalPages, totalElements, message } = storeToRefs(productStore)
// Handle filter admin

const route = useRoute()
const router = useRouter()

const selectedCategory = computed({
  get() {
    return route.query.categoryIds ?? undefined
  },

  set(value) {
    router.push({
      query: {
        ...route.query,
        categoryIds: value || undefined,
      },
    })
  },
})

const selectedCategoryName = computed(() => {
  const ids = Array.isArray(selectedCategory.value)
    ? selectedCategory.value
    : [selectedCategory.value]

  return (
    categories.value
      .filter((c) => ids.includes(String(c.id)))
      .map((c) => c.name)
      .join(', ') || 'Chọn danh mục'
  )
})

const selectedAudience = computed({
  get() {
    return route.query.audienceIds ?? undefined
  },

  set(value) {
    router.push({
      query: {
        ...route.query,
        audienceIds: value || undefined,
      },
    })
  },
})

const selectedAudienceName = computed(() => {
  const ids = Array.isArray(selectedAudience.value)
    ? selectedAudience.value
    : [selectedAudience.value]

  return (
    audiences.value
      .filter((a) => ids.includes(String(a.id)))
      .map((a) => a.name)
      .join(', ') || 'Chọn khách hàng'
  )
})

const selectedBrand = computed({
  get() {
    return route.query.brandIds ?? undefined
  },

  set(value) {
    router.push({
      query: {
        ...route.query,
        brandIds: value || undefined,
      },
    })
  },
})

const selectedBrandName = computed(() => {
  const ids = Array.isArray(selectedBrand.value) ? selectedBrand.value : [selectedBrand.value]

  return (
    brands.value
      .filter((b) => ids.includes(String(b.id)))
      .map((b) => b.name)
      .join(', ') || 'Chọn hãng'
  )
})

const search = ref('')

const audience = computed(() => route.query.audienceIds)
const brand = computed(() => route.query.brandIds)
const category = computed(() => route.query.categoryIds)

watch(
  () => route.query,
  async (query) => {
    const params = {
      brandIds: query.brandIds
        ? Array.isArray(query.brandIds)
          ? query.brandIds.map(Number)
          : [Number(query.brandIds)]
        : [],

      categoryIds: query.categoryIds
        ? Array.isArray(query.categoryIds)
          ? query.categoryIds.map(Number)
          : [Number(query.categoryIds)]
        : [],

      audienceIds: query.audienceIds
        ? Array.isArray(query.audienceIds)
          ? query.audienceIds.map(Number)
          : [Number(query.audienceIds)]
        : [],

      search: query.search || '',
    }
    await productStore.fetchAllProducts({
      newPage: 1,
      newSize: size.value,
      audienceIds: params.audienceIds,
      brandIds: params.brandIds,
      categoryIds: params.categoryIds,
    })
  },
  { immediate: true },
)

const changePage = async (p) => {
  if (p < 1 || p > totalPages.value) return
  await productStore.fetchAllProducts({
    newPage: p,
    newSize: size.value,
    audienceIds: audience.value,
    brandIds: brand.value,
    categoryIds: category.value,
  })
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
const isActive = ref(false)

const handleActiveProduct = async (id, name, status) => {
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
      await productStore.deleteProductById(idDel.value)
      isActive.value
        ? notification.notify(`Khôi phục thành công sản phẩm ${nameDel.value}`, 'success')
        : notification.notify(`Vô hiệu thành công sản phẩm ${nameDel.value}`, 'success')
    } catch (error) {
      isActive.value
        ? notification.notify(`Khôi phục thất bại sản phẩm ${nameDel.value}`, 'error')
        : notification.notify(`Vô hiệu thất bại sản phẩm ${nameDel.value}`, 'error')
    } finally {
      idDel.value = null
    }
  }
  handleOpenModal()
}

// handel upd

const brandStore = useBrandStore()
const categoryStore = useCategoryStore()
const audienceStore = useAudienceStore()

const { brands } = storeToRefs(brandStore)
const { categories } = storeToRefs(categoryStore)
const { audiences } = storeToRefs(audienceStore)

onMounted(async () => {
  await categoryStore.fetchCategory()
})

const schema = yup.object({
  name: yup.string().required('Tên sản phẩm không được để trống'),
  brandID: yup.number().required('Vui lòng chọn thương hiệu'),
  categoryID: yup.number().required('Vui lòng chọn danh mục'),
  price: yup
    .number()
    .typeError('Giá phải là số')
    .positive('Giá phải lớn hơn 0')
    .required('Vui lòng nhập giá'),
})

const { handleSubmit, errors, defineField, setValues } = useForm({
  validationSchema: schema,
})

const [name] = defineField('name')
const [brandID] = defineField('brandID')
const [categoryID] = defineField('categoryID')
const [price] = defineField('price')
const originalProduct = ref(null)

const handleUpdateProduct = (product) => {
  isUpdate.value = true
  isDel.value = false

  const brand = brands.value.find((b) => b.name === product.brand)
  const category = categories.value.find((c) => c.name === product.category)

  originalProduct.value = {
    id: product.id,
    name: product.name,
    brandID: brand?.id,
    categoryID: category?.id,
    price: product.price,
  }
  setValues(originalProduct.value)
  handleOpenModal()
}

// Handle confirm form
const errorMessage = ref('')
const handleConfirmUpdate = handleSubmit(async (values) => {
  if (isUpdate.value) {
    try {
      const currentData = {
        name: values.name,
        brandID: values.brandID,
        categoryID: values.categoryID,
        price: values.price,
      }
      if (
        currentData.name === originalProduct.value.name &&
        currentData.brandID === originalProduct.value.brandID &&
        currentData.categoryID === originalProduct.value.categoryID &&
        currentData.price === originalProduct.value.price
      ) {
        errorMessage.value = 'Không có thay đổi'
        return
      }
      await productStore.updateProductById({ id: originalProduct.value.id, ...currentData })

      notification.notify(`Cập nhật thành công sản phẩm ${currentData.name}`, 'success')
    } catch (error) {
      notification.notify(`Cập nhật thất bại sản phẩm ${currentData.name}`, 'error')
    }
  }
  handleOpenModal()
})
</script>

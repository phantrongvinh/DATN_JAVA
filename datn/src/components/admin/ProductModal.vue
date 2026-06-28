<template>
  <Transition name="fade">
    <div
      v-if="showProductModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      @click.self="closeModal"
    >
      <div class="flex max-h-[90vh] w-[1100px] flex-col overflow-hidden rounded bg-white shadow-xl">
        <!-- Header -->
        <div class="flex items-center justify-between border-b px-6 py-4 shrink-0">
          <h2 class="text-xl font-semibold">
            {{ product ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm' }}
          </h2>

          <button @click="closeModal" class="text-2xl text-gray-500 hover:text-black">×</button>
        </div>
        <form
          :validation-schema="validate"
          @submit.prevent="onSubmit"
          class="flex flex-col flex-1 overflow-hidden"
        >
          <!-- Body -->
          <div class="flex-1 overflow-y-auto p-6">
            <div id="productForm" class="space-y-8">
              <div class="grid grid-cols-2 gap-5">
                <!-- Name -->
                <div class="col-span-2">
                  <label class="mb-2 block text-sm font-medium"> Tên sản phẩm </label>

                  <Field
                    name="name"
                    v-model="name"
                    class="w-full rounded border border-border px-3 py-2 outline-none focus:border-black"
                  />

                  <ErrorMessage name="name" class="mt-1 block text-sm text-red-500" />
                </div>

                <!-- Brand -->
                <div>
                  <label class="mb-2 block text-sm font-medium"> Thương hiệu </label>

                  <Field
                    as="select"
                    name="brandId"
                    v-model="brandId"
                    class="w-full rounded border border-border px-3 py-2"
                  >
                    <option value="">Chọn thương hiệu</option>

                    <option v-for="brand in brands" :key="brand.id" :value="brand.id">
                      {{ brand.name }}
                    </option>
                  </Field>

                  <ErrorMessage name="brandId" class="mt-1 block text-sm text-red-500" />
                </div>

                <!-- Category -->
                <div>
                  <label class="mb-2 block text-sm font-medium"> Danh mục </label>

                  <Field
                    as="select"
                    name="categoryId"
                    v-model="categoryId"
                    class="w-full rounded border border-border px-3 py-2"
                  >
                    <option value="">Chọn danh mục</option>

                    <option v-for="category in categories" :key="category.id" :value="category.id">
                      {{ category.name }}
                    </option>
                  </Field>

                  <ErrorMessage name="categoryId" class="mt-1 block text-sm text-red-500" />
                </div>

                <!-- Target -->
                <div>
                  <label class="mb-2 block text-sm font-medium"> Đối tượng </label>

                  <Field
                    as="select"
                    name="targetAudienceId"
                    v-model="targetAudienceId"
                    class="w-full rounded border border-border px-3 py-2"
                  >
                    <option value="">Chọn đối tượng</option>

                    <option v-for="audience in audiences" :key="audience.id" :value="audience.id">
                      {{ audience.name }}
                    </option>
                  </Field>

                  <ErrorMessage name="targetAudienceId" class="mt-1 block text-sm text-red-500" />
                </div>

                <!-- Price -->
                <div>
                  <label class="mb-2 block text-sm font-medium"> Giá gốc </label>

                  <Field
                    name="basePrice"
                    type="number"
                    v-model="basePrice"
                    class="w-full rounded border border-border px-3 py-2"
                  />

                  <ErrorMessage name="basePrice" class="mt-1 block text-sm text-red-500" />
                </div>

                <!-- Description -->
                <div class="col-span-2">
                  <label class="mb-2 block text-sm font-medium"> Mô tả </label>

                  <Field
                    as="textarea"
                    rows="5"
                    name="description"
                    v-model="description"
                    class="w-full rounded border border-border px-3 py-2"
                  />

                  <ErrorMessage name="description" class="mt-1 block text-sm text-red-500" />
                </div>
                <!-- Images -->
                <div class="col-span-2">
                  <div class="mb-3 flex items-center justify-between">
                    <h3
                      class="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground"
                    >
                      Hình ảnh sản phẩm
                    </h3>

                    <label
                      class="flex cursor-pointer items-center gap-2 border border-border px-3 py-2 text-xs transition hover:bg-secondary"
                    >
                      <Plus class="h-3.5 w-3.5" />
                      Thêm ảnh

                      <input
                        hidden
                        multiple
                        type="file"
                        accept="image/*"
                        @change="handleImageUpload"
                      />
                    </label>
                  </div>

                  <!-- Empty -->
                  <div
                    v-if="previewImages.length === 0"
                    class="rounded border border-dashed border-border py-12 text-center text-sm text-muted-foreground"
                  >
                    Chưa có hình ảnh nào
                  </div>

                  <!-- List -->
                  <div v-else class="grid grid-cols-2 gap-4 md:grid-cols-3 lg:grid-cols-5">
                    <div
                      v-for="(img, index) in previewImages"
                      :key="index"
                      class="group relative overflow-hidden rounded border border-border bg-gray-100"
                    >
                      <img
                        :src="img"
                        class="h-48 w-full object-cover transition duration-300 group-hover:scale-105"
                      />

                      <!-- overlay -->
                      <div
                        class="absolute inset-0 bg-black/20 opacity-0 transition group-hover:opacity-100"
                      />

                      <!-- Primary -->
                      <button
                        type="button"
                        @click="setPrimary(index)"
                        class="absolute left-2 top-2 rounded-full bg-white p-2 shadow"
                      >
                        <Star
                          v-if="primaryImageIndex === index"
                          class="h-4 w-4 fill-yellow-400 text-yellow-400"
                        />

                        <StarOff v-else class="h-4 w-4" />
                      </button>

                      <!-- Delete -->
                      <button
                        type="button"
                        @click="removeImage(index)"
                        class="absolute right-2 top-2 rounded-full bg-red-500 p-2 text-white transition hover:bg-red-600"
                      >
                        <Trash2 class="h-4 w-4" />
                      </button>

                      <!-- Badge -->
                      <div
                        v-if="primaryImageIndex === index"
                        class="absolute bottom-2 left-2 rounded bg-yellow-400 px-2 py-1 text-[10px] font-semibold uppercase text-black"
                      >
                        Ảnh chính
                      </div>
                    </div>
                  </div>
                </div>
                <!-- Variant -->
                <div class="col-span-2 mt-8">
                  <div class="mb-3 flex items-center justify-between">
                    <h3
                      class="text-[11px] font-semibold uppercase tracking-widest text-muted-foreground"
                    >
                      Biến thể sản phẩm
                    </h3>

                    <button
                      type="button"
                      @click="addVariant"
                      class="flex items-center gap-2 border border-border px-3 py-2 text-xs hover:bg-secondary"
                    >
                      <Plus class="h-3.5 w-3.5" />
                      Thêm biến thể
                    </button>
                  </div>

                  <div
                    v-if="variants.length === 0"
                    class="border border-dashed border-border py-8 text-center text-sm text-muted-foreground"
                  >
                    Chưa có biến thể
                  </div>

                  <div class="space-y-4">
                    <div
                      v-for="(variant, index) in variants"
                      :key="index"
                      class="rounded border border-border p-4"
                    >
                      <div class="grid grid-cols-12 gap-3">
                        <!-- Color -->

                        <div class="col-span-3">
                          <label class="mb-1 block text-xs"> Màu </label>

                          <input
                            v-model="variant.color"
                            class="w-full border border-border p-2 outline-none focus:border-foreground"
                          />
                        </div>

                        <!-- Size -->

                        <div class="col-span-2">
                          <label class="mb-1 block text-xs"> Size </label>

                          <select
                            v-model="variant.sizeId"
                            class="w-full border border-border p-2 outline-none focus:border-foreground"
                          >
                            <option value="">Chọn</option>

                            <option v-for="size in sizes" :key="size.id" :value="size.id">
                              {{ size.name }}
                            </option>
                          </select>
                        </div>

                        <!-- Stock -->

                        <div class="col-span-2">
                          <label class="mb-1 block text-xs"> Tồn kho </label>

                          <input
                            type="number"
                            v-model.number="variant.stock"
                            class="w-full border border-border p-2"
                          />
                        </div>

                        <!-- Price -->
                        <div class="col-span-2">
                          <label class="mb-1 block text-xs"> Giá </label>
                          <input
                            type="number"
                            v-model.number="variant.price"
                            class="w-full border border-border p-2"
                          />
                        </div>

                        <!-- SKU -->
                        <div class="col-span-4">
                          <label class="mb-1 block text-xs"> SKU </label>

                          <input v-model="variant.sku" class="w-full border border-border p-2" />
                        </div>

                        <!-- Delete -->
                        <div class="col-span-1 flex items-end">
                          <button
                            type="button"
                            @click="removeVariant(index)"
                            class="flex h-10 w-10 items-center justify-center border border-red-300 text-red-500 hover:bg-red-50"
                          >
                            <Trash2 class="h-4 w-4" />
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div class="flex justify-end gap-3 border-t px-6 py-4 shrink-0">
            <button type="button" :disabled="loadding" class="border px-6 py-2" @click="closeModal">
              Huỷ
            </button>

            <button type="submit" class="bg-ink px-6 py-2 text-white disabled:opacity-50">
              {{ loadding ? 'Đang lưu...' : product ? 'Cập nhật' : 'Thêm sản phẩm' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </Transition>
</template>
<script setup>
import { ref, watch, computed, onMounted } from 'vue'
import { Form, Field, ErrorMessage, useForm, FormContextKey } from 'vee-validate'
import * as yup from 'yup'
import { useProductStore } from '@/stores/useProductStore'
import { Star, StarOff, Trash2, Plus } from 'lucide-vue-next'
import { useBrandStore } from '@/stores/useBrandStore'
import { useAudienceStore } from '@/stores/useAudienceStore'
import { useCategoryStore } from '@/stores/useCategoryStore'
import { storeToRefs } from 'pinia'
import { useSizeStore } from '@/stores/useSizeStore'

const props = defineProps({
  showProductModal: {
    type: Boolean,
    default: false,
  },
  product: {
    type: Object,
    default: null,
  },

  brands: {
    type: Array,
    default: () => [],
  },

  categories: {
    type: Array,
    default: () => [],
  },

  targetAudiences: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['update:showProductModal', 'save'])

const closeModal = () => {
  emit('update:showProductModal', false)
}

// fetch data
const brandStore = useBrandStore()
const audienceStore = useAudienceStore()
const categoryStore = useCategoryStore()
const sizeStore = useSizeStore()

const brands = computed(() => brandStore.brands)
const categories = computed(() => categoryStore.categories)
const audiences = computed(() => audienceStore.audiences)
const sizes = computed(() => sizeStore.sizes)

onMounted(async () => {
  await Promise.all([
    brandStore.fetchBrand(),
    audienceStore.fetchAudiences(),
    categoryStore.fetchCategory(),
    sizeStore.fetchSize(),
  ])
})

// Validate

const validate = yup.object({
  name: yup.string().required('Tên sản phẩm là bắt buộc'),

  description: yup.string(),

  basePrice: yup.number().typeError('Giá phải là số').required('Giá là bắt buộc'),

  brandId: yup.number().typeError('Chọn thương hiệu').required('Chọn thưởng hiệu'),

  categoryId: yup.number().typeError('Chọn danh mục').required('Chọn danh mục'),

  targetAudienceId: yup.number().typeError('Chọn đối tượng').required('Chọn đối tượng'),
})

const { handleSubmit, defineField, resetForm } = useForm({
  validationSchema: validate,
})

const [name] = defineField('name')
const [description] = defineField('description')
const [basePrice] = defineField('basePrice')
const [brandId] = defineField('brandId')
const [targetAudienceId] = defineField('targetAudienceId')
const [categoryId] = defineField('categoryId')

//handle variants
const variants = ref([])
const addVariant = () => {
  variants.value.push({
    id: null,
    color: '',
    sizeId: '',
    stock: 0,
    price: 0,
    sku: '',
  })
}
const removeVariant = (index) => {
  variants.value.splice(index, 1)
}

const existingImageUrls = ref([])
const initialValues = {
  name: '',
  description: '',
  basePrice: '',
  brandId: '',
  categoryId: '',
  targetAudienceId: '',
}
watch(
  () => props.showProductModal,
  (show) => {
    if (!show) return

    if (props.product) {
      resetForm({
        values: {
          name: props.product.name ?? '',
          description: props.product.description ?? '',
          basePrice: props.product.basePrice ?? '',
          brandId: props.product.brandId ?? '',
          categoryId: props.product.categoryId ?? '',
          targetAudienceId: props.product.targetAudienceId ?? '',
        },
      })

      // load variants

      variants.value = props.product.productVariant
        ? props.product.productVariant.map((v) => ({
            id: v.id,
            sizeId: v.sizeId,
            color: v.color,
            stock: v.stock,
            sku: v.sku,
            price: v.price,
          }))
        : []

      // load ảnh
      previewImages.value = props.product.imgs ? props.product.imgs.map((img) => img.imageUrl) : []
      existingImageUrls.value = [...previewImages.value]
      images.value = []
    } else {
      resetForm({
        values: initialValues,
      })

      variants.value = []

      previewImages.value = []

      images.value = []
    }
  },
  {
    immediate: true,
  },
)

// =======================
// Images
// =======================

const images = ref([])
const previewImages = ref([])
const primaryImageIndex = ref(0)

const setPrimary = (index) => {
  primaryImageIndex.value = index
}

const handleImageUpload = (e) => {
  const files = [...e.target.files]

  files.forEach((file) => {
    images.value.push(file)

    previewImages.value.push(URL.createObjectURL(file))
  })

  e.target.value = ''
}
const removeImage = (index) => {
  previewImages.value.splice(index, 1)

  if (index < existingImageUrls.value.length) {
    existingImageUrls.value.splice(index, 1)
  } else {
    images.value.splice(index - existingImageUrls.value.length, 1)
  }

  if (primaryImageIndex.value >= previewImages.value.length) {
    primaryImageIndex.value = 0
  }
}

// handle save

const productStore = useProductStore()

const { loadding } = storeToRefs(productStore)

const onSubmit = handleSubmit(async (values) => {
  const payload = {
    name: values.name,
    description: values.description,
    basePrice: values.basePrice,
    brandId: values.brandId,
    categoryId: values.categoryId,
    targetAudienceId: values.targetAudienceId,
    variants: variants.value,
    images: [
      // Ảnh cũ
      ...existingImageUrls.value.map((url, i) => ({
        id: props.product?.imgs[i]?.id ?? null,
        imageUrl: url,
        isPrimary: i === primaryImageIndex.value,
      })),
      // Ảnh mới
      ...images.value.map((_, i) => ({
        id: null,
        imageUrl: null,
        isPrimary: existingImageUrls.value.length + i === primaryImageIndex.value,
      })),
    ],
  }

  const formData = new FormData()
  formData.append('data', JSON.stringify(payload))

  // Edit
  if (props.product?.id) {
    // Ảnh cũ giữ lại không gửi file, chỉ gửi ảnh mới
    images.value.forEach((file) => formData.append('images', file))
    await productStore.updateProductById(props.product.id, formData)
  }
  // Add
  else {
    images.value.forEach((file) => formData.append('images', file))
    await productStore.createProduct(formData)
  }

  closeModal()

  resetModal()
})

const resetModal = () => {
  resetForm({ values: initialValues })
  variants.value = []
  images.value = []
  previewImages.value = []
  existingImageUrls.value = []
  primaryImageIndex.value = 0
}
</script>

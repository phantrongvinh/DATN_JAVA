<template>
  <!-- Content -->
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Quản lý hàng hóa</h1>
    </header>

    <main class="p-6">
      <div class="border border-border bg-background">
        <!-- Header -->
        <div class="space-y-4 border-b border-border p-4">
          <!-- Search -->
          <div class="relative">
            <Search
              class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
            />
            <input
              type="text"
              v-model="filter.search"
              placeholder="Tìm sản phẩm..."
              class="w-full rounded border border-border bg-background py-2 pl-10 pr-3 text-sm outline-none"
            />
          </div>
          <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
            <div class="flex flex-col gap-3 md:flex-row md:items-center md:flex-wrap">
              <select
                v-model="filter.audienceId"
                class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
              >
                <option value="">Tất cả đối tượng</option>
                <option v-for="a in audiences" :key="a.id" :value="a.id">{{ a.name }}</option>
              </select>

              <select
                v-model="filter.brandId"
                class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
              >
                <option value="">Tất cả thương hiệu</option>
                <option v-for="b in brands" :key="b.id" :value="b.id">{{ b.name }}</option>
              </select>

              <select
                v-model="filter.categoryId"
                class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
              >
                <option value="">Tất cả danh mục</option>
                <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
              </select>

              <!-- Sale -->
              <select
                v-model="filter.onSale"
                class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
              >
                <option :value="null">Tất cả</option>
                <option :value="true">Đang sale</option>
                <option :value="false">Không sale</option>
              </select>

              <!-- Sort -->
              <select
                v-model="filter.sortBy"
                class="border border-border bg-background px-3 py-2 text-sm outline-none focus:border-foreground"
              >
                <option value="">Sắp xếp mặc định</option>
                <option value="price_asc">Giá tăng dần</option>
                <option value="price_desc">Giá giảm dần</option>
                <option value="newest">Mới nhất</option>
              </select>

              <div class="flex flex-wrap items-center gap-3">
                <input
                  v-model="filter.minPrice"
                  type="number"
                  placeholder="Giá từ"
                  class="w-32 rounded border border-border p-2 text-sm"
                />
                <span class="text-muted-foreground">—</span>
                <input
                  v-model="filter.maxPrice"
                  type="number"
                  placeholder="Giá đến"
                  class="w-32 rounded border border-border p-2 text-sm"
                />

                <button
                  @click="resetFilter"
                  class="flex items-center gap-2 rounded border border-border px-3 py-2 text-sm hover:bg-secondary"
                >
                  <RotateCcw class="h-4 w-4" /> Đặt lại
                </button>
              </div>
            </div>
            <button
              @click="openCreateModal"
              class="flex items-center justify-center gap-2 bg-ink px-4 py-2 text-sm text-white hover:bg-ink/90"
            >
              <Plus class="h-4 w-4" />
              Thêm sản phẩm
            </button>
          </div>

          <div
            v-if="selectedProducts.length"
            class="flex items-center justify-between border-b border-border bg-secondary/40 px-4 py-3 gap-3"
          >
            <div class="flex items-center gap-3">
              <span
                class="flex h-8 w-8 items-center justify-center rounded-full bg-ink text-xs font-semibold text-ivory"
              >
                {{ selectedProducts.length }}
              </span>

              <div>
                <p class="text-sm font-medium">Đã chọn {{ selectedProducts.length }} sản phẩm</p>
                <p class="text-xs text-muted-foreground">Thực hiện thao tác hàng loạt</p>
              </div>
            </div>

            <div class="flex items-center gap-2">
              <button
                class="border border-border px-4 py-2 text-xs uppercase tracking-widest hover:bg-secondary"
                @click="selectedProducts = []"
              >
                Bỏ chọn
              </button>

              <button
                class="bg-ink px-4 py-2 text-xs uppercase tracking-widest text-ivory hover:bg-ink/90"
                @click="showPromotionModal = true"
              >
                Thêm khuyến mãi
              </button>
            </div>
          </div>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto">
          <table class="w-full text-sm">
            <thead>
              <tr
                class="border-b border-border bg-secondary/40 text-left text-[11px] uppercase tracking-widest text-muted-foreground"
              >
                <th class="w-12 px-4 py-3">
                  <input type="checkbox" :checked="isAllSelected" @change="toggleSelectAll" />
                </th>
                <th class="px-4 py-3">Sản phẩm</th>
                <th>Thương hiệu</th>
                <th>Danh mục</th>
                <th>Giá</th>
                <th>Tồn kho</th>
                <th>Trạng thái</th>
                <th class="pr-4 text-right">Thao tác</th>
              </tr>
            </thead>

            <tbody>
              <tr
                v-for="p in products"
                :key="p.id"
                class="border-b border-border last:border-0 hover:bg-secondary/30"
              >
                <td class="px-4">
                  <input type="checkbox" :value="p.id" v-model="selectedProducts" />
                </td>
                <td class="px-4 py-3">
                  <div class="flex items-center gap-3">
                    <img :src="imagePrimary(p.imgs)" :alt="p.name" class="h-10 w-10 object-cover" />

                    <span class="line-clamp-1 font-medium">
                      {{ p.name }}
                    </span>
                  </div>
                </td>

                <td>{{ p.brand }}</td>

                <td>{{ p.category }}</td>

                <td>
                  {{ ulti.formatVND(p.discountedPrice) }}

                  <span
                    v-if="p.promotion"
                    class="ml-2 text-[10px] text-muted-foreground line-through"
                  >
                    {{ ulti.formatVND(p.basePrice) }}
                  </span>
                </td>

                <td>{{ p.productVariant.reduce((sum, item) => sum + item.stock, 0) }}</td>

                <td>
                  <span
                    v-if="p.deletedAt"
                    class="inline-block bg-red-100 px-2 py-0.5 text-[10px] uppercase tracking-widest text-red-800"
                  >
                    Deactive
                  </span>
                  <span
                    v-else-if="p.promotion"
                    class="inline-block bg-gold-soft px-2 py-0.5 text-[10px] uppercase tracking-widest text-ink"
                  >
                    Sale
                  </span>

                  <span
                    v-else
                    class="inline-block bg-emerald-100 px-2 py-0.5 text-[10px] uppercase tracking-widest text-emerald-800"
                  >
                    Active
                  </span>
                </td>

                <td class="pr-4">
                  <div class="flex justify-end gap-2">
                    <button
                      class="border border-border p-1.5 hover:bg-secondary"
                      @click="openEditModal(p)"
                    >
                      <Pencil class="h-3.5 w-3.5" />
                    </button>

                    <button
                      class="border border-border p-1.5 text-destructive hover:bg-secondary"
                      @click="handleDelete(p.id)"
                    >
                      <Trash2 class="h-3.5 w-3.5" />
                    </button>
                  </div>
                </td>
              </tr>

              <tr v-if="products.length === 0">
                <td colspan="7" class="py-8 text-center text-muted-foreground">
                  Không tìm thấy sản phẩm.
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Pagination -->
        <div
          v-if="totalPages > 1"
          class="flex items-center justify-between border-t border-border px-6 py-4"
        >
          <div class="text-sm text-gray-500">
            Hiển thị
            {{ (currentPage - 1) * size + 1 }}
            -
            {{ Math.min(currentPage * size, totalElements) }}
            /
            {{ totalElements }}
            sản phẩm
          </div>

          <div class="flex items-center gap-2">
            <button
              class="rounded border px-3 py-2 disabled:opacity-50"
              :disabled="currentPage === 1"
              @click="changePage(currentPage - 1)"
            >
              Trước
            </button>

            <button
              v-for="page in totalPages"
              :key="page"
              @click="changePage(page)"
              class="h-9 w-9 rounded border text-sm"
              :class="currentPage === page ? 'bg-black text-white' : 'hover:bg-gray-100'"
            >
              {{ page }}
            </button>

            <button
              class="rounded border px-3 py-2 disabled:opacity-50"
              :disabled="currentPage === totalPages"
              @click="changePage(currentPage + 1)"
            >
              Sau
            </button>
          </div>
        </div>
        <div
          v-if="isAllCurrentPageSelected && !selectAllPages"
          class="border-t bg-secondary/40 px-4 py-3 text-sm"
        >
          Đã chọn {{ selectedProducts.length }} sản phẩm trên trang này.

          <button class="ml-2 font-medium underline" @click="selectAllProducts">
            Chọn tất cả {{ totalProducts }} sản phẩm
          </button>
        </div>
      </div>
    </main>
  </div>
  <Transition name="fade">
    <div
      v-if="showPromotionModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm"
    >
      <div class="w-full max-w-4xl rounded border border-border bg-background shadow-2xl">
        <!-- Header -->
        <div class="border-b border-border px-6 py-5">
          <h2 class="font-display text-2xl">Thêm khuyến mãi</h2>
          <p class="mt-1 text-xs uppercase tracking-widest text-muted-foreground">
            Chọn khuyến mãi hiện có
          </p>
        </div>

        <!-- Body -->
        <div class="max-h-[70vh] overflow-y-auto p-6">
          <!-- Existing promotions -->
          <h3 class="mb-3 font-medium">Khuyến mãi hiện có</h3>

          <div class="overflow-hidden rounded border border-border">
            <label
              v-for="promotion in activePromotions"
              :key="promotion.id"
              class="flex cursor-pointer items-center justify-between border-b border-border px-4 py-4 last:border-b-0 hover:bg-secondary/40"
            >
              <div class="flex items-center gap-4">
                <input type="radio" v-model="selectedPromotionId" :value="promotion.id" />

                <div>
                  <p class="font-medium">
                    {{ promotion.name }}
                  </p>

                  <p class="mt-1 text-xs text-muted-foreground">
                    {{
                      promotion.discountType === 'PERCENT'
                        ? promotion.discountValue + '%'
                        : ulti.formatVND(promotion.discountValue)
                    }}
                  </p>
                </div>
              </div>

              <div class="text-right text-xs text-muted-foreground">
                <div>{{ promotion.startDate }}</div>
                <div>{{ promotion.endDate }}</div>
              </div>
            </label>
          </div>

          <!-- Divider -->
          <!-- <div class="my-8 flex items-center">
            <div class="h-px flex-1 bg-border"></div>

            <span class="mx-5 text-[11px] uppercase tracking-[0.3em] text-muted-foreground">
              Hoặc tạo mới
            </span>

            <div class="h-px flex-1 bg-border"></div>
          </div> -->

          <!-- Create Promotion -->
          <!-- <div class="grid grid-cols-2 gap-5">
            <div class="col-span-2">
              <label class="text-sm font-medium"> Tên khuyến mãi </label>

              <input
                v-model="name"
                class="mt-2 w-full border border-border bg-background px-3 py-2 outline-none focus:border-foreground"
              />
              <p class="mt-1 text-xs text-red-500">
                {{ errors.name }}
              </p>
            </div>

            <div>
              <label class="text-sm font-medium"> Loại khuyến mãi </label>

              <select
                v-model="discountType"
                class="mt-2 w-full border border-border bg-background px-3 py-2"
              >
                <option value="PERCENT">Giảm theo %</option>

                <option value="FIXED">Giảm theo tiền</option>
              </select>
              <p class="mt-1 text-xs text-red-500">
                {{ errors.discountType }}
              </p>
            </div>

            <div>
              <label class="text-sm font-medium"> Giá trị </label>

              <input
                type="number"
                v-model="discountValue"
                class="mt-2 w-full border border-border bg-background px-3 py-2"
              />
              <p class="mt-1 text-xs text-red-500">
                {{ errors.discountValue }}
              </p>
            </div>

            <div>
              <label class="text-sm font-medium"> Ngày bắt đầu </label>

              <VueDatePicker
                type="date"
                v-model="startDate"
                model-type="yyyy-MM-dd"
                :enable-time-picker="false"
                format="dd/MM/yyyy"
                class="mt-2 w-full border border-border bg-background px-3 py-2"
              />
              <p class="mt-1 text-xs text-red-500">
                {{ errors.startDate }}
              </p>
            </div>

            <div>
              <label class="text-sm font-medium"> Ngày kết thúc </label>

              <VueDatePicker
                type="date"
                v-model="endDate"
                model-type="yyyy-MM-dd"
                :enable-time-picker="false"
                format="dd/MM/yyyy"
                class="mt-2 w-full border border-border bg-background px-3 py-2"
              />
              <p class="mt-1 text-xs text-red-500">
                {{ errors.endDate }}
              </p>
            </div>
          </div> -->
        </div>

        <!-- Footer -->
        <div class="flex justify-end gap-3 border-t border-border bg-secondary/20 px-6 py-4">
          <button
            class="border border-border px-5 py-2 hover:bg-secondary"
            @click="showPromotionModal = false"
          >
            Huỷ
          </button>

          <button class="bg-ink px-6 py-2 text-ivory hover:bg-ink/90" @click="handleAddPromotion">
            Lưu & Áp dụng
          </button>
        </div>
      </div>
    </div>
  </Transition>

  <ProductModal
    v-model:showProductModal="showProductModal"
    :product="editingProduct"
    @save="handleSave"
  ></ProductModal>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Search, Pencil, Trash2, Plus, RotateCcw } from 'lucide-vue-next'

import ulti from '@/ulti/ulti'
import { useProductStore } from '@/stores/useProductStore'
import { storeToRefs } from 'pinia'
import '@vuepic/vue-datepicker/dist/main.css'
import ProductModal from '@/components/admin/ProductModal.vue'
import { usePromotionStore } from '@/stores/usePromotionStore'
import { useDebounce } from '@/composables/useDebounce'
import { useAudienceStore } from '@/stores/useAudienceStore'
import { useBrandStore } from '@/stores/useBrandStore'
import { useCategoryStore } from '@/stores/useCategoryStore'
import { useNotificationStore } from '@/stores/useNotificationStore'

const productStore = useProductStore()
const audienceStore = useAudienceStore()
const brandStore = useBrandStore()
const categoryStore = useCategoryStore()
const promotionStore = usePromotionStore()
const notification = useNotificationStore()

const { products, currentPage, totalPages, totalElements, size } = storeToRefs(productStore)
const { audiences } = storeToRefs(audienceStore)
const { brands } = storeToRefs(brandStore)
const { categories } = storeToRefs(categoryStore)
const { activePromotions } = storeToRefs(promotionStore)

onMounted(async () => {
  await Promise.allSettled([
    productStore.fetchAllProducts({ newPage: 1, newSize: 6 }),
    audienceStore.fetchAudiences(),
    brandStore.fetchBrand(),
    categoryStore.fetchCategory(),
    promotionStore.fetchActivePromotion(),
  ])
  console.log(products.value)
})

// handle filter
const filter = ref({
  search: '',
  audienceId: '',
  brandId: '',
  categoryId: '',
  onSale: null,
  minPrice: '',
  maxPrice: '',
  sortBy: '',
})

const buildParams = (page = 1) => ({
  newPage: page,
  newSize: size.value,
  ...filter.value,
  audienceId: filter.value.audienceId || undefined,
  brandId: filter.value.brandId || undefined,
  categoryId: filter.value.categoryId || undefined,
  minPrice: filter.value.minPrice || undefined,
  maxPrice: filter.value.maxPrice || undefined,
  onSale: filter.value.onSale === null ? undefined : filter.value.onSale,
})

const applyFilter = () => productStore.fetchAllProducts(buildParams(1))

const debouncedSearch = useDebounce(
  computed(() => filter.value.search),
  500,
)
watch(debouncedSearch, applyFilter)

const debouncedMinPrice = useDebounce(
  computed(() => filter.value.minPrice),
  600,
)
const debouncedMaxPrice = useDebounce(
  computed(() => filter.value.maxPrice),
  600,
)
watch([debouncedMinPrice, debouncedMaxPrice], applyFilter)

watch(
  () => [
    filter.value.audienceId,
    filter.value.brandId,
    filter.value.categoryId,
    filter.value.onSale,
    filter.value.sortBy,
  ],
  applyFilter,
)

const resetFilter = () => {
  filter.value = {
    search: '',
    audienceId: '',
    brandId: '',
    categoryId: '',
    onSale: null,
    minPrice: '',
    maxPrice: '',
    sortBy: '',
  }
  notification.notify('Đặt lại bộ lọc', 'success')
}

// handle pagination
const changePage = async (page) => {
  if (page < 1 || page > totalPages.value) return
  await productStore.fetchAllProducts(buildParams(page))
}

// handle image
const imagePrimary = (imgs) => {
  return imgs?.find((i) => i.primary)?.imageUrl ?? ''
}

// handle select product
const totalProducts = computed(() => totalElements.value)
const selectedProducts = ref([])
const selectAllPages = ref(false)

const isAllCurrentPageSelected = computed(() => {
  if (!products.value.length) return false

  return products.value.every((p) => selectedProducts.value.includes(p.id))
})

const isAllSelected = computed(() => {
  return isAllCurrentPageSelected.value
})

const toggleSelectAll = (e) => {
  if (e.target.checked) {
    products.value.forEach((p) => {
      if (!selectedProducts.value.includes(p.id)) {
        selectedProducts.value.push(p.id)
      }
    })
  } else {
    selectedProducts.value = selectedProducts.value.filter(
      (id) => !products.value.some((p) => p.id === id),
    )

    selectAllPages.value = false
  }
}

const selectAllProducts = () => {
  selectAllPages.value = true
}

// hanle thêm khuyến mãi vào sản phẩm hoặc tạo mới khuyến mãi
const showPromotionModal = ref(false)

const selectedPromotionId = ref(null)

const handleAddPromotion = async () => {
  if (!selectedPromotionId.value || selectedProducts.value.length === 0) {
    notification.notify('Chưa chọn chương trình khuyến mãi', 'error')
    return
  } else {
    showPromotionModal.value = false

    await promotionStore.addPromotionToProduct(selectedPromotionId.value, selectedProducts.value)

    selectedProducts.value = []
    selectedPromotionId.value = null
    notification.notify('Thêm khuyến mãi cho sản phẩm thành công', 'success')

    await productStore.fetchAllProducts({ newPage: 1, newSize: size.value })
  }
}

// handle deative product
const handleDelete = async (id) => {
  await productStore.deactivateProduct(id)
}

// handle thêm và sửa product
const editingProduct = ref(null)
const showProductModal = ref(false)

const openCreateModal = () => {
  editingProduct.value = null
  showProductModal.value = true
  notification.notify('Đang mở bảng TẠO MỚI sản phẩm', 'success')
}

const openEditModal = (product) => {
  editingProduct.value = product
  showProductModal.value = true
  notification.notify('Đang mở bảng SỬA sản phẩm', 'success')
}

const handleSave = (values) => {
  if (selectedProducts.value) {
    console.log('Update', values)
  } else {
    console.log('Create', values)
  }
}
</script>

<template>
  <div class="container-fluid">
    <div class="fs-2 fw-bold my-5 px-4">
      {{ label }}
    </div>
    <div class="px-4">
      <div class="d-flex justify-content-center gap-3">
        <div class="w-25 h-100">
          <div class="fs-3 fw-semibold">Filter</div>
          <div class="mt-3">
            <div class="fs-5 fw-semibold">Audience</div>
            <div class="mt-2 fs-5">
              <div class="d-flex flex-wrap">
                <div class="mb-3 form-check w-50" v-for="a in audiences">
                  <input
                    type="checkbox"
                    name="audience"
                    :id="a.name.toLowerCase()"
                    class="form-check-input"
                    :value="a.name.toLowerCase()"
                    v-model="selectedAudience"
                  />
                  <label :for="a.name.toLowerCase()" class="form-check-label">{{ a.name }}</label>
                </div>
              </div>
            </div>
          </div>
          <div class="mt-3">
            <div class="fs-5 fw-semibold">Brand</div>
            <div class="mt-2 fs-5">
              <div class="d-flex flex-wrap">
                <div class="mb-3 form-check w-50" v-for="b in brands">
                  <input
                    type="checkbox"
                    name="brand"
                    :id="b.name.toLowerCase()"
                    class="form-check-input"
                    :value="b.name.toLowerCase()"
                    v-model="selectedBrand"
                  />
                  <label :for="b.name.toLowerCase()" class="form-check-label">{{ b.name }}</label>
                </div>
              </div>
            </div>
          </div>
          <div class="mt-3">
            <div class="fs-5 fw-semibold">Category</div>
            <div class="mt-2 fs-5">
              <select
                class="form-select"
                aria-label="Default select example"
                v-model="selectedCategory"
              >
                <option :value="undefined">All Categories</option>

                <option v-for="c in categories" :key="c.id" :value="c.id">
                  {{ c.name }}
                </option>
              </select>
            </div>
          </div>
        </div>
        <div class="w-75 h-100 ps-3 border-start">
          <div class="row g-4">
            <!-- Có products -->
            <template v-if="filterList.length > 0">
              <div class="col-lg-4" v-for="f in filterList" :key="f.id">
                <RouterLink
                  :to="{
                    name: 'productDetail',
                    query: {
                      ...(f.productVariant && f.productVariant.length > 0
                        ? { productVariantId: f.productVariant[0].id }
                        : { productId: f.id }),
                    },
                  }"
                  class="text-decoration-none"
                >
                  <div class="card border-0 h-100">
                    <img
                      :src="url + '/' + f.img"
                      class="card-img-top object-fit-contain"
                      style="height: 400px; background-color: #e4e4e4"
                      alt=""
                    />
                    <div class="card-body d-flex flex-column ps-0">
                      <h5 class="card-title fw-bold">{{ f.name }}</h5>
                      <p class="card-text text-muted">{{ f.category }} - {{ f.brand }}</p>

                      <!-- Có product variant -->
                      <template v-if="f.productVariant && f.productVariant.length > 0">
                        <p class="fw-bold">
                          {{ (f.productVariant[0].price ?? f.basePrice).toLocaleString('vi-VN') }} đ
                        </p>
                      </template>

                      <!-- Không có variant -->
                      <template v-else>
                        <p class="fw-bold">{{ f.basePrice.toLocaleString('vi-VN') }} đ</p>
                      </template>
                    </div>
                  </div>
                </RouterLink>
              </div>
            </template>

            <!-- Không có product -->
            <div v-else class="col-12 text-center py-5">
              <p class="text-muted fs-14">Không tìm thấy sản phẩm nào</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAudienceStore } from '@/stores/useAudienceStore'
import { useBrandStore } from '@/stores/useBrandStore'
import { useCategoryStore } from '@/stores/useCategoryStore'
import { useProductStore } from '@/stores/useProductStore'
import ulti from '@/ulti/ulti'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// lấy param từ route
const route = useRoute()
const router = useRouter()

const audience = computed(() => route.query.audience)
const brand = computed(() => route.query.brand)
const product = computed(() => route.query.product)
const name = computed(() => route.query.name)

// fetch data

const productStore = useProductStore()
const brandStore = useBrandStore()
const audienceStore = useAudienceStore()
const categoryStore = useCategoryStore()

const { brands } = storeToRefs(brandStore)
const { audiences } = storeToRefs(audienceStore)
const { categories } = storeToRefs(categoryStore)
console.log(categories)

onMounted(async () => {
  await productStore.fetchFilterProducts({ audience: audience.value, brand: brand.value })
})

onMounted(async () => {
  await categoryStore.fetchCategory()
})

watch(
  () => route.query,
  async (query) => {
    await productStore.fetchFilterProducts(query)
  },
  { immediate: true },
)

const label = computed(() => {
  const audience = route.query.audience
  const brand = route.query.brand

  if (!audience && !brand) {
    return 'Clothing'
  }

  // Ưu tiên audience
  if (audience) {
    const audiences = Array.isArray(audience) ? audience : [audience]

    if (audiences.length === 1) {
      return `${ulti.formatLabel(audiences[0])}'s Clothing`
    }

    return 'Clothing'
  }

  // Không có audience thì xét brand
  const brands = Array.isArray(brand) ? brand : [brand]

  if (brands.length === 1) {
    return `${ulti.formatLabel(brands[0])}'s Clothing`
  }

  return 'Clothing'
})

const filterList = computed(() => productStore.filterProducts)

const selectedAudience = computed({
  get() {
    const audience = route.query.audience

    if (!audience) return []

    return Array.isArray(audience) ? audience : [audience]
  },

  set(value) {
    router.push({
      query: {
        ...route.query,
        audience: value.length ? value : undefined,
      },
    })
  },
})

const selectedBrand = computed({
  get() {
    const brand = route.query.brand

    if (!brand) return []

    return Array.isArray(brand) ? brand : [brand]
  },

  set(value) {
    router.push({
      query: {
        ...route.query,
        brand: value.length ? value : undefined,
      },
    })
  },
})

const selectedCategory = computed({
  get() {
    return route.query.category ?? undefined
  },

  set(value) {
    router.push({
      query: {
        ...route.query,
        category: value || undefined,
      },
    })
  },
})

// url

const url = 'http://localhost:8080/uploads/images'
</script>

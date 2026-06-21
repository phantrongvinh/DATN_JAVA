<template>
  <div class="container-fluid">
    <!-- Header của trang product -->
    <div class="d-flex justify-content-between align-items-center px-4 mt-4 mb-5 z-100">
      <!-- Label hiện thỉ theo filter -->
      <div class="fs-2 fw-bold">
        {{ label }}
      </div>

      <!-- Nút bấm để handle ẩn hiện filter -->
      <button class="btn bg-gradient-danger mb-0 shadow" @click="showFilter = !showFilter">
        <i class="fa-solid fa-sliders me-2"></i>

        {{ showFilter ? 'Ẩn bộ lọc' : 'Hiện bộ lọc' }}

        <i class="fa-solid ms-2" :class="showFilter ? 'fa-chevron-left' : 'fa-chevron-right'"></i>
      </button>
    </div>

    <!-- Content trang product -->
    <div class="px-4 mt-5">
      <!-- Chia grid cho content -->
      <div class="row">
        <!-- Filter được handle ẩn hiện theo nút bấm ở trên -->
        <div class="col-lg-3" v-show="showFilter">
          <div class="card position-sticky top-1 border-0 shadow-0">
            <div class="card-header pb-0">
              <h5>Filter</h5>
            </div>
            <div class="card-body">
              <!-- Filter checkbox theo audiences -->
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
                        :value="a.id"
                        v-model="selectedAudience"
                      />
                      <label :for="a.name.toLowerCase()" class="form-check-label">{{
                        a.name
                      }}</label>
                    </div>
                  </div>
                </div>
              </div>
              <!-- Filter checkbox theo brand -->
              <hr class="horizontal dark my-3" />
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
                        :value="b.id"
                        v-model="selectedBrand"
                      />
                      <label :for="b.name.toLowerCase()" class="form-check-label">{{
                        b.name
                      }}</label>
                    </div>
                  </div>
                </div>
              </div>
              <hr class="horizontal dark my-3" />
            </div>
          </div>
        </div>
        <!-- Phần products list sẽ được filter theo điều kiện của side filter -->
        <div class="ps-3 border-start pb-5" :class="showFilter ? 'col-lg-9' : 'col-lg-12'">
          <!-- Phần filter thêm của product list: danh mục, khoảng giá, tăng dần giảm dần -->
          <div class="d-flex justify-content-end gap-3">
            <div class="input-group input-group-outline w-25">
              <select class="form-control" v-model="selectedCategory">
                <option :value="undefined">Chọn danh mục</option>

                <option class="mx-2" v-for="c in categories" :key="c.id" :value="c.id">
                  {{ c.name }}
                </option>
              </select>
            </div>
          </div>
          <hr class="my-5" />
          <!-- Phần data product list -->
          <div class="row g-4">
            <template v-if="filterList.length > 0">
              <div class="col-xl-4 col-md-6 mb-5 d-flex" v-for="f in filterList" :key="f.id">
                <div class="card h-100 w-100 d-flex flex-column" data-animation="true">
                  <!-- Image -->
                  <div class="card-header p-0 position-relative mt-n2 mx-3 z-index-2">
                    <a class="d-block blur-shadow-image">
                      <img
                        :src="url + '/' + f.img"
                        class="img-fluid shadow border-radius-lg"
                        style="height: 300px; width: 100%; object-fit: cover"
                      />
                    </a>
                    <div
                      class="colored-shadow"
                      :style="{ backgroundImage: `url(${url}/${f.img})` }"
                    ></div>
                  </div>

                  <!-- Body -->
                  <div class="card-body text-center d-flex flex-column flex-grow-1">
                    <div class="d-flex mt-n4 mx-auto">
                      <button
                        class="btn btn-link text-danger ms-auto border-0"
                        data-bs-toggle="tooltip"
                        title="Favorite"
                      >
                        <i class="fa-regular fa-heart"></i>
                      </button>
                      <button
                        class="btn btn-link text-dark me-auto border-0"
                        data-bs-toggle="tooltip"
                        title="Quick View"
                      >
                        <i class="fa-solid fa-eye"></i>
                      </button>
                    </div>

                    <!-- Chuyển hướng product detail -->
                    <RouterLink
                      :to="{
                        name: 'productDetail',
                        params: { productId: f.id },
                      }"
                    >
                      <h5 class="font-weight-normal mt-3 text-dark product-title">
                        {{ f.name }}
                      </h5>
                    </RouterLink>

                    <p class="text-sm text-secondary mb-0 product-meta">
                      {{ f.category }} · {{ f.brand }}
                    </p>

                    <!-- spacer đẩy footer xuống đáy -->
                    <div class="mt-auto"></div>
                  </div>

                  <hr class="dark horizontal my-0" />

                  <!-- Footer -->
                  <div class="card-footer d-flex align-items-center justify-content-end">
                    <h6 class="text-danger mb-0">
                      {{ (f.productVariant?.[0]?.price ?? f.basePrice).toLocaleString('vi-VN') }} đ
                    </h6>
                  </div>
                </div>
              </div>
            </template>

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

onMounted(async () => {
  await categoryStore.fetchCategory()
})

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
    await productStore.fetchFilterProducts(params)
  },
  { immediate: true },
)

const label = computed(() => {
  const audienceIds = route.query.audienceIds
  const brandIds = route.query.brandIds

  if (!audienceIds && !brandIds) {
    return 'Clothing'
  }

  // Ưu tiên audience
  if (audienceIds) {
    const ids = Array.isArray(audienceIds) ? audienceIds.map(Number) : [Number(audienceIds)]

    if (ids.length === 1) {
      const audience = audiences.value.find((a) => a.id === ids[0])

      if (audience) {
        return `${ulti.formatLabel(audience.name)}'s Clothing`
      }
    }

    return 'Clothing'
  }

  // Xét brand
  const ids = Array.isArray(brandIds) ? brandIds.map(Number) : [Number(brandIds)]

  if (ids.length === 1) {
    const brand = brands.value.find((b) => b.id === ids[0])

    if (brand) {
      return `${ulti.formatLabel(brand.name)}'s Clothing`
    }
  }

  return 'Clothing'
})
const filterList = computed(() => productStore.filterProducts)

const selectedAudience = computed({
  get() {
    const audienceIds = route.query.audienceIds

    if (!audienceIds) return []

    return Array.isArray(audienceIds) ? audienceIds : [audienceIds]
  },

  set(value) {
    router.push({
      query: {
        ...route.query,
        audienceIds: value.length ? value : undefined,
      },
    })
  },
})

const selectedBrand = computed({
  get() {
    const brandIds = route.query.brandIds

    if (!brandIds) return []

    return Array.isArray(brandIds) ? brandIds : [brandIds]
  },

  set(value) {
    router.push({
      query: {
        ...route.query,
        brandIds: value.length ? value : undefined,
      },
    })
  },
})

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

// url

const url = 'http://localhost:8080/uploads/images'

// ẩn hiện filter
const showFilter = ref(true)
</script>

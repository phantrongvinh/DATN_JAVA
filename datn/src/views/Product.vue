<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import { X } from 'lucide-vue-next'

import ProductCard from '@/components/site/ProductCard.vue'
import FilterGroup from '@/components/site/FilterGroup.vue'
import FilterCheck from '@/components/site/FilterCheck.vue'
import { useProductStore } from '@/stores/useProductStore'
import { storeToRefs } from 'pinia'
import { useAudienceStore } from '@/stores/useAudienceStore'
import { useBrandStore } from '@/stores/useBrandStore'
import { useCategoryStore } from '@/stores/useCategoryStore'
import { useDebounce } from '@/composables/useDebounce'

const route = useRoute()
const router = useRouter()

const productStore = useProductStore()
const audienceStore = useAudienceStore()
const brandStore = useBrandStore()
const categoryStore = useCategoryStore()

const { brands } = storeToRefs(brandStore)
const { audiences } = storeToRefs(audienceStore)
const { categories } = storeToRefs(categoryStore)

onMounted(async () => {
  await categoryStore.fetchCategory()
})

const { filterProducts, totalPages, totalElements } = storeToRefs(productStore)

const toNumberArray = (val) => {
  if (!val) return []
  return Array.isArray(val) ? val.map(Number) : [Number(val)]
}

// ─── Debounce search ─────────────────────────────────
const searchInput = ref(route.query.search ?? '')
const searchDebounced = useDebounce(searchInput, 500)

watch(searchDebounced, (newVal) => {
  router.push({
    query: {
      ...route.query,
      search: newVal || undefined,
      page: 1, // reset về trang 1 khi search
    },
  })
})

// ─── Pagination ──────────────────────────────────────
const currentPage = computed({
  get() {
    return Number(route.query.page ?? 1)
  },
  set(value) {
    router.push({
      query: { ...route.query, page: value },
    })
  },
})

watch(
  () => route.query,
  async (query) => {
    const params = {
      brandIds: toNumberArray(query.brandIds),
      categoryIds: toNumberArray(query.categoryIds),
      audienceIds: toNumberArray(query.audienceIds),
      search: query.search || null,
      onSale: query.onSale === 'true' ? true : null,
      minPrice: query.minPrice ? Number(query.minPrice) : null,
      maxPrice: query.maxPrice ? Number(query.maxPrice) : null,
      sortBy: query.sortBy ?? 'newest',
      page: Number(query.page ?? 1) - 1,
      size: 12,
    }
    await productStore.fetchFilterProducts(params)
  },
  { immediate: true },
)

// handle check brand
const selectedBrand = computed({
  get() {
    const brandIds = route.query.brandIds
    if (!brandIds) return []
    return (Array.isArray(brandIds) ? brandIds : [brandIds]).map(Number)
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

const toggleBrand = (id) => {
  const current = new Set(selectedBrand.value)
  if (current.has(id)) {
    current.delete(id)
  } else {
    current.add(id)
  }
  selectedBrand.value = [...current]
}

// handle check category
const selectedCategory = computed({
  get() {
    const categoryIds = route.query.categoryIds
    if (!categoryIds) return []
    return (Array.isArray(categoryIds) ? categoryIds : [categoryIds]).map(Number)
  },
  set(value) {
    router.push({
      query: {
        ...route.query,
        categoryIds: value.length ? value : undefined,
      },
    })
  },
})

const toggleCategory = (id) => {
  const current = new Set(selectedCategory.value)
  if (current.has(id)) {
    current.delete(id)
  } else {
    current.add(id)
  }
  selectedCategory.value = [...current]
}

// handle check audience
const selectedAudience = computed({
  get() {
    const audienceIds = route.query.audienceIds
    if (!audienceIds) return []
    return (Array.isArray(audienceIds) ? audienceIds : [audienceIds]).map(Number)
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

const toggleAudience = (id) => {
  const current = new Set(selectedAudience.value)
  if (current.has(id)) {
    current.delete(id)
  } else {
    current.add(id)
  }
  selectedAudience.value = [...current]
}
const audienceLabel = (name) => {
  const map = {
    Men: 'Nam',
    Women: 'Nữ',
    Kids: 'Trẻ em',
  }
  return map[name] ?? 'Phi giới tính'
}

// handle price
const PRICE_RANGES = [
  { label: 'Dưới 500K', min: 0, max: 500000 },
  { label: '500K – 1.5tr', min: 500000, max: 1500000 },
  { label: '1.5tr – 3tr', min: 1500000, max: 3000000 },
  { label: '3tr – 5tr', min: 3000000, max: 5000000 },
  { label: 'Trên 5tr', min: 5000000, max: null },
]

const selectedPrice = computed({
  get() {
    return {
      min: route.query.minPrice ? Number(route.query.minPrice) : null,
      max: route.query.maxPrice ? Number(route.query.maxPrice) : null,
    }
  },
  set(value) {
    router.push({
      query: {
        ...route.query,
        minPrice: value.min ?? undefined,
        maxPrice: value.max ?? undefined,
      },
    })
  },
})

// handle sale
const selectedOnSale = computed({
  get() {
    return route.query.onSale === 'true'
  },
  set(value) {
    router.push({
      query: {
        ...route.query,
        onSale: value ? 'true' : undefined,
      },
    })
  },
})

const togglePrice = (price) => {
  // Click lại range đang chọn thì bỏ chọn
  if (selectedPrice.value.min === price.min && selectedPrice.value.max === price.max) {
    selectedPrice.value = { min: null, max: null }
  } else {
    selectedPrice.value = { min: price.min, max: price.max }
  }
}

// handle active filter
const activeFilters = computed(() => {
  const filters = []

  // Brand
  const brandIds = toNumberArray(route.query.brandIds)
  brandIds.forEach((id) => {
    const brand = brands.value.find((b) => b.id === id)
    if (brand) {
      filters.push({
        label: brand.name,
        clear: () => {
          selectedBrand.value = selectedBrand.value.filter((b) => b !== id)
        },
      })
    }
  })
  // Category
  const categoryIds = toNumberArray(route.query.categoryIds)
  categoryIds.forEach((id) => {
    const category = categories.value.find((c) => c.id === id)
    if (category) {
      filters.push({
        label: category.name,
        clear: () => {
          selectedCategory.value = selectedCategory.value.filter((c) => c !== id)
        },
      })
    }
  })

  // Audience
  const audienceIds = toNumberArray(route.query.audienceIds)
  audienceIds.forEach((id) => {
    const audience = audiences.value.find((a) => a.id === id)
    if (audience) {
      filters.push({
        label: audienceLabel(audience.name),
        clear: () => {
          selectedAudience.value = selectedAudience.value.filter((a) => a !== id)
        },
      })
    }
  })

  // Search
  //   if (route.query.search) {
  //     filters.push({
  //       label: `"${route.query.search}"`,
  //       clear: () => router.push({ query: { ...route.query, search: undefined } }),
  //     })
  //   }

  return filters
})
</script>

<template>
  <!-- Header -->
  <div class="border-b border-border">
    <div class="container-x py-12">
      <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">
        <RouterLink to="/">Trang chủ</RouterLink> / Sản phẩm
      </p>

      <h1 class="mt-3 font-display text-4xl md:text-5xl">
        Toàn bộ <span class="italic text-gold">bộ sưu tập</span>
      </h1>
    </div>
  </div>

  <div class="container-x grid gap-10 py-12 md:grid-cols-[260px_1fr]">
    <!-- Sidebar -->
    <aside class="self-start space-y-8 rounded-xl border border-border p-6">
      <FilterGroup title="Thương hiệu">
        <FilterCheck
          v-for="brand in brands"
          :key="brand.id"
          :label="brand.name"
          :value="brand.id"
          :checked="selectedBrand.includes(brand.id)"
          @change="toggleBrand(brand.id)"
        />
      </FilterGroup>

      <FilterGroup title="Danh mục">
        <FilterCheck
          v-for="category in categories"
          :key="category.id"
          :label="category.name"
          :value="category.id"
          :checked="selectedCategory.includes(category.id)"
          @change="toggleCategory(category.id)"
        />
      </FilterGroup>

      <FilterGroup title="Đối tượng">
        <FilterCheck
          v-for="audience in audiences"
          :key="audience.id"
          :label="audienceLabel(audience.name)"
          :value="audience.id"
          :checked="selectedAudience.includes(audience.id)"
          @change="toggleAudience(audience.id)"
        />
      </FilterGroup>

      <FilterGroup title="Khoảng giá">
        <FilterCheck
          label="Đang giảm giá"
          :checked="selectedOnSale"
          @change="selectedOnSale = !selectedOnSale"
        />
        <FilterCheck
          v-for="price in PRICE_RANGES"
          :key="price.label"
          :label="price.label"
          :checked="selectedPrice.min === price.min && selectedPrice.max === price.max"
          @change="togglePrice(price)"
        />
      </FilterGroup>

      <!-- <FilterGroup title="Khuyến mãi">
        <FilterCheck
          label="Chỉ hiện sản phẩm giảm giá"
          :checked="search.onSale === 'true'"
          @change="
            setSearch({
              onSale: search.onSale === 'true' ? undefined : 'true',
            })
          "
        />
      </FilterGroup> -->
    </aside>

    <!-- Product -->
    <div>
      <div class="flex flex-wrap items-center justify-between gap-3 border-b border-border pb-4">
        <input
          v-model="searchInput"
          type="text"
          placeholder="Tìm kiếm sản phẩm..."
          class="rounded border border-border px-3 py-1.5 text-sm outline-none focus:border-black"
        />
        <p class="text-sm text-muted-foreground">
          {{ filterProducts && filterProducts.length > 0 ? filterProducts.length : '0' }}
          sản phẩm
        </p>

        <select
          class="border-b border-foreground bg-transparent py-1 outline-none"
          @change="
            setSearch({
              sort: $event.target.value,
            })
          "
        >
          <option value="newest">Mới nhất</option>
          <option value="oldest">Cũ nhất</option>
          <option value="price-asc">Giá tăng dần</option>
          <option value="price-desc">Giá giảm dần</option>
        </select>
      </div>

      <!-- Active filter -->
      <div v-if="activeFilters.length" class="mt-4 flex flex-wrap gap-2">
        <button
          v-for="(f, i) in activeFilters"
          :key="i"
          @click="f.clear()"
          class="flex items-center gap-1 border px-3 py-1 text-xs"
        >
          {{ f.label }}
          <X class="h-3 w-3" />
        </button>
      </div>

      <!-- Grid -->
      <div v-if="filterProducts && filterProducts.length > 0">
        <div class="mt-8 grid grid-cols-2 gap-x-6 gap-y-10 md:grid-cols-3">
          <ProductCard v-for="p in filterProducts" :key="p.id" :product="p" />
        </div>
        <div v-if="totalPages > 1" class="mt-20 flex items-center justify-end gap-2">
          <button
            class="rounded border px-3 py-1.5 text-sm disabled:opacity-40"
            :disabled="currentPage === 1"
            @click="currentPage--"
          >
            ← Trước
          </button>

          <template v-for="p in totalPages" :key="p">
            <!-- Hiển thị trang đầu, cuối, và xung quanh trang hiện tại -->
            <button
              v-if="p === 1 || p === totalPages || Math.abs(p - currentPage) <= 1"
              class="rounded border px-3 py-1.5 text-sm"
              :class="p === currentPage ? 'bg-black text-white border-black' : 'hover:bg-gray-100'"
              @click="currentPage = p"
            >
              {{ p }}
            </button>

            <!-- Dấu ... -->
            <span v-else-if="Math.abs(p - currentPage) === 2" class="px-1 text-muted-foreground">
              ...
            </span>
          </template>

          <button
            class="rounded border px-3 py-1.5 text-sm disabled:opacity-40"
            :disabled="currentPage === totalPages"
            @click="currentPage++"
          >
            Sau →
          </button>
        </div>
      </div>

      <div v-else class="py-24 text-center text-muted-foreground">
        Không có sản phẩm phù hợp với bộ lọc.
      </div>
    </div>
  </div>
</template>

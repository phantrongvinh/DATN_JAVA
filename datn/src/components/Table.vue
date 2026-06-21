<template>
  <div class="card-body px-0 pb-2">
    <div class="table-responsive p-0">
      <table class="table table-responsive align-items-center mb-0 table-fixed">
        <thead>
          <tr>
            <th
              class="text-uppercase text-secondary text-xxs font-weight-bolder opacity-7 ps-2"
              :class="
                ['trangThai', 'bienThe'].includes(key)
                  ? 'text-center  ps-4'
                  : ['sanPham'].includes(key)
                    ? 'w-25'
                    : ''
              "
              v-for="(f, key) in fields"
              :key="key"
            >
              {{ f.name }}
            </th>
          </tr>
        </thead>
        <tbody>
          <ProductOverview v-if="productOverview" :data="productOverview"></ProductOverview>
          <ProductListView
            v-if="products"
            :data="products"
            :interactive="props.interactive"
            :handleActiveProduct="props.handleActiveProduct"
            :handleUpdateProduct="props.handleUpdateProduct"
          ></ProductListView>
        </tbody>
      </table>
    </div>
  </div>
  <div class="text-muted font-semibold w-100 text-center" v-if="props.message">
    {{ props.message }}
  </div>
</template>

<script setup>
import { reactive, watch } from 'vue'
import ProductOverview from './tableItems/ProductOverview.vue'
import ProductListView from './tableItems/ProductListView.vue'

const props = defineProps({
  initialValues: Object,
  interactive: Boolean,
  isBorder: Boolean,
  productOverview: Array,
  products: Array,
  handleActiveProduct: Function,
  handleUpdateProduct: Function,
  message: String,
})

const fields = reactive({})

watch(
  () => props.initialValues,
  (val) => {
    if (!val) return
    Object.entries(val).forEach(([key, value]) => {
      fields[key] = { name: value }
    })
  },
  { immediate: true },
)

const productOverview = reactive([])

watch(
  () => props.productOverview,
  (val) => {
    if (!val) return
    val.forEach((value, index) => {
      productOverview[index] = {
        id: value.id,
        name: value.name,
        category: value.category,
        brand: value.brand,
        price: value.basePrice,
        stock: value.stock,
      }
    })
  },
  { immediate: true },
)

const products = reactive([])

watch(
  () => props.products,
  (val) => {
    if (!val) return
    products.splice(
      0,
      products.length,
      ...val.map((value) => ({
        id: value.id,
        name: value.name,
        category: value.category,
        brand: value.brand,
        price: value.basePrice,
        variantCount: value.variantCount,
        status: value.status,
        updatedAt: value.updatedAt,
      })),
    )
  },
)
</script>

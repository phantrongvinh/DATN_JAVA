<template>
  <div class="container-fluid">
    <div class="px-4">
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
                  style="height: 600px; background-color: #e4e4e4"
                  alt=""
                />
                <div class="card-body d-flex flex-column">
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
</template>

<script setup>
import { useProductStore } from '@/stores/useProductStore'
import { computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'

// lấy param từ route
const route = useRoute()

const audience = computed(() => route.query.audience)
const brand = computed(() => route.query.brand)
const product = computed(() => route.query.product)
const name = computed(() => route.query.name)

// fetch data

const productStore = useProductStore()

onMounted(async () => {
  await productStore.fetchFilterProducts({ audience: audience.value, brand: brand.value })
})

watch([audience, brand], async ([newAudience, newBrand]) => {
  await productStore.fetchFilterProducts({ audience: newAudience, brand: newBrand })
})

const filterList = computed(() => productStore.filterProducts)

// url

const url = 'http://localhost:8080/uploads/images'
</script>

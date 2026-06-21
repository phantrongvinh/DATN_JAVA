<template>
  <div class="container-fluid py-5">
    <div class="border rounded mx-5">
      <div class="row p-3" v-if="product">
        <!-- LEFT -->
        <div class="col-lg-8">
          <Swiper
            :modules="[Thumbs, Navigation]"
            :thumbs="{ swiper: thumbsSwiper }"
            navigation
            class="rounded-4 shadow product-swiper"
          >
            <SwiperSlide v-for="image in product.images" :key="image">
              <img
                :src="url + '/' + image.imageUrl"
                class="img-fluid border-radius-xl"
                style="height: 800px; width: 100%; object-fit: scale-down"
              />
            </SwiperSlide>
          </Swiper>
          <Swiper
            class="thumb-swiper mt-3 product-swiper"
            :modules="[FreeMode, Thumbs, Navigation]"
            navigation
            :slides-per-view="4"
            :space-between="15"
            :free-mode="true"
            :watch-slides-progress="true"
            @swiper="setThumbsSwiper"
          >
            <SwiperSlide v-for="image in product?.images" :key="image">
              <img
                :src="url + '/' + image.imageUrl"
                class="img-fluid rounded shadow-sm"
                style="height: 100px; width: 100%; object-fit: cover; cursor: pointer"
              />
            </SwiperSlide>
          </Swiper>
        </div>

        <!-- RIGHT -->
        <div class="col-lg-4">
          <div class="ps-lg-5">
            <h2 class="font-weight-bold">
              {{ product.name }}
            </h2>

            ⭐⭐⭐⭐⭐ (32 đánh giá)

            <h3 class="text-gradient text-danger mt-3">{{ ulti.formatVND(selectPrice) }}</h3>

            <span class="badge bg-gradient-success"> Còn hàng </span>

            <div class="mt-4 text-sm">
              {{ product.description }}
            </div>

            <div class="mt-4">
              <h6>Thương hiệu</h6>

              <div class="d-flex align-items-center">
                <!-- <img :src="url + '/brands/' + product.brandLogo" width="40" class="me-3" /> -->

                {{ product.brandName }}
              </div>
            </div>

            <div class="row mt-5">
              <!-- Size -->
              <div class="col-md-4">
                <label>Size</label>
                <Select
                  :data="availableSizes"
                  v-model:selectedByName="selectedSize"
                  :selectedName="selectedSize"
                  title="size"
                ></Select>
              </div>

              <!-- Color -->
              <div class="col-md-4">
                <label>Color</label>

                <div class="dropdown">
                  <button
                    class="btn btn-transparent w-100 text-start border-bottom rounded-0"
                    data-bs-toggle="dropdown"
                  >
                    {{ selectedColor }}
                  </button>

                  <ul class="dropdown-menu shadow-lg border-0 rounded-4 w-100">
                    <li v-for="color in colors" :key="color">
                      <a
                        class="dropdown-item rounded-3 py-2"
                        href="#"
                        @click.prevent="selectedColor = color"
                      >
                        {{ color }}
                      </a>
                    </li>
                  </ul>
                </div>
              </div>

              <!-- Quantity -->
              <div class="col-md-4">
                <label>Quantity</label>

                <div class="dropdown">
                  <button
                    class="btn btn-transparent w-100 text-start border-bottom rounded-0"
                    data-bs-toggle="dropdown"
                  >
                    {{ quantity }}
                  </button>

                  <ul class="dropdown-menu shadow-lg border-0 rounded-4 w-100">
                    <li v-for="i in currentVariant?.stock" :key="i">
                      <a
                        class="dropdown-item rounded-3 py-2"
                        href="#"
                        @click.prevent="quantity = i"
                      >
                        {{ i }}
                      </a>
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            <div class="mt-3" v-if="currentVariant">
              <h6>
                Còn lại:

                <span class="text-success">
                  {{ currentVariant.stock }}
                </span>

                sản phẩm
              </h6>
            </div>

            <div class="mt-4 text-danger" v-if="quantityMessage !== ''">
              {{ quantityMessage }}
            </div>

            <div class="mt-4">
              <button class="btn bg-gradient-danger w-100" @click.prevent="handleAddToCart">
                <i class="fa-solid fa-cart-shopping me-2"></i>

                Thêm vào giỏ hàng
              </button>

              <button class="btn btn-outline-dark w-100">Mua ngay</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <!-- <div class="card mt-5">
    <div class="card-header">
      <h5>Đánh giá khách hàng</h5>
    </div>

    <div class="card-body">
      <div v-for="comment in comments" class="d-flex mb-4">
        <img src="/images/avatar-default.svg" class="avatar avatar-lg me-3" />

        <div>
          <h6>{{ comment.user }}</h6>

          ⭐⭐⭐⭐⭐

          <p>
            {{ comment.content }}
          </p>
        </div>
      </div>
    </div>
  </div>
  <div class="row mt-5">
    <div class="col-lg-3" v-for="item in relatedProducts">
      <div class="card h-100" data-animation="true">
        <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
          <img :src="url + '/' + item.img" class="img-fluid shadow border-radius-lg" />
        </div>

        <div class="card-body text-center">
          <h6>
            {{ item.name }}
          </h6>

          <p class="text-sm text-secondary">
            {{ item.brand }}
          </p>

          <h5 class="text-danger">{{ item.basePrice.toLocaleString('vi-VN') }}đ</h5>
        </div>
      </div>
    </div>
  </div> -->
</template>

<script setup>
import { useRoute } from 'vue-router'
import { Swiper, SwiperSlide } from 'swiper/vue'
import { Navigation, Thumbs, FreeMode } from 'swiper/modules'

import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/css/free-mode'
import 'swiper/css/thumbs'
import { computed, ref, watch } from 'vue'
import { useProductStore } from '@/stores/useProductStore'
import { storeToRefs } from 'pinia'
import ulti from '@/ulti/ulti'
import Select from '@/components/Select.vue'
import { useCartStore } from '@/stores/useCartStore'

const thumbsSwiper = ref(null)
const setThumbsSwiper = (swiper) => {
  thumbsSwiper.value = swiper
}
// url

const url = 'http://localhost:8080/uploads/images'

// lấy id product bằng param
const route = useRoute()

// fetch product theo id
const productStore = useProductStore()

const { product } = storeToRefs(productStore)

const loadProduct = async (id) => {
  await productStore.fetchProductByID(id)
}

watch(
  () => route.params.productId,
  (newId) => {
    if (newId) loadProduct(newId)
  },
  { immediate: true },
)

// handle selected product
const quantity = ref(0)

const colors = computed(() => {
  if (!product.value?.variants) return []
  return [...new Set(product.value.variants.map((v) => v.color))]
})

const selectedColor = ref(null)

const availableSizes = computed(() => {
  if (!product.value?.variants || !selectedColor.value) return []
  return product.value.variants.filter((v) => v.color === selectedColor.value)
})

const selectedSize = ref(null)

const currentVariant = computed(() => {
  if (!product.value?.variants) return null
  return product.value.variants.find(
    (v) => v.color === selectedColor.value && v.sizeName === selectedSize.value,
  )
})

const selectPrice = computed(() => {
  return currentVariant.value?.price
})

watch(product, (newProduct) => {
  if (newProduct?.variants?.length) {
    selectedColor.value = colors.value[0]
    selectedSize.value = availableSizes.value[0]?.sizeName
  }
})

watch(selectedColor, () => {
  selectedSize.value = availableSizes.value[0]?.sizeName
})

// hanlde add to cart
const quantityMessage = ref('')

const cartStore = useCartStore()

const { items } = storeToRefs(cartStore)

watch(quantity, (newQuantity) => {
  if (newQuantity) quantityMessage.value = ''
})

const handleAddToCart = () => {
  if (quantity.value !== 0) {
    const image = product.value.images.find((i) => i.isPrimary)

    const data = {
      name: product.value.name,
      productVariantId: currentVariant.value.id,
      sku: currentVariant.value.sku,
      image: image.imageUrl,
      size: currentVariant.value.sizeName,
      color: currentVariant.value.color,
      quantity: quantity.value,
      price: currentVariant.value.price,
    }
    cartStore.addItem(data)
  } else {
    quantityMessage.value = 'Vui lòng chọn số lượng'
  }
}
</script>

<style scoped>
.swiper-button-prev,
.swiper-button-next {
  width: 50px;
  height: 50px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  border-radius: 50%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.swiper-button-prev::after,
.swiper-button-next::after {
  font-size: 18px;
  color: #212529;
}

.thumb-swiper .swiper-slide {
  width: auto !important;
  flex-shrink: 0;
}
.swiper-slide {
  height: auto !important;
}

.swiper-slide img {
  width: 100%;
  height: 100px;
  object-fit: cover;
}

.product-swiper .swiper-button-prev,
.product-swiper .swiper-button-next {
  width: 36px;
  height: 36px;
  background: black;
  border-radius: 50%;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.15);

  transition: 0.3s;
}

.product-swiper .swiper-button-prev::after,
.product-swiper .swiper-button-next::after {
  font-size: 12px;
  font-weight: bold;
  color: #000000;
}
.product-swiper .swiper-button-prev,
.product-swiper .swiper-button-next {
  opacity: 0;
}

.product-swiper:hover .swiper-button-prev,
.product-swiper:hover .swiper-button-next {
  opacity: 1;
}
</style>

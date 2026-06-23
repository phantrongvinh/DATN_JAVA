<template>
  <div class="mb-5">
    <img src="/images/slide_index_1.jpg" alt="" class="img-fluid w-100" />
  </div>
  <div class="my-5 container">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h3 class="mb-0">🔥 Ưu đãi hôm nay</h3>

        <p class="text-sm text-secondary">Sản phẩm đang giảm giá tốt nhất</p>
      </div>

      <RouterLink to="/sale" class="btn bg-gradient-dark mb-0"> Xem tất cả </RouterLink>
    </div>
    <div class="row d-flex align-items-stretch" v-if="productOnSale && productOnSale.length > 0">
      <div class="col-xl-4 col-md-6 mb-5" v-for="p in productOnSale">
        <div class="card h-100" data-animation="true">
          <!-- Badge giảm giá -->
          <div class="position-absolute top-0 end-0 mt-3 me-3 z-index-3">
            <span class="badge bg-gradient-danger">
              {{
                p.promotion.discountType === 'percent'
                  ? `-${p.promotion.discountValue}%`
                  : `-${ulti.formatVND(p.promotion.discountValue)}`
              }}
            </span>
          </div>

          <!-- Image -->
          <div class="card-header p-0 position-relative mt-n4 mx-3 z-index-2">
            <a class="d-block blur-shadow-image">
              <img
                :src="url + '/' + p.img"
                class="img-fluid shadow border-radius-lg"
                style="height: 300px; width: 100%; object-fit: contain"
              />
            </a>

            <div class="colored-shadow" :style="`background-image: url('${url}/${p.img}')`"></div>
          </div>

          <!-- Body -->
          <div class="card-body text-center">
            <div class="d-flex mt-n4 mx-auto">
              <button class="btn btn-link text-danger ms-auto border-0">
                <i class="fa-regular fa-heart"></i>
              </button>

              <button class="btn btn-link text-dark me-auto border-0">
                <i class="fa-solid fa-eye"></i>
              </button>
            </div>

            <!-- Chuyển hướng product detail -->
            <RouterLink
              :to="{
                name: 'productDetail',
                params: { productId: p.id },
              }"
            >
              <h5 class="font-weight-normal mt-3 text-dark product-title">
                {{ p.name }}
              </h5>
            </RouterLink>

            <p class="text-sm text-secondary mb-2">{{ p.targetAudience }} · {{ p.brand }}</p>

            <!-- Giá -->
            <div class="d-flex justify-content-center align-items-center gap-2">
              <span class="text-muted text-decoration-line-through">
                {{ ulti.formatVND(p.minPrice) }}
              </span>
              <h6 class="text-danger mb-0">
                {{ ulti.formatVND(p.discountedPrice ?? p.minPrice) }}
              </h6>
            </div>
          </div>

          <hr class="dark horizontal my-0" />

          <!-- Footer -->
          <div class="card-footer text-center">
            <button class="btn bg-gradient-danger mb-0 w-100">Mua ngay</button>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="my-5 container">
    <div class="d-flex justify-content-between mx-auto gap-5 py-5 align-items-stretch">
      <div class="w-50 d-flex justify-content-center align-items-center h-100">
        <img src="/images/thum-nail.jpg" alt="" class="img-fluid rounded-5" />
      </div>
      <div class="w-50 d-flex flex-column gap-3 justify-content-center h-100">
        <div class="fs-1 fw-bold">Bóng đá</div>
        <div class="fs-5 lh-lg">
          Mỗi đường chuyền là một cơ hội, mỗi cú sút là một giấc mơ và mỗi trận đấu là một hành
          trình chinh phục. Hãy trang bị cho mình những sản phẩm tốt nhất để tận hưởng trọn vẹn niềm
          đam mê bóng đá và tạo nên những khoảnh khắc đáng nhớ trên sân cỏ.
        </div>
      </div>
    </div>
  </div>
  <div class="fw-bolder text-center mt-5" style="font-size: 100px">SPOTLIGHT</div>
  <div class="d-flex justify-content-center mx-auto flex-wrap py-5 align-items-stretch w-75">
    <RouterLink
      :to="{
        name: 'product',
        query: {
          product: s.id,
          name: s.name,
        },
      }"
      class="d-flex flex-column align-items-center justify-content-end w-20 my-5 text-decoration-none text-dark text-hover"
      v-for="s in spotlightProducts"
      :key="s.id"
    >
      <img :src="url + '/' + s.img" alt="" class="img-fluid w-50" />
      <div class="fs-14 fw-semibold">{{ ulti.getFirstThreeWords(s.name) }}</div>
    </RouterLink>
  </div>
</template>

<script setup>
import { useProductStore } from '@/stores/useProductStore'
import ulti from '@/ulti/ulti'
import { storeToRefs } from 'pinia'
import { onMounted } from 'vue'

const url = 'http://localhost:8080/uploads/images'

const productStore = useProductStore()
const { spotlightProducts, productOnSale } = storeToRefs(productStore)

onMounted(async () => {
  await Promise.all([productStore.fetchSpotlightProducts(), productStore.fetchProductOnSale()])
})
</script>

<template>
  <div class="mb-5">
    <img src="/images/slide_index_1.jpg" alt="" class="img-fluid w-100" />
  </div>
  <div class="my-5 container">
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
        v-for="s in spotlightList"
        :key="s.id"
      >
        <img :src="url + '/' + s.img" alt="" class="img-fluid w-50" />
        <div class="fs-14 fw-semibold">{{ ulti.getFirstThreeWords(s.name) }}</div>
      </RouterLink>
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
</template>

<script setup>
import { useProductStore } from '@/stores/useProductStore'
import ulti from '@/ulti/ulti'
import { storeToRefs } from 'pinia'
import { onMounted } from 'vue'

const url = 'http://localhost:8080/uploads/images'

const productStore = useProductStore()

onMounted(async () => {
  await productStore.fetchSpotlightProducts()
})

const { spotlightProducts: spotlightList } = storeToRefs(productStore)
</script>

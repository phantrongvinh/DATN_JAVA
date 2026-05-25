<template>
  <div class="mb-5">
    <img src="/images/slide_index_1.jpg" alt="" class="img-fluid w-100" />
  </div>
  <div class="my-5 container">
    <div class="fw-bolder text-center mt-5" style="font-size: 100px">SPOTLIGHT</div>
    <div class="d-flex justify-content-center mx-auto flex-wrap py-5 align-items-stretch w-75">
      <RouterLink
        to="/"
        class="d-flex flex-column align-items-center justify-content-end w-20 my-5 text-decoration-none text-dark text-hover"
        v-for="s in spotlightList"
        :key="s.id"
      >
        <img :src="url + '/' + s.img" alt="" class="img-fluid w-50" />
        <div class="fs-14 fw-semibold">{{ ulti.getFirstThreeWords(s.name) }}</div>
      </RouterLink>
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
console.log(spotlightList)
</script>

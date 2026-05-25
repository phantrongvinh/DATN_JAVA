<template>
  <div class="bg-light">
    <div class="d-flex py-1 justify-content-between align-items-center container-fluid px-3 py-2">
      <i class="fa-regular fa-futbol fs-6"></i>
      <div class="d-flex align-items-center">
        <RouterLink to="/login" class="text-decoration-none text-dark fw-semibold fs-14 text-hover"
          >Sign in</RouterLink
        >
        <i class="fa-regular fa-user fs-6 d-none"></i>
      </div>
    </div>
  </div>
  <div class="sticky-top bg-white">
    <div class="container-fluid">
      <nav class="px-3">
        <div class="row py-2">
          <div class="col-lg-4">
            <i class="fa-solid fa-fire-flame-simple fs-2"></i>
          </div>
          <div class="col-lg-4">
            <ul class="nav nav-underline justify-content-around">
              <li class="nav-item">
                <RouterLink to="/" class="nav-link text-decoration-none text-dark fw-semibold"
                  >Home</RouterLink
                >
              </li>
              <li
                class="nav-item dropdown position-static"
                v-for="audience in audienceList"
                :key="audience.id"
              >
                <RouterLink
                  :to="{
                    name: 'audience',
                    params: { audience: audience.name.toLowerCase() },
                  }"
                  class="nav-link text-decoration-none text-dark fw-semibold"
                  >{{ ulti.formatLabel(audience.name) }}</RouterLink
                >
                <ul
                  class="dropdown-menu mt-2 border-0 rounded-0 start-0 end-0 d-flex justify-content-center"
                >
                  <div class="w-50 row my-3">
                    <div
                      class="col-lg-3 text-center my-2"
                      v-for="brand in brandList"
                      :key="brand.id"
                    >
                      <li>
                        <RouterLink
                          class="text-decoration-none fs-14 text-secondary lh-lg fw-semibold"
                          :to="{
                            name: 'audience',
                            params: {
                              audience: audience.name.toLowerCase(),
                            },
                            query: {
                              brand: brand.name,
                            },
                          }"
                        >
                          {{ brand.name }}
                        </RouterLink>
                      </li>
                    </div>
                  </div>
                </ul>
              </li>
              <li class="nav-item dropdown position-static">
                <RouterLink
                  to="/accesories"
                  class="nav-link text-decoration-none text-dark fw-semibold"
                  >Accessories</RouterLink
                >
                <ul
                  class="dropdown-menu mt-2 border-0 rounded-0 start-0 end-0 d-flex justify-content-center"
                >
                  <div class="w-50 row my-3">
                    <div
                      class="col-lg-3 text-center my-2"
                      v-for="accesory in accesoryList"
                      :key="accesory.id"
                    >
                      <li>
                        <RouterLink
                          class="text-decoration-none fs-14 text-secondary lh-lg fw-semibold"
                          to="/"
                        >
                          {{ accesory.name }}
                        </RouterLink>
                      </li>
                    </div>
                  </div>
                </ul>
              </li>
            </ul>
          </div>
          <div class="col-lg-4">
            <div class="d-flex justify-content-end">
              <form class="d-flex" role="search">
                <input
                  class="form-control me-2"
                  type="search"
                  placeholder="Search"
                  aria-label="Search"
                />
                <button class="btn btn-outline-dark" type="submit">Search</button>
              </form>
              <div class="mx-3 d-flex align-items-center">
                <i class="fa-regular fa-heart fs-4"></i>
              </div>

              <div class="d-flex align-items-center">
                <i class="fa-brands fa-opencart fs-4"></i>
              </div>
            </div>
          </div>
        </div>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { useAudienceStore } from '@/stores/useAudienceStore'
import { useBrandStore } from '@/stores/useBrandStore'
import { useCategoryStore } from '@/stores/useCategoryStore'
import ulti from '@/ulti/ulti'
import { storeToRefs } from 'pinia'
import { onMounted } from 'vue'

// stores
const audienceStore = useAudienceStore()
const brandStore = useBrandStore()
const categoryStore = useCategoryStore()

onMounted(async () => {
  await audienceStore.fetchAudiences()
  await brandStore.fetchBrand()
  await categoryStore.fetchAccessory()
})

const { audiences: audienceList } = storeToRefs(audienceStore)
const { brands: brandList } = storeToRefs(brandStore)
const { accessories: accesoryList } = storeToRefs(categoryStore)
</script>

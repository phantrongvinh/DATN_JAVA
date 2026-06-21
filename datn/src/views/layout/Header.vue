<template>
  <div class="bg-gradient-danger">
    <div class="d-flex py-1 justify-content-between align-items-center container-fluid px-5 py-2">
      <i class="fa-regular fa-futbol fs-6 text-white"></i>
      <div class="d-flex align-items-center">
        <div class="dropdown ms-3" v-if="isAuthenticated">
          <a href="#" class="text-dark" data-bs-toggle="dropdown" aria-expanded="false">
            <i class="fa-regular fa-user fs-5 cursor-pointer text-white"></i>
          </a>

          <ul
            class="dropdown-menu dropdown-menu-end px-2 py-3 shadow border-0 z-100"
            style="min-width: 320px"
          >
            <li>
              <div class="d-flex align-items-center mb-3 px-2">
                <img
                  src="/images/avatar-default.svg"
                  alt=""
                  class="rounded-circle"
                  width="50"
                  height="50"
                />

                <div class="ms-3">
                  <h6 class="mb-0">
                    {{ user?.fullName }}
                  </h6>

                  <p class="text-sm text-secondary mb-0">
                    {{ user?.email }}
                  </p>
                </div>
              </div>
            </li>

            <li>
              <hr class="horizontal dark my-2" />
            </li>

            <li>
              <RouterLink class="dropdown-item border-radius-md" to="/profile">
                <i class="fa-regular fa-user me-2"></i>
                Hồ sơ cá nhân
              </RouterLink>
            </li>

            <li>
              <a
                class="dropdown-item border-radius-md text-danger"
                href="#"
                @click.prevent="handleLogout"
              >
                <i class="fa-solid fa-right-from-bracket me-2"></i>
                Đăng xuất
              </a>
            </li>
          </ul>
        </div>
        <RouterLink
          to="/login"
          class="text-decoration-none fw-semibold fs-14 text-hover text-white"
          v-else
          >Sign in</RouterLink
        >
      </div>
    </div>
  </div>
  <div class="sticky-top bg-white border-bottom">
    <div class="container-fluid">
      <nav class="px-5">
        <div class="row py-2">
          <div class="col-lg-4">
            <i class="fa-solid fa-fire-flame-simple fs-2"></i>
          </div>
          <div class="col-lg-4">
            <ul class="nav nav-underline justify-content-around">
              <li class="nav-item">
                <RouterLink to="/" class="nav-link text-dark fw-semibold"> Home </RouterLink>
              </li>

              <li
                class="nav-item dropdown position-static"
                v-for="audience in audienceList"
                :key="audience.id"
              >
                <a href="#" class="nav-link text-dark fw-semibold" data-bs-toggle="dropdown">
                  {{ ulti.formatLabel(audience.name) }}
                </a>

                <div class="dropdown-menu border-0 rounded-0 mt-2 start-0 end-0 shadow">
                  <div class="container py-3">
                    <div class="row">
                      <div class="col-lg-3 col-md-4 col-6 mb-3">
                        <RouterLink
                          class="text-decoration-none text-menu fw-semibold"
                          :to="{
                            name: 'product',
                            query: {
                              audienceIds: audience.id,
                            },
                          }"
                        >
                          Tất cả
                        </RouterLink>
                      </div>
                      <div
                        class="col-lg-3 col-md-4 col-6 mb-3"
                        v-for="brand in brandList"
                        :key="brand.id"
                      >
                        <RouterLink
                          class="text-decoration-none text-menu fw-semibold"
                          :to="{
                            name: 'product',
                            query: {
                              audienceIds: audience.id,
                              brandIds: brand.id,
                            },
                          }"
                        >
                          {{ brand.name }}
                        </RouterLink>
                      </div>
                    </div>
                  </div>
                </div>
              </li>

              <li class="nav-item dropdown position-static">
                <a href="#" class="nav-link text-dark fw-semibold" data-bs-toggle="dropdown">
                  Accessories
                </a>

                <div class="dropdown-menu border-0 rounded-0 mt-2 start-0 end-0 shadow">
                  <div class="container py-3">
                    <div class="row">
                      <div
                        class="col-lg-3 col-md-4 col-6 mb-3"
                        v-for="accessory in accesoryList"
                        :key="accessory.id"
                      >
                        <RouterLink
                          class="text-decoration-none text-menu fw-semibold"
                          :to="{
                            name: 'product',
                            query: {
                              accessoryIds: accessory.id,
                            },
                          }"
                        >
                          {{ accessory.name }}
                        </RouterLink>
                      </div>
                    </div>
                  </div>
                </div>
              </li>
            </ul>
          </div>
          <div class="col-lg-4">
            <div class="d-flex justify-content-end">
              <div class="input-group input-group-outline w-50">
                <label class="form-label">Tìm kiếm sản phẩm</label>
                <input class="form-control me-2" type="search" aria-label="Search" />
              </div>
              <div class="mx-3 d-flex align-items-center">
                <i class="fa-regular fa-heart fs-4"></i>
              </div>

              <RouterLink to="/cart" class="d-flex align-items-center">
                <i class="fa-brands fa-opencart fs-4"></i>
              </RouterLink>
            </div>
          </div>
        </div>
      </nav>
    </div>
  </div>
</template>

<script setup>
import { useAudienceStore } from '@/stores/useAudienceStore'
import { useAuthStore } from '@/stores/useAuthStore'
import { useBrandStore } from '@/stores/useBrandStore'
import { useCategoryStore } from '@/stores/useCategoryStore'
import ulti from '@/ulti/ulti'
import { storeToRefs } from 'pinia'
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// stores
const audienceStore = useAudienceStore()
const brandStore = useBrandStore()
const categoryStore = useCategoryStore()
const authStore = useAuthStore()

onMounted(async () => {
  await Promise.all([
    audienceStore.fetchAudiences(),
    brandStore.fetchBrand(),
    categoryStore.fetchAccessory(),
  ])
})

const { audiences: audienceList } = storeToRefs(audienceStore)
const { brands: brandList } = storeToRefs(brandStore)
const { accessories: accesoryList } = storeToRefs(categoryStore)
const isAuthenticated = computed(() => authStore.isAuthenticated)
const { user } = storeToRefs(authStore)

// logout
const handleLogout = () => {
  try {
    authStore.logout()
    router.push('/')
    return { success: true }
  } catch (error) {
    return { success: false }
  }
}
</script>

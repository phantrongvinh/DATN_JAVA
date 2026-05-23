<template>
  <div class="bg-light">
    <div class="d-flex py-1 justify-content-between align-items-center container-fluid px-5 py-2">
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
      <nav class="px-5">
        <div class="row py-2">
          <div class="col-lg-4">
            <i class="fa-solid fa-fire-flame-simple fs-2"></i>
          </div>
          <div class="col-lg-4">
            <ul class="nav nav-underline justify-content-center">
              <li class="nav-item px-3">
                <RouterLink to="/" class="nav-link text-decoration-none text-dark fw-semibold"
                  >Home</RouterLink
                >
              </li>
              <li
                class="nav-item dropdown position-static px-3"
                v-for="gender in genderList"
                :key="gender.id"
              >
                <RouterLink
                  :to="{ name: 'gender', params: { gender: gender.name.toLowerCase() } }"
                  class="nav-link text-decoration-none text-dark fw-semibold"
                  >{{ ulti.formatLabel(gender.name) }}</RouterLink
                >
                <ul
                  class="dropdown-menu mt-2 border-0 rounded-0 start-0 end-0 d-flex justify-content-center"
                >
                  <div class="w-50 row my-3">
                    <div class="col-lg-3 text-center">
                      <li>
                        <a class="dropdown-item" href="#"> Jerseys </a>
                      </li>
                    </div>
                    <div class="col-lg-3 text-center">
                      <li>
                        <a class="dropdown-item" href="#"> Shoes </a>
                      </li>
                    </div>
                    <div class="col-lg-3 text-center">
                      <li>
                        <a class="dropdown-item" href="#"> Jerseys </a>
                      </li>
                    </div>
                    <div class="col-lg-3 text-center">
                      <li>
                        <a class="dropdown-item" href="#"> Shoes </a>
                      </li>
                    </div>
                  </div>
                </ul>
              </li>

              <li class="nav-item px-3">
                <RouterLink
                  to="/accesories"
                  class="nav-link text-decoration-none text-dark fw-semibold"
                  >Accessories</RouterLink
                >
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
                <button class="btn btn-outline-success" type="submit">Search</button>
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
import { useGenderStore } from '@/stores/useGenderStore'
import ulti from '@/ulti/ulti'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref, watch } from 'vue'

const genderStore = useGenderStore()

const { genders: genderList } = storeToRefs(genderStore)
</script>

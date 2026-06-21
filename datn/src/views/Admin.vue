<template>
  <div class="container-fluid py-4">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-xl-2 col-lg-3">
        <div class="card position-sticky top-7 shadow-lg" style="min-height: 90vh">
          <div class="card-header bg-transparent text-center border-bottom">
            <h4 class="text-dark mb-0">Welcome ADMIN</h4>
          </div>

          <div class="card-body p-2">
            <div class="nav-wrapper position-relative end-0">
              <ul class="nav nav-pills nav-fill flex-column bg-transparent p-1">
                <li class="nav-item">
                  <button
                    class="nav-link text-start"
                    :class="{
                      'active bg-gradient-danger text-white shadow': activeTab === 'overview',
                    }"
                    @click="activeTab = 'overview'"
                  >
                    <i class="fa-solid fa-chart-line me-2"></i>
                    Tổng quan
                  </button>
                </li>

                <li class="nav-item mt-2">
                  <button
                    class="nav-link text-start"
                    :class="{
                      'active bg-gradient-danger text-white shadow': activeTab === 'product',
                    }"
                    @click="activeTab = 'product'"
                  >
                    <i class="fa-solid fa-box me-2"></i>
                    Hàng hóa
                  </button>
                </li>

                <li class="nav-item mt-2">
                  <button
                    class="nav-link text-start"
                    :class="{
                      'active bg-gradient-danger text-white shadow': activeTab === 'user',
                    }"
                    @click="activeTab = 'user'"
                  >
                    <i class="fa-solid fa-users me-2"></i>
                    Người dùng
                  </button>
                </li>
                <li class="nav-item mt-2">
                  <button
                    class="nav-link text-start"
                    :class="{
                      'active bg-gradient-danger text-white shadow': activeTab === 'order',
                    }"
                    @click="activeTab = 'order'"
                  >
                    <i class="fa-solid fa-cart-shopping me-2"></i>
                    Đơn hàng
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- Main Content -->
      <div class="col-xl-10 col-lg-9">
        <!-- Header -->
        <div class="card mb-4">
          <div class="card-body d-flex justify-content-between align-items-center">
            <h4 class="mb-0 fw-bold">Dashboard</h4>

            <div class="font-weight-bolder">
              Hôm nay:
              {{ ulti.formatDate(new Date()) }}
            </div>
          </div>
        </div>

        <!-- Content -->
        <div class="card shadow-sm">
          <div class="card-body">
            <Overview
              :activeTab="activeTab"
              @update:activeTab="activeTab = $event"
              v-if="activeTab === 'overview'"
            />

            <ProductView v-else-if="activeTab === 'product'" />

            <Account v-else-if="activeTab === 'user'" />

            <div v-else>đơn hàng</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Overview from '@/components/admin/Overview.vue'
import ProductView from '@/components/admin/ProductView.vue'

import ulti from '@/ulti/ulti'
import { ref } from 'vue'

const activeTab = ref('overview')
</script>

<style scoped>
/* ADMIN */
</style>

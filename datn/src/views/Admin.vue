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
            <button
              class="nav-link p-2 w-100 text-start mb-2"
              :class="
                activeTab === 'overview'
                  ? 'active bg-gradient-danger text-dark shadow'
                  : 'text-dark'
              "
              @click="activeTab = 'overview'"
            >
              <i class="fa-solid fa-chart-line me-2"></i>
              Tổng quan
            </button>

            <button
              class="nav-link p-2 w-100 text-start mb-2"
              :class="
                activeTab === 'product' ? 'active bg-gradient-danger text-dark shadow' : 'text-dark'
              "
              @click="activeTab = 'product'"
            >
              <i class="fa-solid fa-box me-2"></i>
              Hàng hóa
            </button>

            <button
              class="nav-link p-2 w-100 text-start mb-2"
              :class="
                activeTab === 'user' ? 'active bg-gradient-danger text-dark shadow' : 'text-dark'
              "
              @click="activeTab = 'user'"
            >
              <i class="fa-solid fa-users me-2"></i>
              Người dùng
            </button>

            <button
              class="nav-link p-2 w-100 text-start"
              :class="
                activeTab === 'order' ? 'active bg-gradient-danger text-dark shadow' : 'text-dark'
              "
              @click="activeTab = 'order'"
            >
              <i class="fa-solid fa-cart-shopping me-2"></i>
              Đơn hàng
            </button>
          </div>
        </div>
      </div>

      <!-- Main Content -->
      <div class="col-xl-10 col-lg-9">
        <!-- Header -->
        <div class="card mb-4">
          <div class="card-body d-flex justify-content-between align-items-center">
            <h4 class="mb-0 fw-bold">Dashboard</h4>

            <div class="text-secondary">
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
import Account from '@/components/admin/Account.vue'
import Overview from '@/components/admin/Overview.vue'
import ProductView from '@/components/admin/ProductView.vue'
import ulti from '@/ulti/ulti'
import { ref } from 'vue'

const activeTab = ref('overview')
</script>

<style scoped>
/* ADMIN */
.nav-link {
  border-radius: 0.75rem;
  transition: 0.3s;
  font-weight: 600;
}

.nav-link:hover:not(.active) {
  background: rgba(255, 255, 255, 0.1);
}
</style>

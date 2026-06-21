<template>
  <div class="container-fluid py-5">
    <div class="row">
      <!-- Sidebar -->
      <div class="col-lg-3">
        <div class="card">
          <div class="card-body">
            <div class="nav-wrapper position-relative end-0">
              <ul class="nav nav-pills nav-fill flex-column bg-transparent p-1">
                <li class="nav-item">
                  <button
                    class="nav-link text-start"
                    :class="{
                      'active bg-gradient-danger text-white shadow': activeTab === 'profile',
                    }"
                    @click="activeTab = 'profile'"
                  >
                    <i class="fa-regular fa-user me-2"></i>
                    Hồ sơ
                  </button>
                </li>

                <li class="nav-item mt-2">
                  <button
                    class="nav-link text-start"
                    :class="{
                      'active bg-gradient-danger text-white shadow': activeTab === 'like',
                    }"
                    @click="activeTab = 'like'"
                  >
                    <i class="fa-regular fa-heart me-2"></i>
                    Yêu thích
                  </button>
                </li>

                <li class="nav-item mt-2">
                  <button
                    class="nav-link text-start"
                    :class="{
                      'active bg-gradient-danger text-white shadow': activeTab === 'password',
                    }"
                    @click="activeTab = 'password'"
                  >
                    <i class="fa-solid fa-key me-2"></i>
                    Đổi mật khẩu
                  </button>
                </li>

                <li class="nav-item mt-2">
                  <button
                    class="nav-link text-start"
                    :class="{
                      'active bg-gradient-danger text-white shadow': activeTab === 'update',
                    }"
                    @click="activeTab = 'update'"
                  >
                    <i class="fa-solid fa-pen me-2"></i>
                    Cập nhật thông tin
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- Content -->
      <div class="col-lg-9">
        <div class="card">
          <div class="card-body">
            <div v-if="activeTab === 'profile'">
              <h5>Đơn hàng</h5>

              <div class="row mt-4">
                <div class="col-lg-8">
                  <div class="card shadow-sm">
                    <div class="card-body">đơn hàng 1</div>
                  </div>
                </div>

                <div class="col-lg-4">
                  <div class="card shadow-sm">
                    <div class="card-body text-center">
                      <img
                        src="/images/avatar-default.svg"
                        class="rounded-circle mb-3"
                        width="100"
                      />

                      <h6>{{ user?.fullName }}</h6>

                      <p class="text-sm text-secondary mb-1">
                        {{ user?.email }}
                      </p>

                      <p class="text-sm text-secondary mb-1">
                        {{ ulti.formatDate(user?.birthDay) }}
                      </p>

                      <p class="text-sm text-secondary">
                        {{ user?.phone }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div v-else-if="activeTab === 'like'">Like</div>

            <div v-else-if="activeTab === 'password'">Change Password</div>

            <div v-else-if="activeTab === 'update'">Update Profile</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/useAuthStore'
import ulti from '@/ulti/ulti'
import { storeToRefs } from 'pinia'
import { ref } from 'vue'

const authStore = useAuthStore()

const { user } = storeToRefs(authStore)

const activeTab = ref('profile')
</script>

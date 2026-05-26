<template>
  <div class="container-fluid px-5 mt-5">
    <div class="fw-semibold" style="font-size: 32px">Hồ sơ</div>
    <div class="row mt-5">
      <div class="col-lg-2">
        <div class="d-flex align-items-start">
          <ul class="nav nav-underline flex-column" id="myTab" role="tablist">
            <li class="nav-item" role="presentation">
              <button
                class="nav-link text-hover active fw-semibold lh-lg"
                id="pills-profile-tab"
                data-bs-toggle="pill"
                data-bs-target="#pills-profile"
                type="button"
                role="tab"
                aria-controls="pills-profile"
                aria-selected="true"
              >
                Hồ sơ
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button
                class="nav-link text-hover fw-semibold lh-lg"
                id="pills-like-tab"
                data-bs-toggle="pill"
                data-bs-target="#pills-like"
                type="button"
                role="tab"
                aria-controls="pills-like"
                aria-selected="true"
              >
                Yêu thích
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button
                class="nav-link text-hover fw-semibold lh-lg"
                id="pills-change-pass-tab"
                data-bs-toggle="pill"
                data-bs-target="#pills-change-pass"
                type="button"
                role="tab"
                aria-controls="pills-change-pass"
                aria-selected="true"
              >
                Đổi mật khẩu
              </button>
            </li>
            <li class="nav-item" role="presentation">
              <button
                class="nav-link text-hover fw-semibold lh-lg"
                id="pills-update-profile-tab"
                data-bs-toggle="pill"
                data-bs-target="#pills-update-profile"
                type="button"
                role="tab"
                aria-controls="pills-update-profile"
                aria-selected="true"
              >
                Cập nhật thông tin
              </button>
            </li>
          </ul>
        </div>
      </div>
      <div class="col-lg-10 border-start">
        <div class="d-flex justify-content-center">
          <div class="tab-content w-100" id="pills-tabContent">
            <div
              class="tab-pane fade show active"
              id="pills-profile"
              role="tabpanel"
              aria-labelledby="pills-profile-tab"
              tabindex="0"
            >
              <div class="d-flex gap-4">
                <div class="w-75">
                  <div class="mx-4">
                    <div class="fw-bolder fs-4">Đơn hàng</div>
                  </div>

                  <div class="border rounded shadow-sm m-4">
                    <div class="m-4">đnow hàng 1</div>
                  </div>
                </div>

                <div class="w-25 shadow-sm rounded border">
                  <div class="d-flex justify-content-center m-4">
                    <img
                      src="/images/avatar-default.svg"
                      alt=""
                      class="img-fluid rounded-circle w-25"
                    />
                  </div>
                  <div class="d-flex flex-column align-items-start mx-5 my-2">
                    <div class="fs-14 my-2 fw-bold">{{ user?.fullName }}</div>
                    <div class="fs-14 my-2 fw-semibold">{{ user?.email }}</div>

                    <div class="fs-14 my-2 fw-semibold">{{ ulti.formatDate(user?.birthDay) }}</div>
                    <div class="fs-14 my-2 fw-semibold">{{ user?.phone }}</div>
                  </div>
                </div>
              </div>
            </div>
            <div
              class="tab-pane fade"
              id="pills-like"
              role="tabpanel"
              aria-labelledby="pills-like-tab"
              tabindex="0"
            >
              Like
            </div>
            <div
              class="tab-pane fade"
              id="pills-change-pass"
              role="tabpanel"
              aria-labelledby="pills-change-pass-tab"
              tabindex="0"
            >
              Change pass
            </div>
            <div
              class="tab-pane fade"
              id="pills-update-profile"
              role="tabpanel"
              aria-labelledby="pills-update-profile-tab"
              tabindex="0"
            >
              Update Profile
            </div>
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

const authStore = useAuthStore()

const { user } = storeToRefs(authStore)
</script>

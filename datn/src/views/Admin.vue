<template>
  <div class="row w-100">
    <div class="col-lg-2 d-flex">
      <div class="border-end w-100 h-100 bg-light pb-5">
        <div class="fw-semibold text-center px-5 pt-5 btn-primary" style="font-size: 28px">
          Welcome ADMIN
        </div>
        <div class="mt-5">
          <div class="d-flex">
            <ul class="nav flex-column w-100 mx-2" id="myTab" role="tablist">
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link text-hover active fw-semibold lh-lg w-100 btn-custom mt-3 rounded-3"
                  id="pills-all-tab"
                  data-bs-toggle="pill"
                  data-bs-target="#pills-all"
                  type="button"
                  role="tab"
                  aria-controls="pills-all"
                  aria-selected="true"
                >
                  Tổng quan
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link text-hover fw-semibold lh-lg w-100 btn-custom mt-3 rounded-3"
                  id="pills-product-tab"
                  data-bs-toggle="pill"
                  data-bs-target="#pills-product"
                  type="button"
                  role="tab"
                  aria-controls="pills-product"
                  aria-selected="true"
                >
                  Hàng hóa
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link text-hover fw-semibold lh-lg w-100 btn-custom mt-3 rounded-3"
                  id="pills-user-tab"
                  data-bs-toggle="pill"
                  data-bs-target="#pills-user"
                  type="button"
                  role="tab"
                  aria-controls="pills-user"
                  aria-selected="true"
                >
                  Người dùng
                </button>
              </li>
              <li class="nav-item" role="presentation">
                <button
                  class="nav-link text-hover fw-semibold lh-lg w-100 btn-custom mt-3 rounded-3"
                  id="pills-order-tab"
                  data-bs-toggle="pill"
                  data-bs-target="#pills-order"
                  type="button"
                  role="tab"
                  aria-controls="pills-order"
                  aria-selected="true"
                >
                  Đơn hàng
                </button>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <div class="col-lg-10 d-flex py-5">
      <div class="w-100 h-100">
        <div class="text-end fw-bold fs-5 w-100">
          Hôm nay: {{ ulti.formatDate(new Date(Date.now())) }}
        </div>
        <div class="d-flex justify-content-center mt-5">
          <div class="tab-content w-100" id="pills-tabContent">
            <div
              class="tab-pane fade show active"
              id="pills-all"
              role="tabpanel"
              aria-labelledby="pills-all-tab"
              tabindex="0"
            >
              <Overview />
            </div>
            
            <div
              class="tab-pane fade"
              id="pills-product"
              role="tabpanel"
              aria-labelledby="pills-product-tab"
              tabindex="0"
            >
              <ProductView />
            </div>
            
            <div
              class="tab-pane fade"
              id="pills-user"
              role="tabpanel"
              aria-labelledby="pills-user-tab"
              tabindex="0"
            >
              <UserView />
            </div>
            
            <div
              class="tab-pane fade"
              id="pills-order"
              role="tabpanel"
              aria-labelledby="pills-order-tab"
              tabindex="0"
            >
              <OrderView />
            </div>
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

import UserView from '@/components/admin/UserView.vue'
import OrderView from '@/components/admin/OrderView.vue'


</script>
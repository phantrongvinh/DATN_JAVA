<template>
  <!-- Thống kê -->
  <div class="row">
    <div class="col-xl-3 col-sm-6 mb-4">
      <div class="card">
        <div class="card-body p-3">
          <div class="row">
            <div class="col-8">
              <p class="text-sm mb-0 text-uppercase font-weight-bold">Tổng doanh thu</p>

              <h5 class="font-weight-bolder">123456</h5>

              <p class="mb-0 text-success text-sm">↑ +12%</p>
            </div>

            <div class="col-4 text-end">
              <div class="icon icon-shape bg-gradient-success shadow text-center border-radius-md">
                <i class="fa-solid fa-dollar-sign text-lg opacity-10"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Tương tự cho Đơn hàng, Nhập hàng, Xuất hàng -->
    <div class="col-xl-3 col-sm-6 mb-4">
      <div class="card">
        <div class="card-body p-3">
          <div class="row">
            <div class="col-8">
              <p class="text-sm mb-0 text-uppercase font-weight-bold">Tổng doanh thu</p>

              <h5 class="font-weight-bolder">123456</h5>

              <p class="mb-0 text-success text-sm">↑ +12%</p>
            </div>

            <div class="col-4 text-end">
              <div class="icon icon-shape bg-gradient-success shadow text-center border-radius-md">
                <i class="fa-solid fa-dollar-sign text-lg opacity-10"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col-xl-3 col-sm-6 mb-4">
      <div class="card">
        <div class="card-body p-3">
          <div class="row">
            <div class="col-8">
              <p class="text-sm mb-0 text-uppercase font-weight-bold">Tổng doanh thu</p>

              <h5 class="font-weight-bolder">123456</h5>

              <p class="mb-0 text-success text-sm">↑ +12%</p>
            </div>

            <div class="col-4 text-end">
              <div class="icon icon-shape bg-gradient-success shadow text-center border-radius-md">
                <i class="fa-solid fa-dollar-sign text-lg opacity-10"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="col-xl-3 col-sm-6 mb-4">
      <div class="card">
        <div class="card-body p-3">
          <div class="row">
            <div class="col-8">
              <p class="text-sm mb-0 text-uppercase font-weight-bold">Tổng doanh thu</p>

              <h5 class="font-weight-bolder">123456</h5>

              <p class="mb-0 text-success text-sm">↑ +12%</p>
            </div>

            <div class="col-4 text-end">
              <div class="icon icon-shape bg-gradient-success shadow text-center border-radius-md">
                <i class="fa-solid fa-dollar-sign text-lg opacity-10"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- Chart -->
  <div class="row mt-4">
    <div class="col-lg-6 mb-4">
      <div class="card">
        <div class="card-header pb-0">
          <div class="d-flex justify-content-between">
            <h6>Thống kê nhập xuất</h6>

            <RouterLink to="/" class="text-primary text-sm fw-bold"> Xem tất cả </RouterLink>
          </div>
        </div>

        <div class="card-body">
          <!-- Chart -->
        </div>
      </div>
    </div>

    <div class="col-lg-6 mb-4">
      <div class="card">
        <div class="card-header pb-0">
          <div class="d-flex justify-content-between">
            <h6>Sản phẩm bán chạy</h6>

            <RouterLink to="/" class="text-primary text-sm fw-bold"> Xem tất cả </RouterLink>
          </div>
        </div>

        <div class="card-body">...</div>
      </div>
    </div>
  </div>

  <!-- Product top -->
  <div class="card card-body mb-3 shadow-sm">
    <div class="d-flex align-items-center">
      <div class="me-3 fw-bold">1</div>

      <img class="avatar avatar-xl me-3" src="" />

      <div class="flex-grow-1">
        <h6 class="mb-0">Giày bóng đá</h6>

        <span class="text-sm text-secondary"> Đã bán: 120 </span>
      </div>

      <div class="text-end">
        <div class="text-sm">Doanh thu</div>

        <h6 class="text-success mb-0">123456789</h6>
      </div>
    </div>
  </div>

  <!-- Overview -->
  <div class="mt-4">
    <!-- Product -->
    <div class="mb-4">
      <div class="card h-100">
        <div class="card-header pb-0">
          <div class="d-flex justify-content-between align-items-center">
            <h6>Quản lý hàng hóa</h6>

            <button class="btn btn-link text-primary p-0 mb-0" @click="hanleChange">
              Xem tất cả
            </button>
          </div>
        </div>

        <div class="card-body">
          <!-- TABLE COMPONENT PRODUCT -->
          <Table :initialValues="initialProduct" :productOverview="productOverview"></Table>
        </div>
      </div>
    </div>

    <!-- User -->
    <div class="mb-4">
      <div class="card h-100">
        <div class="card-header pb-0">
          <div class="d-flex justify-content-between">
            <h6>Khách hàng</h6>

            <button class="btn btn-link text-primary p-0 mb-0" @click="goToTab('user')">
              Xem tất cả
            </button>
          </div>
        </div>

        <div class="card-body">
          <!-- TABLE COMPONENT USER -->
          <Table :initialValues="initialUser"></Table>
        </div>
      </div>
    </div>

    <!-- Order -->
    <div class="mb-4">
      <div class="card h-100">
        <div class="card-header pb-0">
          <div class="d-flex justify-content-between">
            <h6>Đơn hàng</h6>

            <button class="btn btn-link text-primary p-0 mb-0" @click="props.activeTab = 'product'">
              Xem tất cả
            </button>
          </div>
        </div>

        <div class="card-body">
          <!-- TABLE COMPONENT ORDER -->
          <Table :initialValues="intialOrder"></Table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import aminAPI from '@/api/adminAPI'
import Table from '@/components/Table.vue'
import { useProductStore } from '@/stores/useProductStore'
import ulti from '@/ulti/ulti'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref } from 'vue'

// table product
const initialProduct = {
  sanPham: 'Sản phẩm',
  danhMuc: 'Danh mục',
  hang: 'Hãng',
  giaBan: 'Giá bán',
  trangThai: 'Trạng thái',
}

const productStore = useProductStore()

onMounted(async () => {
  await productStore.fetchProductOVerview()
})

const { productOverview } = storeToRefs(productStore)

// table user
const initialUser = { ten: 'Tên', email: 'Email', soDienThoai: 'SDT', trangThai: 'Trạng thái' }

// table order
const intialOrder = {
  maDonHang: 'Mã đơn hàng',
  khachHang: 'Khách hàng',
  tongTien: 'Tổng tiền',
  trangThai: 'Trạng thái',
}

// handle chuyển tabs
const props = defineProps({
  activeTab: String,
})

const emit = defineEmits(['update:activeTab'])

const hanleChange = () => {
  emit('update:activeTab', 'product')
}
</script>

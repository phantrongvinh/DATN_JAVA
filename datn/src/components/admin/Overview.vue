<template>
  <div class="d-flex gap-4 align-items-stretch">
    <div class="w-25 border border-success border-opacity-50 rounded-4">
      <div class="d-flex justify-content-between align-items-center m-3">
        <div class="d-flex flex-column gap-3">
          <div class="fw-bold fs-5">Tổng doanh thu</div>
          <div class="fw-bold fs-14">123456</div>
          <div class="text-success">-></div>
        </div>
        <div class="w-25 text-end">ICON</div>
      </div>
    </div>
    <div class="w-25 border border-info border-opacity-50 rounded-4">
      <div class="d-flex justify-content-between align-items-center m-3">
        <div class="d-flex flex-column gap-3">
          <div class="fw-bold fs-5">Tổng đơn hàng</div>
          <div class="fw-bold fs-14">123456</div>
          <div class="text-success">-></div>
        </div>
        <div class="w-25 text-end">ICON</div>
      </div>
    </div>
    <div class="w-25 border border-primary border-opacity-50 rounded-4">
      <div class="d-flex justify-content-between align-items-center m-3">
        <div class="d-flex flex-column gap-3">
          <div class="fw-bold fs-5">Tổng nhập hàng</div>
          <div class="fw-bold fs-14">123456</div>
          <div class="text-success">-></div>
        </div>
        <div class="w-25 text-end">ICON</div>
      </div>
    </div>
    <div class="w-25 border border-danger border-opacity-50 rounded-4">
      <div class="d-flex justify-content-between align-items-center m-3">
        <div class="d-flex flex-column gap-3">
          <div class="fw-bold fs-5">Tổng xuất hàng</div>
          <div class="fw-bold fs-14">123456</div>
          <div class="text-success">-></div>
        </div>
        <div class="w-25 text-end">ICON</div>
      </div>
    </div>
  </div>
  <div class="d-flex gap-4 mt-5 align-items-stretch">
    <div class="w-50 border border-opacity-50 rounded-4">
      <div class="m-4 d-flex justify-content-between">
        <div class="fw-bold fs-5">Thong ke nhap xuat</div>
        <RouterLink to="/" class="fs-14 fw-semibold text-decoration-none text-hover text-primary"
          >Xem tất cả</RouterLink
        >
      </div>
    </div>
    <div class="w-50 border border-opacity-50 rounded-4">
      <div class="m-4 d-flex justify-content-between">
        <div class="fw-bold fs-5">Thong ke nhap xuat</div>
        <RouterLink to="/" class="fs-14 fw-semibold text-decoration-none text-hover text-primary"
          >Xem tất cả</RouterLink
        >
      </div>
      <div class="mt-2">
        <div class="d-flex flex-column gap-2 mb-4">
          <div class="border border-opacity-50 rounded-4 mx-4">
            <div class="d-flex justify-content-between m-3">
              <div class="d-flex w-75">
                <div class="fw-bold fs-14">1</div>
                <img src="" alt="" class="img-fluid w-25 object-fit-contain" />
                <div class="d-flex flex-column justify-content-center gap-2">
                  <div class="fw-bold">Giay bong da</div>
                  <div class="fw-bold text-muted">Da ban: 120</div>
                </div>
              </div>
              <div class="w-25 d-flex flex-column align-items-end gap-2">
                <div class="fw-bold">Doanh thu</div>
                <div class="fw-bold text-success">123456789</div>
              </div>
            </div>
          </div>
          <div class="border border-opacity-50 rounded-4 mx-4">
            <div class="d-flex justify-content-between m-3">
              <div class="d-flex w-75">
                <div class="fw-bold fs-14">1</div>
                <img src="" alt="" class="img-fluid w-25 object-fit-contain" />
                <div class="d-flex flex-column justify-content-center gap-2">
                  <div class="fw-bold">Giay bong da</div>
                  <div class="fw-bold text-muted">Da ban: 120</div>
                </div>
              </div>
              <div class="w-25 d-flex flex-column align-items-end gap-2">
                <div class="fw-bold">Doanh thu</div>
                <div class="fw-bold text-success">123456789</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="mt-5 d-flex gap-3">
    <div class="border w-100 border-opacity-50 rounded-4">
      <div class="m-4">
        <div class="m-4 d-flex justify-content-between align-items-center">
          <div class="fw-bold fs-5">Quản ly hàng hóa</div>
          <button
            class="btn btn-transparent p-0 fs-14 fw-semibold text-decoration-none text-hover text-primary"
            @click.prevent="goToTab('pills-product-tab')"
          >
            Xem tất cả
          </button>
        </div>
        <!-- TABLE COMPONENT PRODUCT -->
        <Table :initialValues="initialProduct" :productOverview="productOverview"></Table>
      </div>
    </div>
    <div class="border w-100 border-opacity-50 rounded-4">
      <div class="m-4">
        <div class="m-4 d-flex justify-content-between align-items-center">
          <div class="fw-bold fs-5">Quản ly khach hang</div>
          <button
            class="btn btn-transparent p-0 fs-14 fw-semibold text-decoration-none text-hover text-primary"
            @click.prevent="goToTab('pills-user-tab')"
          >
            Xem tất cả
          </button>
        </div>
        <!-- TABLE COMPONENT USER -->
        <Table :initialValues="initialUser"></Table>
      </div>
    </div>
    <div class="border w-100 border-opacity-50 rounded-4">
      <div class="m-4">
        <div class="m-4 d-flex justify-content-between align-items-center">
          <div class="fw-bold fs-5">Quản ly don hang</div>
          <button
            class="btn btn-transparent p-0 fs-14 fw-semibold text-decoration-none text-hover text-primary"
            @click.prevent="goToTab('pills-order-tab')"
          >
            Xem tất cả
          </button>
        </div>
        <!-- TABLE COMPONENT ORDER -->
        <Table :initialValues="intialOrder"></Table>
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
import { computed, onMounted } from 'vue'

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
const goToTab = (tabId) => {
  const tabEl = document.getElementById(tabId)
  if (tabEl) new bootstrap.Tab(tabEl).show()
}
</script>

<template>
  <div class="d-flex justify-content-between">
    <div class="w-75 d-flex justify-content-between gap-3">
      <div class="w-40">
        <div class="input-group mb-3">
          <span class="input-group-text bg-white border-end-0" id="basic-addon1"
            ><i class="fa-solid fa-magnifying-glass"></i
          ></span>
          <input
            type="text"
            class="form-control border-start-0 fst-italic py-2"
            placeholder="Tìm kiếm sản phẩm..."
          />
        </div>
      </div>
      <div class="w-20">
        <select class="form-select" aria-label="Default select example">
          <option selected>Open this select menu</option>
          <option value="1">One</option>
          <option value="2">Two</option>
          <option value="3">Three</option>
        </select>
      </div>
      <div class="w-20">
        <select class="form-select" aria-label="Default select example">
          <option selected>Open this select menu</option>
          <option value="1">One</option>
          <option value="2">Two</option>
          <option value="3">Three</option>
        </select>
      </div>
      <div class="w-20">
        <select class="form-select" aria-label="Default select example">
          <option selected>Open this select menu</option>
          <option value="1">One</option>
          <option value="2">Two</option>
          <option value="3">Three</option>
        </select>
      </div>
    </div>
    <div class="w-25 text-end">
      <button type="button" class="btn btn-primary w-50">Them san pham</button>
    </div>
  </div>
  <div class="mt-5">
    <div class="d-flex justify-content-between gap-3">
      <div class="border border-opacity-50 rounded-4 border-secondary w-100">
        <div class="m-4">
          <div class="d-flex gap-4">
            <div class="w-25 d-flex justify-content-center align-items-center">
              <div class="d-inline-block rounded-circle bg-secondary p-4 h-75 w-75"></div>
            </div>
            <div class="w-75 d-flex flex-column gap-2 justify-content-center">
              <div class="fs-5 fw-bold text-secondary">Tong san pham</div>
              <div class="fs-2 fw-bold">1,250</div>
              <div class="fs-5 fw-bold text-secondary">San pham trong kho</div>
            </div>
          </div>
        </div>
      </div>
      <div class="border border-opacity-50 rounded-4 border-warning w-100">
        <div class="m-4">
          <div class="d-flex gap-4">
            <div class="w-25 d-flex justify-content-center align-items-center">
              <div class="d-inline-block rounded-circle bg-warning p-4 h-75 w-75"></div>
            </div>
            <div class="w-75 d-flex flex-column gap-2 justify-content-center">
              <div class="fs-5 fw-bold text-secondary">Sap het hang</div>
              <div class="fs-2 fw-bold">43</div>
              <div class="fs-5 fw-bold text-secondary">San pham</div>
            </div>
          </div>
        </div>
      </div>
      <div class="border border-opacity-50 rounded-4 border-danger w-100">
        <div class="m-4">
          <div class="d-flex gap-4">
            <div class="w-25 d-flex justify-content-center align-items-center">
              <div class="d-inline-block rounded-circle bg-danger p-4 h-75 w-75"></div>
            </div>
            <div class="w-75 d-flex flex-column gap-2 justify-content-center">
              <div class="fs-5 fw-bold text-secondary">Het hang</div>
              <div class="fs-2 fw-bold">12</div>
              <div class="fs-5 fw-bold text-secondary">San pham</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="mt-5 border rounded-4">
      <div class="m-4">
        <div class="text-start fw-bold fs-5 w-100">Danh sach san pham</div>
        <div class="mt-3">
          <Table :initialValues="initialProductList" :interactive="interactive" :isBorder="true" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Table from '../Table.vue'

const initialProductList = {
  sanPham: 'Sản phẩm',
  danhMuc: 'Danh mục',
  giaBan: 'Giá bán',
  tonKho: 'Tồn kho',
  trangThai: 'Trạng thái',
  ngayCapNhat: 'Ngày cập nhật',
  thaoTac: 'Thao tác',
}

const interactive = [{ del: 'Xóa' }, { upd: 'Cập nhật' }]
</script>

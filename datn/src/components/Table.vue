<template>
  <table :class="isBorder ? 'table border' : 'table table-borderless' + ''">
    <thead>
      <tr>
        <th
          class="fw-bold text-center text-secondary py-3 fs-5 align-top"
          v-for="(f, key) in fields"
          :key="key"
        >
          {{ f.name }}
        </th>
      </tr>
    </thead>
    <tbody>
      <tr class="my-3 align-end" v-for="d in data" :key="d.id">
        <td class="fs-14 text-center fw-bolder py-4">{{ d.name }}</td>
        <td class="fs-14 text-center fw-bolder py-4">{{ d.category }}</td>
        <td class="fs-14 text-center fw-bolder py-4">{{ d.brand }}</td>
        <td class="fs-14 text-center fw-bolder py-4">{{ ulti.formatVND(d.basePrice) }}</td>
        <td class="fs-14 text-center fw-bolder py-4">
          <span
            class="bg-opacity-25 rounded-3 px-3 py-2 d-inline-block"
            :class="
              d.stock <= 0
                ? 'bg-danger text-danger '
                : d.stock <= 5
                  ? 'bg-warning text-warning '
                  : 'bg-success text-success '
            "
          >
            {{ d.stock <= 0 ? 'Hết hàng' : d.stock <= 5 ? 'Sắp hết hàng' : 'Còn hàng' }}
          </span>
        </td>
        <!-- <td class="fs-14 text-center fw-bolder py-4">
          {{ ulti.formatDate(new Date(Date.now())) }}
        </td> -->
        <td v-if="interactive">
          <div class="d-flex justify-content-center align-items-center gap-2">
            <button class="btn btn-danger fs-14">Xoa</button>
            <button class="btn btn-danger fs-14">Xoa</button>
          </div>
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import ulti from '@/ulti/ulti'
import { onMounted, reactive, ref, watch } from 'vue'

const props = defineProps({
  initialValues: Object,
  interactive: Array,
  isBorder: Boolean,
  datalist: Array,
})

const fields = reactive({})

watch(
  () => props.initialValues,
  (val) => {
    if (!val) return
    Object.entries(val).forEach(([key, value]) => {
      fields[key] = { name: value }
    })
  },
  { immediate: true },
)

const data = reactive([])

watch(
  () => props.datalist,
  (val) => {
    if (!val) return
    val.forEach((value, index) => {
      data[index] = {
        id: value.id,
        name: value.name,
        category: value.category,
        brand: value.brand,
        price: value.basePrice,
        stock: () => value.productVariant.map((v) => v.stock),
      }
    })
  },
  { immediate: true },
)
console.log(props.datalist)
</script>

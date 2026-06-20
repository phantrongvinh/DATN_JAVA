<template>
  <tr class="my-3 align-middle" v-for="d in props.data" :key="d.id">
    <td class="fs-14 text-center fw-bolder py-4">{{ d.name }}</td>
    <td class="fs-14 text-center fw-bolder py-4">{{ d.category }}</td>
    <td class="fs-14 text-center fw-bolder py-4">{{ d.brand }}</td>
    <td class="fs-14 text-center fw-bolder py-4">{{ ulti.formatVND(d.price) }}</td>
    <td class="fs-14 text-center fw-bolder py-4">
      <span
        class="bg-opacity-25 rounded-3 px-3 py-2 d-inline-block text-white"
        :class="d.stock <= 0 ? 'bg-danger  ' : d.stock <= 15 ? 'bg-warning  ' : 'bg-success  '"
      >
        {{ d.stock <= 0 ? 'Hết hàng' : d.stock <= 15 ? 'Sắp hết' : 'Còn hàng' }}
      </span>
    </td>
  </tr>
</template>
<script setup>
import ulti from '@/ulti/ulti'

const props = defineProps({
  data: Array,
})
</script>

<template>
  <tr class="my-3 align-middle" v-for="d in props.data" :key="d.id">
    <td class="fs-14 text-center fw-bolder py-4">{{ d.name }}</td>
    <td class="fs-14 text-center fw-bolder py-4">{{ d.category }}</td>
    <td class="fs-14 text-center fw-bolder py-4">{{ d.brand }}</td>
    <td class="fs-14 text-center fw-bolder py-4">{{ ulti.formatVND(d.price) }}</td>
    <td class="fs-14 text-center fw-bolder py-4">{{ d.variantCount }}</td>

    <td class="fs-14 text-center fw-bolder py-4">
      <span
        class="bg-opacity-25 rounded-3 px-3 py-2 d-inline-block text-white"
        :class="d.status !== false ? 'bg-danger  ' : 'bg-success  '"
      >
        {{ d.status !== false ? 'Deactive' : 'Active' }}
      </span>
    </td>
    <td class="fs-14 text-center fw-bolder py-4">{{ ulti.formatDate(d.updatedAt) }}</td>

    <td v-if="interactive">
      <div class="d-flex justify-content-center align-items-center gap-1">
        <button
          class="btn btn-danger fs-14 w-50"
          @click.prevent="props.handleActiveProduct(d.id, d.name, d.status)"
        >
          {{ d.status ? 'Khôi phục' : 'Vô hiệu' }}
        </button>
        <button class="btn btn-warning fs-14 w-50" @click.prevent="props.handleUpdateProduct(d)">
          Cập nhật
        </button>
      </div>
    </td>
  </tr>
</template>
<script setup>
import ulti from '@/ulti/ulti'

const props = defineProps({
  data: Array,
  interactive: Boolean,
  handleActiveProduct: Function,
  handleUpdateProduct: Function,
})
</script>

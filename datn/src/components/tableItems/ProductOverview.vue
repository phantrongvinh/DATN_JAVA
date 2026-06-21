<template>
  <tr v-for="d in props.data" :key="d.id">
    <td>
      <div class="d-flex px-2 py-1">
        <div>
          <img
            src="../assets/img/team-2.jpg"
            class="avatar avatar-sm me-3 border-radius-lg"
            alt="user1"
          />
        </div>
        <div class="d-flex flex-column justify-content-center">
          <p class="text-xs font-weight-bold mb-0">{{ d.name }}</p>
        </div>
      </div>
    </td>
    <td>
      <p class="text-xs font-weight-bold text-secondary mb-0">{{ d.category }}</p>
    </td>
    <td>
      <p class="text-xs font-weight-bold text-secondary mb-0">{{ d.brand }}</p>
    </td>
    <td>
      <p class="text-xs font-weight-bold text-secondary mb-0">{{ ulti.formatVND(d.price) }}</p>
    </td>
    <td class="align-middle text-center text-sm">
      <span
        class="badge badge-sm"
        :class="
          d.stock <= 0
            ? 'bg-gradient-danger  '
            : d.stock <= 15
              ? 'bg-gradient-warning  '
              : 'bg-gradient-success  '
        "
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

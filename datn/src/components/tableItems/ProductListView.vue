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
      <p class="text-xs font-weight-bold text-secondary mb-0">{{ d.variantCount }}</p>
    </td>

    <td class="align-middle text-center text-md">
      <span
        class="badge badge-sm"
        :class="d.status !== false ? 'bg-gradient-danger  ' : 'bg-gradient-success  '"
      >
        {{ d.status !== false ? 'Deactive' : 'Active' }}
      </span>
    </td>

    <td>
      <p class="text-xs font-weight-bold text-secondary mb-0">{{ ulti.formatDate(d.updatedAt) }}</p>
    </td>

    <td class="align-middle">
      <div class="d-flex justify-content-start align-items-center gap-2">
        <a
          class="text-secondary font-weight-bold text-xs cursor-pointer text-danger"
          data-toggle="tooltip"
          data-original-title="Edit user"
          @click.prevent="props.handleActiveProduct(d.id, d.name, d.status)"
        >
          {{ d.status ? 'Khôi phục' : 'Vô hiệu' }}
        </a>
        <a
          class="text-secondary font-weight-bold text-xs cursor-pointer text-warning"
          data-toggle="tooltip"
          data-original-title="Edit user"
          @click.prevent="props.handleUpdateProduct(d)"
        >
          Cập nhật
        </a>
      </div>
    </td>
    <td class="align-middle">
      <div class="form-check">
        <input
          type="checkbox"
          :id="d.id"
          class="form-check-input"
          :checked="props.selectedIds.includes(d.id)"
          @change="emit('select-product', d.id)"
        />
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
  selectedIds: Array,
})

defineOptions({
  inheritAttrs: false,
})
const emit = defineEmits(['select-product'])
</script>

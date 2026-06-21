<template>
  <div class="row align-items-center">
    <div class="col-md-2">
      <img :src="url + '/' + props.i.image" class="img-fluid border-radius-lg" />
    </div>

    <div class="col-md-5">
      <h6 class="mb-1">{{ props.i.name }}</h6>

      <div class="text-sm text-secondary">{{ props.i.color }} · Size {{ props.i.size }}</div>
    </div>
    <div class="col-md-5 d-flex justify-content-between align-items-end flex-column gap-1">
      <div class="">
        <a class="btn btn-link text-dark text-gradient mx-1 mb-0" href="javascript:;">
          <i class="material-symbols-rounded">remove</i></a
        >
        <span class="">{{ props.i.quantity }}</span>
        <a class="btn btn-link text-dark text-gradient mx-1 mb-0" href="javascript:;"
          ><i class="material-symbols-rounded">add</i></a
        >
      </div>
    </div>
    <div class="row w-100">
      <div class="col-md-2"></div>
      <div class="col-md-5">
        <a class="btn btn-link text-danger text-gradient px-0 mb-0" href="javascript:;"
          ><i class="material-symbols-rounded text-sm me-2">delete</i>Delete</a
        >
      </div>
      <div class="col-md-5 text-end">
        <h6 class="text-dark mb-0">{{ ulti.formatVND(props.i.price) }}</h6>
      </div>
    </div>
  </div>
</template>

<script setup>
import ulti from '@/ulti/ulti'

const props = defineProps({
  i: Array,
})

// url
const url = 'http://localhost:8080/uploads/images'
</script>

<template>
  <apexchart type="donut" height="350" :series="series" :options="chartOptions" />
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: Array,
})

const series = computed(() => props.data.map((i) => Number(i.productCount)))

const chartOptions = computed(() => ({
  labels: props.data.map((i) => i.categoryName),

  colors: [
    '#22C55E', // xanh lá
    '#3B82F6', // xanh dương
    '#F59E0B', // vàng
    '#EF4444', // đỏ
    '#8B5CF6', // tím
    '#06B6D4', // cyan
  ],

  legend: {
    position: 'bottom',
  },

  tooltip: {
    y: {
      formatter: (v) => `${v} sản phẩm`,
    },
  },
}))
</script>

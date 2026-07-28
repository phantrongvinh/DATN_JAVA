<template>
  <apexchart type="bar" height="350" :series="series" :options="chartOptions" />
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: Array,
})

const series = computed(() => [
  {
    name: 'Đã giao',
    data: props.data.map((i) => i.delivered),
  },
  {
    name: 'Đang giao',
    data: props.data.map((i) => i.shipping),
  },
  {
    name: 'Đã hủy',
    data: props.data.map((i) => i.cancelled),
  },
])

const chartOptions = computed(() => ({
  chart: {
    stacked: true,
    toolbar: {
      show: false,
    },
  },
  colors: [
    '#22C55E', // xanh lá
    '#3B82F6', // xanh dương
    '#EF4444', // đỏ
  ],

  plotOptions: {
    bar: {
      borderRadius: 4,
    },
  },

  xaxis: {
    categories: props.data.map((i) => `T${i.month}`),
  },

  dataLabels: {
    enabled: false,
  },

  legend: {
    position: 'top',
  },
}))
</script>

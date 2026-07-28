<template>
  <apexchart height="350" type="area" :options="chartOptions" :series="series" />
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: {
    type: Array,
    default: () => [],
  },
})

const series = computed(() => [
  {
    name: 'Doanh thu',
    data: props.data.map((i) => Number(i.revenue)),
  },
])

const chartOptions = computed(() => ({
  chart: {
    toolbar: {
      show: false,
    },
    zoom: {
      enabled: false,
    },
  },

  stroke: {
    curve: 'smooth',
    width: 3,
  },

  dataLabels: {
    enabled: false,
  },

  xaxis: {
    categories: props.data.map((i) => `T${i.month}`),
  },

  yaxis: {
    labels: {
      formatter: (v) =>
        new Intl.NumberFormat('vi-VN', {
          notation: 'compact',
        }).format(v),
    },
  },

  tooltip: {
    y: {
      formatter: (v) =>
        new Intl.NumberFormat('vi-VN', {
          style: 'currency',
          currency: 'VND',
        }).format(v),
    },
  },

  fill: {
    type: 'gradient',
  },
}))
</script>

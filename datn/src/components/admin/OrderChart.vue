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
  {
    name: 'Đang hoàn đơn',
    data: props.data.map((i) => i.returnRequested),
  },
  {
    name: 'Đã hoàn đơn',
    data: props.data.map((i) => i.returned),
  },
  {
    name: 'Đã lấy hàng giao',
    data: props.data.map((i) => i.picked),
  },
])

const chartOptions = computed(() => ({
  chart: {
    stacked: true,
    toolbar: {
      show: false,
    },
  },
  colors: ['#22C55E', '#3B82F6', '#EF4444', '#4B0082', '#FFFF00', '#FFA500'],

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

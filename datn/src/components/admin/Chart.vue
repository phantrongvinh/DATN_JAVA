<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'

import { CanvasRenderer } from 'echarts/renderers'

import { LineChart, PieChart } from 'echarts/charts'

import { GridComponent, TooltipComponent, LegendComponent } from 'echarts/components'

import { TrendingUp } from 'lucide-vue-next'

use([CanvasRenderer, LineChart, PieChart, GridComponent, TooltipComponent, LegendComponent])

const REVENUE_TREND = [
  { m: 'T1', revenue: 124 },
  { m: 'T2', revenue: 142 },
  { m: 'T3', revenue: 138 },
  { m: 'T4', revenue: 168 },
  { m: 'T5', revenue: 196 },
  { m: 'T6', revenue: 215 },
  { m: 'T7', revenue: 198 },
  { m: 'T8', revenue: 246 },
  { m: 'T9', revenue: 268 },
  { m: 'T10', revenue: 254 },
  { m: 'T11', revenue: 312 },
  { m: 'T12', revenue: 348 },
]

const CATEGORY_SHARE = [
  { name: 'Giày', value: 42 },
  { name: 'Áo đấu', value: 28 },
  { name: 'Phụ kiện', value: 18 },
  { name: 'Bóng', value: 12 },
]

const PIE_COLORS = ['#c8993f', '#1a1a1a', '#7a7a7a', '#d4c5a3']

const revenueOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
  },
  grid: {
    left: 20,
    right: 20,
    bottom: 20,
    top: 20,
    containLabel: true,
  },
  xAxis: {
    type: 'category',
    data: REVENUE_TREND.map((i) => i.m),
    axisLine: { show: false },
    axisTick: { show: false },
  },
  yAxis: {
    type: 'value',
    axisLine: { show: false },
    splitLine: {
      lineStyle: {
        type: 'dashed',
      },
    },
  },
  series: [
    {
      data: REVENUE_TREND.map((i) => i.revenue),
      type: 'line',
      smooth: true,
      areaStyle: {
        color: 'rgba(200,153,63,.3)',
      },
      lineStyle: {
        color: '#c8993f',
        width: 3,
      },
      itemStyle: {
        color: '#c8993f',
      },
    },
  ],
}))

const categoryOption = computed(() => ({
  tooltip: {
    trigger: 'item',
  },
  legend: {
    bottom: 0,
  },
  series: [
    {
      type: 'pie',
      radius: ['45%', '70%'],
      data: CATEGORY_SHARE,
      color: PIE_COLORS,
      label: {
        formatter: '{b}\n{d}%',
      },
    },
  ],
}))
</script>
<template>
  <div class="mt-6 grid gap-6 lg:grid-cols-3">
    <!-- Area Chart -->
    <div class="border border-border bg-background p-6 lg:col-span-2">
      <div class="flex items-center justify-between">
        <div>
          <h3 class="font-display text-xl">Tăng trưởng doanh thu</h3>
          <p class="text-xs text-muted-foreground">12 tháng gần nhất (triệu ₫)</p>
        </div>

        <TrendingUp class="h-4 w-4 text-gold" :stroke-width="1.5" />
      </div>

      <div class="mt-5 h-72">
        <v-chart :option="revenueOption" autoresize class="h-full w-full" />
      </div>
    </div>

    <!-- Pie Chart -->
    <div class="border border-border bg-background p-6">
      <h3 class="font-display text-xl">Cơ cấu danh mục</h3>

      <p class="text-xs text-muted-foreground">% doanh thu theo nhóm hàng</p>

      <div class="mt-2 h-64">
        <v-chart :option="categoryOption" autoresize class="h-full w-full" />
      </div>
    </div>
  </div>
</template>

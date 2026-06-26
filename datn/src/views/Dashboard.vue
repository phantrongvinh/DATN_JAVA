<script setup>
import BoxWatch from '@/components/admin/BoxWatch.vue'
import Chart from '@/components/admin/Chart.vue'
import { Package, ShoppingCart, Users, DollarSign } from 'lucide-vue-next'

const stats = [
  {
    label: 'Doanh thu',
    value: '125.000.000₫',
    icon: DollarSign,
    trend: 12.4,
    positive: true,
  },
  {
    label: 'Đơn hàng',
    value: '256',
    icon: ShoppingCart,
    trend: 8.2,
    positive: true,
  },
  {
    label: 'Sản phẩm',
    value: '124',
    icon: Package,
    trend: 2.1,
    positive: true,
  },
  {
    label: 'Khách hàng',
    value: '58',
    icon: Users,
    trend: -1.6,
    positive: false,
  },
]
</script>
<template>
  <!-- Content -->
  <div class="flex-1">
    <header class="border-b border-border bg-background px-6 py-4">
      <h1 class="font-display text-2xl">Dashboard</h1>
    </header>

    <main class="p-6">
      <BoxWatch :data="stats"></BoxWatch>

      <Chart></Chart>
    </main>
  </div>
</template>

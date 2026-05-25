<template>Detail</template>

<script setup>
import { useRoute } from 'vue-router'

const route = useRoute()

console.log(route.query.productId)

console.log(route.query.productVariantId)
</script>

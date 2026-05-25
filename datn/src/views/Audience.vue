<template>{{ gender }}{{ brand }}</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const gender = computed(() => route.params.audience)
const brand = computed(() => route.query.brand)
</script>

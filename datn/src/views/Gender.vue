<template>
  {{ gender }}
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const gender = computed(() => route.params.gender)
</script>

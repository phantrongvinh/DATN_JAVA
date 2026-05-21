<template></template>
<script setup>
const param = new URLSearchParams(window.location.search)

const token = param.get('token')

if (token) {
  localStorage.setItem('token', token)
  window.location.href = '/'
}
</script>

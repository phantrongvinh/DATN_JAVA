<template>
  <form action="">
    <div class="mb-3" v-for="(field, key) in fields" :key="key">
      <div class="form-label">{{ ulti.formatLabel(field.name) }}</div>

      <input
        v-if="field.name !== 'birthDay'"
        :type="passwordFields.includes(field.name) ? 'password' : 'text'"
        class="form-control rounded-3"
        :id="field.name"
        @focus="clearMessages"
        v-model="field.value"
      />
      <flat-pickr
        v-else
        class="form-control rounded-3"
        :id="field.name"
        :config="config"
        @focus="clearMessages"
        :model-type="'Date'"
        v-model="field.value"
      />
      <span v-if="field.meta.touched" class="text-danger">
        {{ field.error }}
      </span>
    </div>
    <div class="mb-3">
      <div class="text-danger">
        {{ localErrorMessage }}
      </div>
      <div class="text-success">
        {{ localSuccessMessage }}
      </div>
    </div>
    <div class="mb-3" v-if="props.interactive">
      <div class="d-flex justify-content-between">
        <div class="form-check">
          <input type="checkbox" name="rememberMe" id="rememberMe" class="form-check-input" />
          <label class="form-check-label text-secondary fs-6">Remember me</label>
        </div>

        <div class="text-secondary">
          <RouterLink to="/" class="fs-6 hover-underline">Forgot password?</RouterLink>
        </div>
      </div>
    </div>
    <div class="mb-3">
      <button
        class="btn btn-outline-primary w-100 rounded-3"
        @click.prevent="onSubmit"
        :disabled="props.loading"
      >
        {{ !props.loading ? props.btn : props.btn.trim().split(/\s+/).slice(0, 1) + 'ing' }}
      </button>
    </div>
  </form>
</template>

<script setup>
import ulti from '@/ulti/ulti'
import flatPickr from 'vue-flatpickr-component'

import 'flatpickr/dist/flatpickr.css'
import { useField, useForm } from 'vee-validate'
import { reactive, ref, watch } from 'vue'
import { Vietnamese } from 'flatpickr/dist/l10n/vn.js'

// config date input
const config = {
  locale: Vietnamese,
  altInput: true,
  altFormat: 'd/m/Y',

  dateFormat: 'Y-m-d',
}

// Lấy props từ cha
const props = defineProps({
  initialValues: Object,
  interactive: Boolean,
  validate: Object,
  btn: String,
  handleSubmitForm: Function,
  errorMessage: String,
  successMessage: String,
  loading: Boolean,
})
const localErrorMessage = ref(props.errorMessage)
const localSuccessMessage = ref(props.successMessage)

watch(
  () => props.errorMessage,
  (val) => {
    localErrorMessage.value = val
  },
)

watch(
  () => props.successMessage,
  (val) => {
    localSuccessMessage.value = val
  },
)

// Quản lý form và validate bằng thư viện vee-validate yup

const { handleSubmit } = useForm({
  validationSchema: props.validate,
  initialValues: props.initialValues,
})

// Hanle tạo field
const fields = reactive({})

Object.keys(props.initialValues).forEach((key) => {
  const field = useField(key)

  fields[key] = {
    name: key,
    value: field.value,
    error: field.errorMessage,
    meta: field.meta,
  }
})

// Hàmm dùng để handle event có các thuộc tính trong fields
const passwordFields = ['password', 'confirmPassword']

const onSubmit = handleSubmit(async (values) => {
  props.handleSubmitForm(values)
})

// handle clear message
const clearMessages = () => {
  localErrorMessage.value = ''
  localSuccessMessage.value = ''
}
</script>

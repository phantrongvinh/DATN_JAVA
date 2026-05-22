<template>
  <form action="">
    <div class="mb-3" v-for="(field, key) in fields" :key="key">
      <div class="form-label">{{ formatLabel(field.name) }}</div>
      <input
        :type="passwordFields.includes(field.name) ? 'password' : 'text'"
        class="form-control rounded-3"
        :id="field.name"
        @focus="localErrorMessage = ''"
        v-model="field.value"
      />
      <span v-if="field.meta.touched" class="text-danger">
        {{ field.error }}
      </span>
    </div>
    <div class="mb-3 text-danger">
      {{ localErrorMessage }}
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
      <button class="btn btn-primary w-100 rounded-3" @click.prevent="onSubmit">
        {{ props.btn }}
      </button>
    </div>
  </form>
</template>

<script setup>
import { useField, useForm } from 'vee-validate'
import { reactive, ref, watch } from 'vue'

// Lấy props từ cha
const props = defineProps({
  initialValues: Object,
  interactive: Boolean,
  validate: Object,
  btn: String,
  handleSubmitForm: Function,
  errorMessage: String,
})
const localErrorMessage = ref(props.errorMessage)

watch(
  () => props.errorMessage,
  (val) => {
    localErrorMessage.value = val
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

const formatLabel = (str) => {
  return str.replace(/([A-Z])/g, ' $1').replace(/^./, (c) => c.toUpperCase())
}

// Hàmm dùng để handle event có các thuộc tính trong fields
const passwordFields = ['password', 'confirmPassword']

const onSubmit = handleSubmit(async (values) => {
  props.handleSubmitForm(values)
})
</script>

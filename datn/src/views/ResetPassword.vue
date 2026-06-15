<template>
  <div
    ref="screenRef"
    class="d-flex justify-content-center align-items-center"
    :style="{ minHeight: screenHeight - 50 + 'px' }"
  >
    <div class="w-50 rounded-4 shadow">
      <div class="row m-4">
        <div class="col-lg-6 d-flex justify-content-center align-items-center">
          <i class="fa-solid fa-fire-flame-curved fs-1"></i>
        </div>
        <div class="col-lg-6 mb-4">
          <div class="m-4">
            <div class="text-center fs-2 fw-bold my-5">Reset Password</div>
            <Form
              :initialValues="initialValues"
              :validate="validate"
              btn="Change password"
              :handleSubmitForm="handleResetPass"
              :errorMessage="error"
              :successMessage="message"
              :loading="loadding"
              :countDown="countDown"
              :isResetForm="true"
            ></Form>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Form from '@/components/Form.vue'
import { useAuthStore } from '@/stores/useAuthStore'
import { storeToRefs } from 'pinia'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as yup from 'yup'

const router = useRouter()

const screenHeight = ref(window.innerHeight)

const screenRef = ref(null)

onMounted(() => {
  window.addEventListener('resize', () => {
    screenHeight.value = window.innerHeight
  })
})

const route = useRoute()

// Giá trị của các trường thông tin
const initialValues = { password: '', confirmPassword: '' }

// AuthStore
const authStore = useAuthStore()
onMounted(() => authStore.clearMessages())

const { loadding, message, error } = storeToRefs(authStore)

// handle confirm vs validate
const handleResetPass = async (values) => {
  try {
    const token = route.query.token
    await authStore.resetPassword(token, values.password)
  } catch (error) {}
}

const validate = yup.object({
  password: yup.string().required('Password required'),
  confirmPassword: yup
    .string()
    .required('Confirm pass required')
    .oneOf([yup.ref('password')], 'Passwords must match'),
})
</script>

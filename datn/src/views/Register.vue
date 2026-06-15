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
            <div class="text-center fs-2 fw-bold my-5">Create your account</div>
            <Form
              :initialValues="initialValues"
              btn="Sign up"
              :loading="loadding"
              :errorMessage="error"
              :validate="validate"
              :handleSubmitForm="handleRegister"
              :successMessage="message"
            ></Form>
            <div class="mt-5">
              <div class="text-dark fs-6 text-center">
                Already have an account?
                <RouterLink to="/login" class="fw-semibold hover-underline text-primary"
                  >Log in</RouterLink
                >
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import authAPI from '@/api/authAPI'
import Form from '@/components/Form.vue'
import { useAuthStore } from '@/stores/useAuthStore'
import { storeToRefs } from 'pinia'
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as yup from 'yup'

const router = useRouter()

const screenHeight = ref(window.innerHeight)

const screenRef = ref(null)

// Khai báo các props truyền xuống Form component
const initialValues = {
  fullName: '',
  email: '',
  password: '',
  confirmPassword: '',
  birthDay: '',
  phone: '',
}

// Validate bằng yup cho register
const validate = yup.object({
  fullName: yup.string().required('Name required'),
  email: yup.string().required('Email required').email('Invalid email'),
  password: yup.string().required('Password required'),
  confirmPassword: yup
    .string()
    .required('Confirm pass required')
    .oneOf([yup.ref('password')], 'Passwords must match'),
  birthDay: yup
    .date()
    .transform((value, originalValue) => {
      if (!originalValue) return null

      return new Date(originalValue)
    })
    .max(new Date(), 'Birth day cannot be in the future')
    .required('Birth day required'),
  phone: yup
    .string()
    .required('Phone required')
    .matches(/^(03|05|07|08|09)+([0-9]{8})$/, 'Invalid phone number'),
})

// handle register

const authStore = useAuthStore()
onMounted(() => authStore.clearMessages())

const { loadding, error, message } = storeToRefs(authStore)

const handleRegister = async (values) => {
  try {
    await authStore.register(values)
  } catch (error) {}
}
</script>

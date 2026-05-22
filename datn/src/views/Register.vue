<template>
  <div
    ref="screenRef"
    class="d-flex justify-content-center align-items-center"
    :style="{ minHeight: screenHeight - 50 + 'px' }"
  >
    <div class="w-50 rounded-4 shadow">
      <div class="row m-4">
        <div class="col-lg-6"></div>
        <div class="col-lg-6 mb-4">
          <div class="m-4">
            <div class="text-center fs-2 fw-bold my-5">Welcome Back!</div>
            <Form
              :initialValues="initialValues"
              btn="Sign up"
              :errorMessage="errorMessage"
              :validate="validate"
              :handleSubmitForm="handleRegister"
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import * as yup from 'yup'

const router = useRouter()

const screenHeight = ref(window.innerHeight)

const screenRef = ref(null)

// Khai báo các props truyền xuống Form component
const initialValues = { fullName: '', email: '', password: '', confirmPassword: '', phone: '' }
const errorMessage = ref('')

// Validate bằng yup cho register
const validate = yup.object({
  fullName: yup.string().required('Name required'),
  email: yup.string().required('Email required').email('Invalid email'),
  password: yup.string().required('Password required'),
  confirmPassword: yup
    .string()
    .required('Confirm pass required')
    .oneOf([yup.ref('password')], 'Passwords must match'),
  phone: yup
    .string()
    .required('Phone required')
    .matches(/^(03|05|07|08|09)+([0-9]{8})$/, 'Invalid phone number'),
})

const handleRegister = async (values) => {
  try {
    const res = await authAPI.register(values)

    router.push('/login')
  } catch (error) {
    errorMessage.value = error?.response?.data?.message
  }
}
</script>

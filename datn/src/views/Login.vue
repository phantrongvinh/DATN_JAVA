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
              :interactive="true"
              :validate="validate"
              btn="Login"
              :handleSubmitForm="handleLogin"
              :errorMessage="errorMessage"
            ></Form>
            <div class="mb-3">
              <button
                class="btn btn-outline-dark w-100 rounded-3"
                @click.prevent="handleGoogleLogin"
              >
                <img src="/images/google.svg" alt="google" width="20" height="20" />
                Log in with Google
              </button>
            </div>
            <div class="mt-5">
              <div class="text-dark fs-6 text-center">
                Don't have an account?
                <RouterLink to="/register" class="fw-semibold hover-underline text-primary"
                  >Sign up</RouterLink
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
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as yup from 'yup'

const router = useRouter()

const screenHeight = ref(window.innerHeight)

const screenRef = ref(null)

onMounted(() => {
  window.addEventListener('resize', () => {
    screenHeight.value = window.innerHeight
  })
})

// Hàm dùng để handle event login bằng google
const handleGoogleLogin = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google'
}

// Khai báo props để truyền xuống Form component hao
const initialValues = { email: '', password: '' }
const errorMessage = ref('')

// Validate bằng yup cho login
const validate = yup.object({
  email: yup.string().required('Email required').email('Invalid email'),
  password: yup.string().required('Password required'),
})

// Handle event login

const handleLogin = async (values) => {
  try {
    const res = await authAPI.login(values)

    localStorage.setItem('token', res.token)
    router.push('/')
  } catch (error) {
    errorMessage.value = error?.response?.data?.message
  }
}
</script>

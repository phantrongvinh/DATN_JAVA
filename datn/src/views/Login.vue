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
            <div class="text-center fs-2 fw-bold my-5">Welcome Back!</div>
            <Form
              :initialValues="initialValues"
              :interactive="true"
              :validate="validate"
              btn="Login"
              :handleSubmitForm="handleLogin"
              :errorMessage="error"
              :successMessage="message"
            ></Form>
            <div class="mb-3" v-if="resend">
              <button
                class="btn btn-outline-success w-100 rounded-3"
                @click.prevent="handleResend"
                :disabled="loadding"
              >
                {{ loadding ? 'Resending' : 'Resend active' }}
              </button>
            </div>
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
import { useAuthStore } from '@/stores/useAuthStore'
import { storeToRefs } from 'pinia'
import { computed, onMounted, ref, watch } from 'vue'
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

// Validate bằng yup cho login
const validate = yup.object({
  email: yup.string().required('Email required').email('Invalid email'),
  password: yup.string().required('Password required'),
})

// Handle event login
const mailSend = ref('')
const authStore = useAuthStore()
const { loadding, error, resend, message } = storeToRefs(authStore)

const handleLogin = async (values) => {
  try {
    message.value = ''
    mailSend.value = values.email
    await authStore.login(values)
    router.push('/')
  } catch (err) {}
}

// Handle resend email
const handleResend = async () => {
  console.log(mailSend.value)

  await authStore.resendMail({ email: mailSend.value })
  mailSend.value = ''
  error.value = ''
}
</script>

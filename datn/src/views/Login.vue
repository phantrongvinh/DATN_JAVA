<template>
  <div
    ref="screenRef"
    class="d-flex justify-content-center align-items-center"
    :style="{ minHeight: screenHeight - 50 + 'px' }"
  >
    <div class="w-50 rounded-4 shadow">
      <div class="row m-4">
        <div class="col-lg-6"></div>
        <div class="col-lg-6 mb-5">
          <div class="m-4">
            <div class="text-center fs-2 fw-bold my-5">Welcome Back!</div>
            <form action="">
              <div class="mb-3">
                <div class="form-label" for="#email">Email</div>
                <input
                  type="text"
                  name="email"
                  id="email"
                  class="form-control rounded-3"
                  placeholder="abc@gmail.com"
                  v-model="email"
                />
              </div>
              <div class="mb-3">
                <div class="form-label" for="#password">Password</div>
                <input
                  type="password"
                  name="password"
                  id="password"
                  class="form-control rounded-3"
                  placeholder="*****************"
                  v-model="password"
                />
              </div>
              <div :class="errorMessage === '' ? 'd-none' : 'mb-3 text-danger'">
                {{ errorMessage }}
              </div>
              <div class="mb-3">
                <div class="d-flex justify-content-between">
                  <div class="form-check">
                    <input
                      type="checkbox"
                      name="rememberMe"
                      id="rememberMe"
                      class="form-check-input"
                    />
                    <label class="form-check-label text-secondary fs-6">Remember me</label>
                  </div>

                  <div class="text-secondary">
                    <RouterLink to="/" class="text-decoration-none text-secondary fs-6"
                      >Forgot password?</RouterLink
                    >
                  </div>
                </div>
              </div>
              <div class="mb-3">
                <button class="btn btn-primary w-100 rounded-3" @click.prevent="handleLogin">
                  Login
                </button>
              </div>
            </form>
            <div class="mb-3">
              <button
                class="btn btn-outline-dark w-100 rounded-3"
                @click.prevent="handleGoogleLogin"
              >
                <img src="/images/google.svg" alt="google" width="20" height="20" />
                Log in with Google
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import authAPI from '@/api/authAPI'
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const screenHeight = ref(window.innerHeight)

const screenRef = ref(null)

// Hàm dùng để handle event login bằng google
const handleGoogleLogin = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google'
}

// Hàmm dùng để handle event login bằng username(email) và password

const email = ref('')
const password = ref('')

const errorMessage = ref('')

// Timeout cho message lỗi
// const showError = (message) => {
//   errorMessage.value = message

//   setTimeout(() => {
//     errorMessage.value = ''
//   }, 3000)
// }

watch([email, password], () => {
  errorMessage.value = ''
})

const handleLogin = async () => {
  try {
    const data = {
      email: email.value,
      password: password.value,
    }
    const res = await authAPI.login(data)

    localStorage.setItem('token', res.token)
    router.push('/')
  } catch (error) {
    errorMessage.value = error?.response?.data?.message
  }
}
</script>

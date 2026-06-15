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
            <div class="text-center fs-2 fw-bold my-5">Forgot password</div>
            <Form
              :initialValues="initialValues"
              :validate="validate"
              btn="Send email"
              :handleSubmitForm="handleSendEmail"
              :errorMessage="error"
              :successMessage="message"
              :loading="loadding"
              :countDown="countDown"
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
import { onBeforeUnmount, onMounted, ref } from 'vue'
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

// Khai báo props để truyền xuống Form component hao
const initialValues = { email: '' }

// Validate bằng yup cho login
const validate = yup.object({
  email: yup.string().required('Email required').email('Invalid email'),
})

// Handle send mail forgot mail
const authStore = useAuthStore()
onMounted(() => authStore.clearMessages())
const { loadding, error, message } = storeToRefs(authStore)

const handleSendEmail = async (values) => {
  try {
    await authStore.forgotPassword(values.email)
    values.email = ''
    handleSend()
  } catch (error) {}
}

const countDown = ref(0)

let timer = null

const handleSend = () => {
  countDown.value = 10

  timer = setInterval(() => {
    countDown.value--

    if (countDown.value <= 0) {
      clearInterval(timer)
      timer = null
    }
  }, 1000)
}

onBeforeUnmount(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

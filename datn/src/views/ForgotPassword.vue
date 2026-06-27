<template>
  <div class="container-x mx-auto max-w-md py-20">
    <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">Khôi phục</p>

    <h1 class="mt-2 font-display text-4xl">Quên mật khẩu</h1>

    <p class="mt-3 text-sm text-muted-foreground">
      Nhập email đăng ký, chúng tôi sẽ gửi liên kết đặt lại mật khẩu đến hộp thư của bạn.
    </p>

    <Form
      v-if="!sendSuccess"
      :validation-schema="validate"
      :initial-values="initialValues"
      class="mt-8 space-y-5"
      @submit="handleSendEmail"
    >
      <div>
        <label class="mb-2 block text-sm font-medium"> Email </label>

        <Field
          name="email"
          type="email"
          placeholder="Nhập email"
          class="w-full border border-border bg-background px-4 py-3 outline-none transition focus:border-foreground"
        />

        <ErrorMessage name="email" class="mt-1 block text-sm text-red-500" />

        <!-- Thông báo từ backend -->
        <div
          v-if="error"
          class="mt-3 rounded border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-600"
        >
          {{ error }}
        </div>
      </div>

      <button
        type="submit"
        :disabled="loadding || countDown > 0"
        class="w-full bg-ink py-3 text-sm font-medium uppercase tracking-widest text-ivory transition hover:bg-ink/85 disabled:cursor-not-allowed disabled:opacity-60"
      >
        <span v-if="loadding"> Đang gửi... </span>

        <span v-else-if="countDown > 0"> Gửi lại sau {{ countDown }}s </span>

        <span v-else> Gửi liên kết đặt lại </span>
      </button>
    </Form>
    <div v-else class="mt-8 space-y-5">
      <div class="flex items-start gap-3 rounded border border-green-200 bg-green-50 p-4 text-sm">
        <Mail class="mt-0.5 h-5 w-5 text-green-600" />

        <div>
          <p class="font-medium text-green-700">
            {{ message }}
          </p>

          <p class="mt-1 text-gray-500">
            Vui lòng kiểm tra hộp thư của bạn để tiếp tục đặt lại mật khẩu.
          </p>
        </div>
      </div>

      <button
        class="w-full bg-ink py-3 text-sm font-medium uppercase tracking-widest text-ivory hover:bg-ink/85"
        @click="router.push('/login')"
      >
        Quay lại đăng nhập
      </button>
    </div>

    <p class="mt-8 text-center text-sm text-muted-foreground">
      Nhớ rồi?

      <RouterLink to="/login" class="border-b border-foreground pb-px text-foreground">
        Quay lại đăng nhập
      </RouterLink>
    </p>
  </div>
</template>

<script setup>
import { Mail } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/useAuthStore'
import { storeToRefs } from 'pinia'
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as yup from 'yup'
import { ErrorMessage, Field, Form } from 'vee-validate'

const router = useRouter()

// Khai báo props để truyền xuống Form component hao
const initialValues = { email: '' }

// Validate bằng yup cho login
const validate = yup.object({
  email: yup.string().required('Email không được để trống').email('Không đúng dịnh dang email'),
})

// Handle send mail forgot mail
const authStore = useAuthStore()
onMounted(() => authStore.clearMessages())
const { loadding, error, message } = storeToRefs(authStore)

const sendSuccess = ref(false)

const handleSendEmail = async (values) => {
  sendSuccess.value = false

  await authStore.forgotPassword(values.email)

  if (message.value === 'Email đã được gửi') {
    sendSuccess.value = true
    handleSend()
  }
}

const countDown = ref(0)

let timer = null

const handleSend = () => {
  countDown.value = 30

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

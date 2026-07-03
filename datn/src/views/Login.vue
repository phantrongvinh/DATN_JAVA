<script setup>
import Field from '@/components/site/Field.vue'
import { useAuthStore } from '@/stores/useAuthStore'
import { storeToRefs } from 'pinia'
import { useForm } from 'vee-validate'
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import * as yup from 'yup'

const router = useRouter()

const authStore = useAuthStore()

const { loadding, error, message, resend } = storeToRefs(authStore)

const schema = yup.object({
  email: yup.string().required('Email không được để trống').email('Email không hợp lệ'),

  password: yup.string().required('Mật khẩu không được để trống'),
})

const { handleSubmit, errors, defineField } = useForm({
  validationSchema: schema,
})

const [email] = defineField('email')
const [password] = defineField('password')
const mailSend = ref('')

const onSubmit = handleSubmit(async (values) => {
  try {
    const data = {
      email: values.email,
      password: values.password,
    }
    mailSend.value = values.email
    await authStore.login(data)
    await router.push('/profile')
  } catch (e) {
    console.log(e)
  }
})

// Hàm dùng để handle event login bằng google
const handleGoogleLogin = () => {
  window.location.href = 'http://localhost:8080/oauth2/authorization/google'
}

// Handle resend email
const handleResend = async () => {
  await authStore.resendMail({ email: mailSend.value })
  mailSend.value = ''
  error.value = ''
}
</script>

<template>
  <div class="container-x grid gap-12 py-16 md:grid-cols-2">
    <div class="hidden aspect-[4/5] overflow-hidden bg-secondary md:block">
      <img
        src="https://images.unsplash.com/photo-1551958219-acbc608c6377?auto=format&fit=crop&w=900&q=80"
        class="h-full w-full object-cover"
        alt=""
      />
    </div>

    <div class="mx-auto w-full max-w-md md:mt-12">
      <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">Chào mừng trở lại</p>

      <h1 class="mt-2 font-display text-4xl">Đăng nhập</h1>

      <p class="mt-3 text-sm text-muted-foreground">
        Truy cập đơn hàng, danh sách yêu thích và ưu đãi thành viên.
      </p>

      <form class="mt-8 space-y-5" @submit.prevent="onSubmit">
        <div>
          <Field label="Email" type="email" v-model="email" @focus="authStore.clearMessages" />

          <p v-if="errors.email" class="mt-1 text-sm text-red-500">
            {{ errors.email }}
          </p>
        </div>

        <div>
          <Field
            label="Mật khẩu"
            type="password"
            v-model="password"
            @focus="authStore.clearMessages"
          />

          <p v-if="errors.password" class="mt-1 text-sm text-red-500">
            {{ errors.password }}
          </p>
        </div>

        <p v-if="error" class="mt-1 text-sm text-red-500">
          {{ error }}
        </p>
        <p v-if="message" class="mt-1 text-sm text-green-500">
          {{ message }}
        </p>

        <div class="flex items-center justify-between text-xs">
          <label class="flex items-center gap-2 text-muted-foreground">
            <input type="checkbox" class="accent-foreground" /> Ghi nhớ tôi
          </label>
          <RouterLink
            to="/forgot-password"
            class="border-b border-foreground/40 pb-px hover:border-foreground"
          >
            Quên mật khẩu?
          </RouterLink>
        </div>

        <div v-if="resend" class="mt-6">
          <button
            @click.prevent="handleResend"
            :disabled="loadding"
            class="cursor-pointer flex w-full items-center justify-center gap-2 rounded-xl border border-emerald-500 px-4 py-3 text-sm font-medium text-emerald-600 transition-all duration-200 hover:bg-emerald-500 hover:text-white disabled:cursor-not-allowed disabled:opacity-60"
          >
            <svg
              v-if="loadding"
              class="h-4 w-4 animate-spin"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 24 24"
            >
              <circle
                class="opacity-25"
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                stroke-width="4"
              />
              <path
                class="opacity-75"
                fill="currentColor"
                d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"
              />
            </svg>

            {{ loadding ? 'Đang gửi mã...' : 'Gửi lại mã kích hoạt' }}
          </button>
        </div>
        <button
          type="submit"
          class="w-full bg-ink py-3 text-ivory cursor-pointer"
          :disabled="loadding"
        >
          {{ loadding ? 'Đăng nhập ... ' : 'Đăng nhập' }}
        </button>
      </form>
      <div class="mt-6">
        <button
          class="flex items-center justify-center gap-3 w-full border border-border bg-white py-3 text-black rounded-md shadow-sm hover:bg-gray-50 transition cursor-pointer"
          @click.prevent="handleGoogleLogin"
        >
          <img src="/images/google.svg" alt="Google" class="w-5 h-5" />

          <span class="font-medium"> Sign in with Google </span>
        </button>
      </div>

      <p class="mt-8 text-center text-sm text-muted-foreground">
        Chưa có tài khoản?

        <RouterLink to="/register" class="border-b border-foreground pb-px text-foreground">
          Đăng ký ngay
        </RouterLink>
      </p>
    </div>
  </div>
</template>

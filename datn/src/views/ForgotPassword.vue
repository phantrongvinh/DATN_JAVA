<template>
  <div class="container-x mx-auto max-w-md py-20">
    <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">Khôi phục</p>

    <h1 class="mt-2 font-display text-4xl">Quên mật khẩu</h1>

    <p class="mt-3 text-sm text-muted-foreground">
      Nhập email đăng ký, chúng tôi sẽ gửi liên kết đặt lại mật khẩu đến hộp thư của bạn.
    </p>

    <!-- Form nhập email -->
    <form v-if="!sentCode" class="mt-8 space-y-5" @submit.prevent="submit">
      <div>
        <label class="mb-2 block text-sm font-medium"> Email </label>

        <input
          v-model="email"
          type="email"
          placeholder="Nhập email"
          class="w-full border border-border bg-background px-4 py-3 outline-none focus:border-foreground"
        />
      </div>

      <button
        type="submit"
        class="w-full bg-ink py-3 text-sm font-medium uppercase tracking-widest text-ivory transition-colors hover:bg-ink/85"
      >
        Gửi liên kết đặt lại
      </button>
    </form>

    <!-- Đã gửi mail -->
    <div v-else class="mt-8 space-y-5">
      <div class="flex items-start gap-3 border border-gold/40 bg-gold-soft/40 p-4 text-sm">
        <Mail class="mt-0.5 h-4 w-4 text-gold" />

        <div>
          <p class="font-medium">Đã gửi liên kết đến {{ email }}</p>

          <p class="mt-1 text-muted-foreground">
            (Demo) Mã của bạn:
            <span class="font-mono text-base font-semibold text-ink">
              {{ sentCode }}
            </span>
          </p>
        </div>
      </div>

      <button
        class="w-full bg-ink py-3 text-sm font-medium uppercase tracking-widest text-ivory hover:bg-ink/85"
        @click="goResetPassword"
      >
        Tiếp tục đặt lại mật khẩu
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

const router = useRouter()

onMounted(() => {
  window.addEventListener('resize', () => {
    screenHeight.value = window.innerHeight
  })
})

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

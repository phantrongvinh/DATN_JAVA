<template>
  <div class="container-x mx-auto max-w-md py-20">
    <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">Bảo mật</p>

    <h1 class="mt-2 font-display text-4xl">Đặt lại mật khẩu</h1>

    <!-- Link không hợp lệ -->
    <div
      v-if="error"
      class="mt-8 flex items-start gap-3 border border-red-200 bg-red-50 p-4 text-sm"
    >
      <AlertCircle class="mt-0.5 h-5 w-5 text-red-600" />

      <div>
        <p class="font-medium text-red-700">
          {{ error }}
        </p>

        <RouterLink
          to="/forgot-password"
          class="mt-2 inline-block border-b border-red-600 text-red-700"
        >
          Yêu cầu liên kết mới
        </RouterLink>
      </div>
    </div>

    <!-- Form -->
    <template v-else-if="!message">
      <div class="mt-6 flex items-start gap-3 border border-emerald-200 bg-emerald-50 p-3 text-xs">
        <CheckCircle2 class="mt-0.5 h-4 w-4 text-emerald-600" />

        <p class="text-emerald-800">Liên kết hợp lệ. Hãy đặt mật khẩu mới.</p>
      </div>

      <Form
        :validation-schema="validate"
        :initial-values="initialValues"
        class="mt-6 space-y-5"
        @submit="handleResetPass"
      >
        <div>
          <label class="mb-2 block text-sm font-medium"> Mật khẩu mới </label>

          <Field
            name="password"
            type="password"
            class="w-full border border-border bg-background px-4 py-3 outline-none transition focus:border-foreground"
          />

          <ErrorMessage name="password" class="mt-1 block text-sm text-red-500" />
        </div>

        <div>
          <label class="mb-2 block text-sm font-medium"> Xác nhận mật khẩu </label>

          <Field
            name="confirmPassword"
            type="password"
            class="w-full border border-border bg-background px-4 py-3 outline-none transition focus:border-foreground"
          />

          <ErrorMessage name="confirmPassword" class="mt-1 block text-sm text-red-500" />
        </div>

        <button
          type="submit"
          :disabled="loadding"
          class="w-full bg-ink py-3 text-sm font-medium uppercase tracking-widest text-ivory transition hover:bg-ink/85 disabled:cursor-not-allowed disabled:opacity-60"
        >
          <span v-if="loadding">Đang cập nhật...</span>
          <span v-else>Cập nhật mật khẩu</span>
        </button>
      </Form>
    </template>

    <!-- Thành công -->
    <div v-else class="mt-8 space-y-5">
      <div class="flex items-start gap-3 border border-emerald-200 bg-emerald-50 p-4 text-sm">
        <CheckCircle2 class="mt-0.5 h-5 w-5 text-emerald-600" />

        <div>
          <p class="font-medium">
            {{ message }}
          </p>

          <p class="mt-1 text-muted-foreground">Bạn có thể đăng nhập bằng mật khẩu mới.</p>
        </div>
      </div>

      <button
        class="w-full bg-ink py-3 text-sm font-medium uppercase tracking-widest text-ivory transition hover:bg-ink/85"
        @click="router.push('/login')"
      >
        Quay lại đăng nhập
      </button>
    </div>

    <p class="mt-8 text-center text-sm text-muted-foreground">
      <RouterLink to="/login" class="border-b border-foreground pb-px text-foreground">
        Quay lại đăng nhập
      </RouterLink>
    </p>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/useAuthStore'
import { storeToRefs } from 'pinia'
import { ErrorMessage, Field, Form } from 'vee-validate'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import * as yup from 'yup'

const router = useRouter()

onMounted(async () => {
  await authStore.validateResetToken(route.query.token)
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

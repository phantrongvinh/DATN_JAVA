<script setup>
import { useRouter } from 'vue-router'
import { useForm } from 'vee-validate'
import * as yup from 'yup'
import Field from '@/components/site/Field.vue'
import { useAuthStore } from '@/stores/useAuthStore'
import { storeToRefs } from 'pinia'
import { ArrowLeft } from 'lucide-vue-next'

const router = useRouter()

const authStore = useAuthStore()

const { loadding, message, error } = storeToRefs(authStore)

const schema = yup.object({
  fullName: yup.string().required('Họ và tên không được để trống'),

  email: yup.string().required('Email không được để trống').email('Email không hợp lệ'),

  password: yup.string().required('Mật khẩu không được để trống'),

  confirm: yup
    .string()
    .required('Vui lòng xác nhận mật khẩu')
    .oneOf([yup.ref('password')], 'Mật khẩu xác nhận không khớp'),
})

const { handleSubmit, defineField, errors, resetForm } = useForm({
  validationSchema: schema,
})

const [fullName] = defineField('fullName')
const [email] = defineField('email')
const [password] = defineField('password')
const [confirm] = defineField('confirm')

const onSubmit = handleSubmit(async (values) => {
  try {
    const data = {
      fullName: values.fullName,
      email: values.email,
      password: values.password,
      confirmPassword: values.confirm,
    }
    await authStore.register(data)

    // toast.success('Tạo tài khoản thành công')

    router.push('/login')
  } catch (error) {
    // toast.error('Đăng ký thất bại')
  }
})
</script>

<template>
  <div class="container-x relative grid gap-12 py-16 md:grid-cols-2">
    <RouterLink
      to="/"
      class="absolute left-4 top-6 inline-flex items-center gap-1.5 text-xs text-muted-foreground transition-colors hover:text-foreground md:left-8"
    >
      <ArrowLeft class="h-3.5 w-3.5" />
      Trở lại trang chủ
    </RouterLink>
    <!-- Image -->
    <div class="hidden aspect-[4/5] overflow-hidden bg-secondary md:block">
      <img
        src="https://plus.unsplash.com/premium_photo-1661868926397-0083f0503c07?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        class="h-full w-full object-cover"
        alt=""
      />
    </div>

    <!-- Form -->
    <div class="mx-auto w-full max-w-md md:mt-12">
      <p class="text-xs uppercase tracking-[0.25em] text-muted-foreground">Bắt đầu hành trình</p>

      <h1 class="mt-2 font-display text-4xl">Đăng ký</h1>

      <p class="mt-3 text-sm text-muted-foreground">
        Tạo tài khoản để nhận quà chào mừng và ưu đãi sinh nhật.
      </p>

      <form class="mt-8 space-y-5">
        <div>
          <Field
            label="Họ và tên"
            type="text"
            v-model="fullName"
            @focus="authStore.clearMessages"
          />
          <p class="mt-1 text-sm text-red-500">
            {{ errors.fullName }}
          </p>
        </div>

        <div>
          <Field label="Email" type="email" v-model="email" @focus="authStore.clearMessages" />
          <p class="mt-1 text-sm text-red-500">
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
          <p class="mt-1 text-sm text-red-500">
            {{ errors.password }}
          </p>
        </div>

        <div>
          <Field
            label="Xác nhận mật khẩu"
            type="password"
            v-model="confirm"
            @focus="authStore.clearMessages"
          />
          <p class="mt-1 text-sm text-red-500">
            {{ errors.confirm }}
          </p>
        </div>

        <p v-if="error" class="mt-1 text-sm text-red-500">
          {{ error }}
        </p>
        <p v-if="message" class="mt-1 text-sm text-green-500">
          {{ message }}
        </p>

        <button
          @click.prevent="onSubmit"
          type="submit"
          class="w-full bg-ink py-3 text-sm font-medium uppercase tracking-widest text-ivory transition-colors hover:bg-ink/85 cursor-pointer"
        >
          Tạo tài khoản
        </button>
      </form>

      <p class="mt-8 text-center text-sm text-muted-foreground">
        Đã có tài khoản?

        <RouterLink to="/login" class="border-b border-foreground pb-px text-foreground">
          Đăng nhập
        </RouterLink>
      </p>
    </div>
  </div>
</template>

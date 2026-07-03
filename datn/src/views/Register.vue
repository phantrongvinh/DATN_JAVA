<script setup>
import { useRouter } from 'vue-router'
import { useForm } from 'vee-validate'
import * as yup from 'yup'
import Field from '@/components/site/Field.vue'
import { useAuthStore } from '@/stores/useAuthStore'
import { storeToRefs } from 'pinia'

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
  <div class="container-x grid gap-12 py-16 md:grid-cols-2">
    <!-- Image -->
    <div class="hidden aspect-[4/5] overflow-hidden bg-secondary md:block">
      <img
        src="https://images.unsplash.com/photo-1517466787929-bc90951d0974?auto=format&fit=crop&w=900&q=80"
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

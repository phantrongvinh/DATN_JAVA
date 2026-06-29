<script setup>
import { watch } from 'vue'
import { Form, Field, ErrorMessage, useForm } from 'vee-validate'
import * as yup from 'yup'

const props = defineProps({
  show: Boolean,

  promotion: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['update:show', 'save'])

const initialValues = {
  name: '',
  discountType: 'PERCENT',
  discountValue: '',
  startTime: '',
  endTime: '',
  isActive: true,
}

const schema = yup.object({
  name: yup.string().required('Tên không được để trống'),

  discountType: yup.string().oneOf(['PERCENT', 'FIXED']).required('Chọn loại giảm giá'),

  discountValue: yup
    .number()
    .typeError('Giá trị phải là số')
    .positive('Giá trị phải lớn hơn 0')
    .required('Giá trị giảm là bắt buộc')
    .when('discountType', {
      is: 'PERCENT',
      then: (schema) => schema.max(100, 'Phần trăm không được vượt quá 100%'),
    }),

  startTime: yup.string().required('Chọn giờ bắt đầu'),

  endTime: yup
    .string()
    .required('Chọn giờ kết thúc')
    .test('is-after-start', 'Giờ kết thúc phải lớn hơn giờ bắt đầu', function (endTime) {
      const { startTime } = this.parent
      if (!startTime || !endTime) return true
      return endTime > startTime
    }),

  isActive: yup.boolean(),
})

const { handleSubmit, resetForm } = useForm({
  validationSchema: schema,
  initialValues,
})

watch(
  () => props.show,
  (show) => {
    if (!show) return

    if (props.promotion) {
      console.log(props.promotion)

      resetForm({
        values: {
          name: props.promotion.name,
          discountType: props.promotion.discountType,
          discountValue: props.promotion.discountValue,
          startTime: props.promotion.startTime,
          endTime: props.promotion.endTime,
          isActive: props.promotion.active,
        },
      })
    } else {
      resetForm({
        values: initialValues,
      })
    }
  },
)

const closeModal = () => {
  emit('update:show', false)
}

const onSubmit = handleSubmit((values) => {
  emit('save', values)

  closeModal()
})
</script>
<template>
  <Transition name="fade">
    <div
      v-if="show"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      @click.self="closeModal"
    >
      <div class="flex max-h-[90vh] w-[700px] flex-col overflow-hidden rounded bg-white shadow-xl">
        <!-- Header -->
        <div class="flex items-center justify-between border-b px-6 py-4">
          <h2 class="text-xl font-semibold">
            {{ promotion ? 'Chỉnh sửa khung giờ vàng' : 'Tạo khung giờ vàng' }}
          </h2>

          <button class="text-2xl text-gray-500 hover:text-black" @click="closeModal">×</button>
        </div>

        <!-- Body -->
        <form id="timePromotionForm" :validation-schema="schema" @submit="onSubmit">
          <div class="flex-1 overflow-y-auto p-6">
            <div class="grid grid-cols-1 gap-5 md:grid-cols-2">
              <!-- Name -->
              <div class="md:col-span-2">
                <label class="mb-2 block font-medium"> Tên khung giờ </label>

                <Field name="name" v-slot="{ field, errorMessage }">
                  <input
                    v-bind="field"
                    class="w-full rounded border p-2"
                    :class="errorMessage ? 'border-red-500' : 'border-border'"
                  />

                  <p v-if="errorMessage" class="mt-1 text-xs text-red-500">
                    {{ errorMessage }}
                  </p>
                </Field>
              </div>

              <!-- Type -->
              <div>
                <label class="mb-2 block font-medium"> Loại giảm </label>

                <Field
                  name="discountType"
                  as="select"
                  class="w-full rounded border border-border p-2"
                >
                  <option value="PERCENT">Phần trăm (%)</option>

                  <option value="FIXED">Tiền mặt</option>
                </Field>
              </div>

              <!-- Value -->
              <div>
                <label class="mb-2 block font-medium"> Giá trị </label>

                <Field
                  name="discountValue"
                  type="number"
                  class="w-full rounded border border-border p-2"
                />

                <ErrorMessage name="discountValue" class="mt-1 block text-xs text-red-500" />
              </div>

              <!-- Start -->
              <div>
                <label class="mb-2 block font-medium"> Giờ bắt đầu </label>

                <Field
                  name="startTime"
                  type="time"
                  class="w-full rounded border border-border p-2"
                />

                <ErrorMessage name="startTime" class="mt-1 block text-xs text-red-500" />
              </div>

              <!-- End -->
              <div>
                <label class="mb-2 block font-medium"> Giờ kết thúc </label>

                <Field name="endTime" type="time" class="w-full rounded border border-border p-2" />

                <ErrorMessage name="endTime" class="mt-1 block text-xs text-red-500" />
              </div>

              <!-- Active -->
              <div class="md:col-span-2">
                <label class="flex items-center gap-3">
                  <Field name="isActive" type="checkbox" />

                  <span>Đang hoạt động</span>
                </label>
              </div>
            </div>
          </div>

          <!-- Footer -->
          <div class="flex justify-end gap-3 border-t px-6 py-4">
            <button type="button" class="border px-6 py-2" @click="closeModal">Huỷ</button>

            <button type="submit" class="bg-ink px-6 py-2 text-white">
              {{ promotion ? 'Cập nhật' : 'Tạo mới' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </Transition>
</template>

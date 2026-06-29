<script setup>
import { computed, watch } from 'vue'
import { Form, Field, ErrorMessage, useForm } from 'vee-validate'
import * as yup from 'yup'
import '@vuepic/vue-datepicker/dist/main.css'
import { VueDatePicker } from '@vuepic/vue-datepicker'

const props = defineProps({
  showPromotionModal: {
    type: Boolean,
    default: false,
  },
  promotion: {
    type: Object,
    default: null,
  },
})

const emit = defineEmits(['update:showPromotionModal', 'save'])

const closeModal = () => {
  emit('update:showPromotionModal', false)
}

const initialValues = {
  name: '',
  discountType: 'PERCENT',
  discountValue: '',
  startAt: new Date(),
  endAt: new Date(),
}

const schema = yup.object({
  name: yup.string().required('Tên khuyến mãi không được để trống'),

  discountType: yup.string().oneOf(['PERCENT', 'FIXED']).required(),

  discountValue: yup
    .number()
    .typeError('Giá trị phải là số')
    .required('Giá trị giảm là bắt buộc')
    .when('discountType', {
      is: 'PERCENT',
      then: (schema) =>
        schema
          .min(1, 'Phần trăm phải lớn hơn 0')
          .max(100, 'Phần trăm giảm không được vượt quá 100%'),
      otherwise: (schema) => schema.min(1, 'Giá trị giảm phải lớn hơn 0'),
    }),

  startAt: yup.date().required('Chọn ngày bắt đầu'),

  endAt: yup
    .date()
    .min(yup.ref('startAt'), 'Ngày kết thúc phải lớn hơn ngày bắt đầu')
    .required('Chọn ngày kết thúc'),
})

const { handleSubmit, resetForm, values, isSubmitting, setFieldValue } = useForm({
  validationSchema: schema,
  initialValues,
})

watch(
  () => props.showPromotionModal,
  (show) => {
    if (!show) return

    if (props.promotion) {
      resetForm({
        values: {
          name: props.promotion.name,
          discountType: props.promotion.discountType.toUpperCase(),
          discountValue: props.promotion.discountValue,
          startAt: props.promotion?.startAt ? new Date(props.promotion.startAt) : new Date(),

          endAt: props.promotion?.endAt ? new Date(props.promotion.endAt) : new Date(),
        },
      })
    } else {
      resetForm({
        values: initialValues,
      })
    }
  },
  {
    immediate: true,
  },
)

const submit = handleSubmit((values) => {
  emit('save', values)
  closeModal()
})

const modalTitle = computed(() => (props.promotion ? 'Chỉnh sửa khuyến mãi' : 'Thêm khuyến mãi'))

const buttonText = computed(() => (props.promotion ? 'Cập nhật' : 'Tạo khuyến mãi'))
</script>

<template>
  <Transition name="fade">
    <div
      v-if="props.showPromotionModal"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
      @click.self="closeModal"
    >
      <div
        class="flex max-h-[90vh] w-full max-w-3xl flex-col overflow-hidden rounded bg-white shadow-xl"
      >
        <!-- Header -->
        <div class="flex items-center justify-between border-b px-6 py-4">
          <div>
            <h2 class="text-xl font-semibold">{{ modalTitle }}</h2>
            <p class="mt-1 text-sm text-gray-500">Thiết lập chương trình giảm giá</p>
          </div>
          <button @click="closeModal" class="text-2xl text-gray-500 hover:text-black">×</button>
        </div>

        <!-- Body -->
        <div class="flex-1 overflow-y-auto p-6">
          <div class="grid grid-cols-1 gap-5 md:grid-cols-2">
            <!-- Name -->
            <div class="md:col-span-2">
              <label class="mb-2 block font-medium">Tên khuyến mãi</label>
              <Field name="name" v-slot="{ field, errorMessage }">
                <input
                  v-bind="field"
                  type="text"
                  class="w-full rounded border p-2 outline-none focus:border-black"
                  :class="errorMessage ? 'border-red-500' : 'border-border'"
                  placeholder="Ví dụ: Flash Sale Hè 2026"
                />
                <p v-if="errorMessage" class="mt-1 text-xs text-red-500">{{ errorMessage }}</p>
              </Field>
            </div>

            <!-- Discount type -->
            <div>
              <label class="mb-2 block font-medium">Loại giảm giá</label>
              <Field name="discountType" v-slot="{ field }">
                <select
                  v-bind="field"
                  class="w-full rounded border border-border p-2 outline-none focus:border-black"
                >
                  <option value="PERCENT">Phần trăm (%)</option>
                  <option value="FIXED">Số tiền cố định</option>
                </select>
              </Field>
            </div>

            <!-- Value -->
            <div>
              <label class="mb-2 block font-medium">
                Giá trị {{ values.discountType === 'PERCENT' ? '(%)' : '(VNĐ)' }}
              </label>
              <Field name="discountValue" v-slot="{ field, errorMessage }">
                <input
                  v-bind="field"
                  type="number"
                  class="w-full rounded border p-2 outline-none focus:border-black"
                  :class="errorMessage ? 'border-red-500' : 'border-border'"
                />
                <p v-if="errorMessage" class="mt-1 text-xs text-red-500">{{ errorMessage }}</p>
              </Field>
            </div>

            <!-- Start -->
            <div>
              <label class="mb-2 block font-medium">Ngày bắt đầu</label>
              <Field name="startAt" v-slot="{ errorMessage }">
                <VueDatePicker
                  :model-value="values.startAt"
                  @update:model-value="(val) => setFieldValue('startAt', val)"
                  auto-apply
                  :enable-time-picker="false"
                />
                <p v-if="errorMessage" class="mt-1 text-xs text-red-500">{{ errorMessage }}</p>
              </Field>
            </div>

            <!-- End -->
            <div>
              <label class="mb-2 block font-medium">Ngày kết thúc</label>
              <Field name="endAt" v-slot="{ errorMessage }">
                <VueDatePicker
                  :model-value="values.endAt"
                  @update:model-value="(val) => setFieldValue('endAt', val)"
                  auto-apply
                  :enable-time-picker="false"
                  :min-date="values.startAt"
                />
                <p v-if="errorMessage" class="mt-1 text-xs text-red-500">{{ errorMessage }}</p>
              </Field>
            </div>
          </div>
        </div>

        <!-- Footer -->
        <div class="flex justify-end gap-3 border-t px-6 py-4">
          <button
            type="button"
            @click="closeModal"
            class="rounded border border-border px-5 py-2 hover:bg-gray-100"
          >
            Huỷ
          </button>
          <button
            type="button"
            :disabled="isSubmitting"
            @click="submit"
            class="rounded bg-black px-6 py-2 text-white hover:bg-neutral-800 disabled:opacity-50"
          >
            {{ isSubmitting ? 'Đang lưu...' : buttonText }}
          </button>
        </div>
      </div>
    </div>
  </Transition>
</template>

import adminAPI from '@/api/adminAPI'
import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useVoucherStore = defineStore('voucher', () => {
  const loading = ref(false)
  const error = ref(null)
  const message = ref(null)

  const vouchers = ref([])
  const size = ref(5)
  const page = ref(1)
  const totalPages = ref(0)
  const totalElements = ref(0)
  const currentPage = ref(1)

  const keyword = ref('')
  const discountTypeFilter = ref(null)
  const activeFilter = ref(null)
  const stackableFilter = ref(null)
  const personalFilter = ref(null)

  const birthdayPreview = ref(null)
  const birthdayLoading = ref(false)

  const fetchAll = async ({
    newPage = page.value,
    newSize = size.value,
    search = keyword.value,
    discountType = discountTypeFilter.value,
    isActive = activeFilter.value,
    isStackable = stackableFilter.value,
    isPersonal = personalFilter.value,
  } = {}) => {
    loading.value = true
    error.value = null
    try {
      const res = await adminAPI.getAllVocuher({
        page: newPage,
        size: newSize,
        search: search || undefined,
        discountType: discountType || undefined,
        isActive: isActive === null ? undefined : isActive,
        isStackable: isStackable === null ? undefined : isStackable,
        isPersonal: isPersonal === null ? undefined : isPersonal,
      })

      vouchers.value = res.content
      totalPages.value = res.page.totalPages
      totalElements.value = res.page.totalElements
      page.value = res.page.number + 1
      size.value = res.page.size
      currentPage.value = newPage
    } catch (err) {
      error.value = err?.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  const resetFilters = () => {
    keyword.value = ''
    discountTypeFilter.value = null
    activeFilter.value = null
    stackableFilter.value = null
    personalFilter.value = null
    return fetchAll({ newPage: 1 })
  }

  const createVoucher = async (data) => {
    loading.value = true
    error.value = null
    try {
      await adminAPI.creatVoucher(data)
    } catch (err) {
      error.value = err?.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  const updateVoucher = async (id, data) => {
    loading.value = true
    error.value = null
    try {
      await adminAPI.editVoucher(id, data)
    } catch (err) {
      error.value = err?.response?.data?.message
      throw err
    } finally {
      loading.value = false
    }
  }

  //   const deleteVoucher = async (id) => {
  //     loading.value = true
  //     error.value = null
  //     try {
  //       await adminAPI.remove(id)
  //     } catch (err) {
  //       error.value = err?.response?.data?.message
  //       throw err
  //     } finally {
  //       loading.value = false
  //     }
  //   }

  const fetchBirthdayPreview = async () => {
    birthdayLoading.value = true
    try {
      birthdayPreview.value = await adminAPI.birthdayReviewResponse()
    } catch (err) {
      error.value = err?.response?.data?.message
      throw err
    } finally {
      birthdayLoading.value = false
    }
  }

  const generateBirthdayVouchers = async () => {
    birthdayLoading.value = true
    try {
      const res = await adminAPI.generatVoucherBirthday()
      message.value = `Đã tạo ${res.created} voucher và gửi email.`
      return res
    } catch (err) {
      error.value = err?.response?.data?.message
      throw err
    } finally {
      birthdayLoading.value = false
    }
  }

  return {
    loading,
    error,
    message,
    vouchers,
    size,
    page,
    totalPages,
    totalElements,
    currentPage,
    keyword,
    discountTypeFilter,
    activeFilter,
    stackableFilter,
    personalFilter,
    birthdayPreview,
    birthdayLoading,
    fetchAll,
    resetFilters,
    createVoucher,
    updateVoucher,
    // deleteVoucher,
    fetchBirthdayPreview,
    generateBirthdayVouchers,
  }
})

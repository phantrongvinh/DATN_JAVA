import axiosClient from './axiosClient'

const url = '/vouchers'

const applyVoucher = async (code, orderTotal) => {
  const res = await axiosClient.get(`${url}/apply`, { params: { code, orderTotal } })
  return res.data
}

const voucherAPI = {
  applyVoucher,
}

export default voucherAPI

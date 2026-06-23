import axiosClient from './axiosClient'

const url = '/vouchers'

const applyVoucher = async (code, orderTotal) => {
  const res = await axiosClient.get(`${url}/apply`, { params: { code, orderTotal } })
  console.log(res.data)

  return res.data
}

const voucherAPI = {
  applyVoucher,
}

export default voucherAPI

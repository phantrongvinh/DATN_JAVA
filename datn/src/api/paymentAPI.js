import axiosClient from './axiosClient'

const url = '/payment'

const vnpay = async (id) => {
  const res = await axiosClient.get(`${url}/vnpay/${id}`)
  return res.data
}

const repayVnpay = async (id) => {
  const res = await axiosClient.get(`${url}/vnpay/repay/${id}`)
  return res.data
}

const paymentAPI = {
  vnpay,
  repayVnpay,
}

export default paymentAPI

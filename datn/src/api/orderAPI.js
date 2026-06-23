import axiosClient from './axiosClient'

const url = '/orders'

const placeOrder = async (data) => {
  const res = await axiosClient.post(`${url}`, data)
  return res.data
}

const orderAPI = { placeOrder }
export default orderAPI

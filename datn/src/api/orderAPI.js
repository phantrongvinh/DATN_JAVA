import axiosClient from './axiosClient'

const url = '/orders'

const placeOrder = async (data) => {
  console.log(data)

  const res = await axiosClient.post(`${url}`, data)
  return res.data
}

const fetchMyOrder = async () => {
  const res = await axiosClient.get(`${url}`)
  return res.data
}

const fetchOrderById = async (id) => {
  const res = await axiosClient.get(`${url}/${id}`)
  return res.data
}
const cancelOrder = async (id) => {
  const res = await axiosClient.patch(`${url}/${id}/cancel`)
  return res.data
}
const orderAPI = { placeOrder, fetchMyOrder, fetchOrderById, cancelOrder }
export default orderAPI

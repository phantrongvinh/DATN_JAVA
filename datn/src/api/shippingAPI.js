import axiosClient from './axiosClient'

const url = 'shipping-simulator'

const updateOrderStatus = async (orderId, status) => {
  const res = await axiosClient.post(`${url}/orders/${orderId}/${status}`)
  return res.data
}

const shippingAPI = {
  updateOrderStatus,
}

export default shippingAPI

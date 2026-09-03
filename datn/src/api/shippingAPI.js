import axiosClient from './axiosClient'

const url = 'shipping-simulator'

const updateOrderStatus = async (orderId, status) => {
  const res = await axiosClient.post(`${url}/orders/${orderId}/${status}`)
  return res.data
}

const updateReturnStatus = async (returnId, status) => {
  const res = await axiosClient.post(`${url}/returns/${returnId}/${status}`)
  return res.data
}

const shippingAPI = {
  updateOrderStatus,
  updateReturnStatus,
}

export default shippingAPI

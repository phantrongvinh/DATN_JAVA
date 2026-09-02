import axiosClient from './axiosClient'

const url = '/returns'

const requestReturn = async (orderId, formData) => {
  const response = await axiosClient.post(`${url}/orders/${orderId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  })
  return response.data
}

const returnAPI = { requestReturn }

export default returnAPI

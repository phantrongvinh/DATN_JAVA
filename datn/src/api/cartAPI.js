import axiosClient from './axiosClient'

const url = '/cart'

const fetchCart = async () => {
  const res = await axiosClient.get(`${url}`)
  return res.data
}

const addItemServer = async (item) => {
  const res = await axiosClient.post(`${url}/items`, item)
  return res.data
}

const removeItemServer = async (variantId) => {
  const res = await axiosClient.delete(`${url}/items/${variantId}`)
  return res.data
}

const updateQuantityServer = async (variantId, quantity) => {
  const res = await axiosClient.patch(`${url}/items/${variantId}`, null, { params: { quantity } })
  return res.data
}

const mergeCartToServer = async (items) => {
  const res = await axiosClient.post(`${url}/merge`, { items })
  return res.data
}
const cartAPI = {
  fetchCart,
  addItemServer,
  removeItemServer,
  updateQuantityServer,
  mergeCartToServer,
}

export default cartAPI

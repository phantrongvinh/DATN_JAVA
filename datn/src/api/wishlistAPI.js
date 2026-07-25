import axiosClient from './axiosClient'

const url = '/wishlist'

const getWishList = async () => {
  const res = await axiosClient.get(url)
  return res.data
}

const toggle = async (productId) => {
  const res = await axiosClient.post(`${url}/${productId}/toggle`)
  return res.data
}

const getIds = async () => {
  const res = await axiosClient.get(`${url}/ids`)
  return res.data
}

const wishlistAPI = { getWishList, toggle, getIds }

export default wishlistAPI

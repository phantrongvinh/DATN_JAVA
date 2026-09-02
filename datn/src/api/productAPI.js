import axiosClient from './axiosClient'

const url = '/products'

const fetchSpotlightProducts = async () => {
  const res = await axiosClient.get(`${url}/spotlight`)
  return res.data
}

const fetchFilterProducts = async (data) => {
  const res = await axiosClient.get(`${url}`, {
    params: data,
  })
  return res.data
}

const fetchProductById = async (id) => {
  const res = await axiosClient.get(`${url}/${id}`)
  return res.data
}

const fetchProductOnSale = async () => {
  const res = await axiosClient.get(`${url}/sales`)
  return res.data
}

const fetchReviews = async (productId) => {
  const res = await axiosClient.get(`${url}/${productId}/reviews`)
  return res.data
}

const createReview = async (productId, form) => {
  const res = await axiosClient.post(`${url}/${productId}/reviews`, form)
  return res.data
}

const productAPI = {
  fetchSpotlightProducts,
  fetchFilterProducts,
  fetchProductById,
  fetchProductOnSale,
  fetchReviews,
  createReview,
}

export default productAPI

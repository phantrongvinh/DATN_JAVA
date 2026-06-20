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

const productAPI = {
  fetchSpotlightProducts,
  fetchFilterProducts,
  fetchProductById,
}

export default productAPI

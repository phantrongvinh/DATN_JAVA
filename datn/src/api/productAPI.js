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

const productAPI = {
  fetchSpotlightProducts,
  fetchFilterProducts,
}

export default productAPI

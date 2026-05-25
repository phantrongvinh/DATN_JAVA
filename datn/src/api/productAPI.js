import axiosClient from './axiosClient'

const url = '/products'

const fetchSpotlightProducts = async () => {
  const res = await axiosClient.get(`${url}/spotlight`)
  return res.data
}

const productAPI = {
  fetchSpotlightProducts,
}

export default productAPI

import axiosClient from './axiosClient'

const url = '/brands'

const fetchBrand = async () => {
  const res = await axiosClient.get(`${url}`)
  return res.data
}

const brandAPI = {
  fetchBrand,
}
export default brandAPI

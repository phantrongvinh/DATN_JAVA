import axiosClient from './axiosClient'

const url = '/admin'

const fetchProductOverview = async () => {
  const res = await axiosClient.get(`${url}/products`)
  console.log(res.data)

  return res.data
}

const aminAPI = {
  fetchProductOverview,
}

export default aminAPI

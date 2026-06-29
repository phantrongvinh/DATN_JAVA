import axiosClient from './axiosClient'

const url = '/sizes'

const fetchAllSize = async () => {
  const res = await axiosClient.get(`${url}`)
  return res.data
}

const sizeAPI = {
  fetchAllSize,
}

export default sizeAPI

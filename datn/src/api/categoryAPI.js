import axiosClient from './axiosClient'

const url = '/categories'

const fetchCategory = async () => {
  const res = await axiosClient.get(`${url}`)

  return res.data
}

const fetchAccessory = async () => {
  const res = await axiosClient.get(`${url}/accessory`)
  return res.data
}

const categoryAPI = {
  fetchCategory,
  fetchAccessory,
}

export default categoryAPI

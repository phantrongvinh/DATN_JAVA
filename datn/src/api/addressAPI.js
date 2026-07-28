import axiosClient from './axiosClient'

const url = '/addresses'

const fetchAddress = async () => {
  const res = await axiosClient.get(`${url}`)
  return res.data
}
const addAddress = async (data) => {
  const res = await axiosClient.post(`${url}`, data)
  return res.data
}

const deleteAddress = async (id) => {
  const res = await axiosClient.delete(`${url}/${id}`)
  return res.data
}

const setPrimary = async (id) => {
  const res = await axiosClient.patch(`${url}/${id}/primary`)
  return res.data
}

const addressAPI = {
  fetchAddress,
  addAddress,
  deleteAddress,
  setPrimary,
}

export default addressAPI

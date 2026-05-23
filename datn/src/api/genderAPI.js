import axiosClient from './axiosClient'

const url = '/genders'

const getAllGender = async () => {
  const res = await axiosClient.get(`${url}`)
  return res.data
}

const genderAPI = {
  getAllGender,
}

export default genderAPI

import axiosClient from './axiosClient'

const url = '/auth'

const register = async (data) => {
  const res = await axiosClient.post(`${url}/register`, data)
  return res.data
}

const login = async (data) => {
  const res = await axiosClient.post(`${url}/login`, data)
  return res.data
}

const authAPI = {
  register,
  login,
}

export default authAPI

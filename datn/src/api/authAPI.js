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

const logout = async () => {
  const res = await axiosClient.post(`${url}/logout`)
  return res.data
}

const resend = async (email) => {
  const res = await axiosClient.post(`${url}/resend-activation`, email)
  return res.data
}

const me = async () => {
  const res = await axiosClient.get(`${url}/me`)
  return res.data
}

const authAPI = {
  register,
  login,
  logout,
  resend,
  me,
}

export default authAPI

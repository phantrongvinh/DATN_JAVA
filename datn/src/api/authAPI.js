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

const forgotPassword = async (email) => {
  const res = await axiosClient.post(`${url}/forgot-password`, { email: email })

  return res.data
}
const resetPassword = async (token, password) => {
  const res = await axiosClient.post(`${url}/reset-password`, {
    token: token,
    password: password,
  })
  return res.data
}

const authAPI = {
  register,
  login,
  logout,
  resend,
  me,
  forgotPassword,
  resetPassword,
}

export default authAPI

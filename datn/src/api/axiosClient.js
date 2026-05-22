import axios from 'axios'

const axiosClient = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 5000,
  responseType: 'json',
  headers: {
    'Content-Type': 'application/json',
  },
})

axiosClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

axiosClient.interceptors.response.use(
  (res) => {
    return res
  },
  (error) => {
    const requestUrl = error.config?.url || ''
    const isLoginRequest = requestUrl.includes('/login')
    if (!isLoginRequest) {
      const status = error.response?.status
      if (status === 401) {
        localStorage.removeItem('token')
        window.location.href = '/login'
      }

      if (status === 403) {
        window.location.href = '/forbidden'
      }
      if (status === 500) {
        console.error('Server error')
      }
      return Promise.reject(error)
    }
  },
)

export default axiosClient

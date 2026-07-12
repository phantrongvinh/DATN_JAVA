import axios from 'axios'
import axiosClient from './axiosClient'

const url = '/admin'

// product
const fetchProductOverview = async () => {
  const res = await axiosClient.get(`${url}/products/top5`)
  return res.data
}

const fetchAllProducts = async (param) => {
  const res = await axiosClient.get(`${url}/products/all`, { params: param })
  return res.data
}

const deactivateProduct = async (id) => {
  const res = await axiosClient.delete(`${url}/products/${id}`)
  return res.data
}

const updateProduct = async (id, data) => {
  const res = await axiosClient.put(`${url}/products/${id}`, data, {
    transformRequest: (data, headers) => {
      delete headers['Content-Type']
      return data
    },
  })
  return res.data
}

const createProduct = async (data) => {
  const res = await axiosClient.post(`${url}/products`, data, {
    transformRequest: (data, headers) => {
      delete headers['Content-Type']
      return data
    },
  })
  return res.data
}

// promotion
const fetchAllActivePromotion = async () => {
  const res = await axiosClient.get(`${url}/promotions`)
  return res.data
}

const addPromotionToProduct = async (promotionId, productIds) => {
  await axiosClient.post(`${url}/promotions/assign`, {
    promotionId,
    productIds,
  })
}

// promotion by product
const fetchAllPromotion = async (param) => {
  const res = await axiosClient.get(`${url}/promotions/all`, { params: param })
  return res.data
}

// promotion by time
const fetchAllTimePromotion = async (param) => {
  const res = await axiosClient.get(`${url}/time-promotions/all`, { params: param })
  return res.data
}

const createTimePromotion = async (data) => {
  const res = await axiosClient.post(`${url}/time-promotions`, data)
  return res.data
}

const toggleTimePromotion = async (id) => {
  const res = await axiosClient.patch(`${url}/time-promotions/${id}`)
  return res.data
}

const updateTimePromotion = async (id, data) => {
  const res = await axiosClient.put(`${url}/time-promotions/${id}`, data)
  return res.data
}

const deleteTimePromotion = async (id) => {
  const res = await axiosClient.delete(`${url}/time-promotions/${id}`)
  return res.data
}

// order
const fetchAllOrders = async (param) => {
  console.log(param)

  const res = await axiosClient.get(`${url}/orders`, { params: param })
  return res.data
}

const updateStatusOrder = async (id, status) => {
  const res = await axiosClient.patch(`${url}/orders/${id}/status`, null, { params: { status } })
  return res.data
}

// user
const fetchAlluser = async (params) => {
  const res = await axiosClient.get(`${url}/users`, { params: params })

  return res.data
}

// dashboard
const fetchDashboard = async (params) => {
  const res = await axiosClient.get(`${url}/dashboard`, { params: params })
  return res.data
}

const adminAPI = {
  fetchProductOverview,
  fetchAllProducts,
  deactivateProduct,
  updateProduct,
  createProduct,
  fetchAllActivePromotion,
  addPromotionToProduct,
  fetchAllPromotion,
  fetchAllTimePromotion,
  createTimePromotion,
  toggleTimePromotion,
  updateTimePromotion,
  deleteTimePromotion,
  fetchAllOrders,
  updateStatusOrder,
  fetchAlluser,
  fetchDashboard,
}

export default adminAPI

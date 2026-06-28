import axios from 'axios'
import axiosClient from './axiosClient'

const url = '/admin'

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

const adminAPI = {
  fetchProductOverview,
  fetchAllProducts,
  deactivateProduct,
  updateProduct,
  createProduct,
}

export default adminAPI

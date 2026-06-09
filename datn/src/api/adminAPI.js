import axiosClient from './axiosClient'

const url = '/admin'

const fetchProductOverview = async () => {
  const res = await axiosClient.get(`${url}/products`)
  return res.data
}

const fetchAllProducts = async (param) => {
  const res = await axiosClient.get(`${url}/products/all`, { params: param })
  return res.data
}

const deleteProductById = async (id) => {
  const res = await axiosClient.patch(`${url}/products/delete/${id}`)
  return res.data
}

const updateProductById = async (data) => {
  const res = await axiosClient.put(`${url}/products/update`, data)
  return res.data
}

const adminAPI = {
  fetchProductOverview,
  fetchAllProducts,
  deleteProductById,
  updateProductById,
}

export default adminAPI

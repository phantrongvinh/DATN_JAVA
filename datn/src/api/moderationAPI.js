import axiosClient from './axiosClient'

const url = '/moderation'

const check = async (text) => {
  const res = await axiosClient.post(`${url}/check`, { check })
  return res
}

const moderationAPI = { check }
export default moderationAPI

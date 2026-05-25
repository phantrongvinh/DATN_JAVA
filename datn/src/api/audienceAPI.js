import axiosClient from './axiosClient'

const url = '/audiences'

const fetchAudiences = async () => {
  const res = await axiosClient.get(`${url}`)
  return res.data
}

const audienceAPI = {
  fetchAudiences,
}

export default audienceAPI

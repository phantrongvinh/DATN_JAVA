import axiosClient from './axiosClient'

const url = '/time-promotions'

const fetchActiveTimePromotion = async () => {
  const res = await axiosClient.get(`${url}/active`)
  console.log(res.data)

  return res.data
}

const fetchAllTimePromotion = async () => {
  const res = await axiosClient.get(`${url}/`)
}

const timePromotionAPI = {
  fetchActiveTimePromotion,
}

export default timePromotionAPI

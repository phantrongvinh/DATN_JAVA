import axiosClient from "./axiosClient"

const url = "/time-promotions"

const fetchActiveTimePromotion = async () =>{
    const res = await axiosClient.get(`${url}/active`);
    return res.data
}

const timePromotionAPI = {
    fetchActiveTimePromotion
}

export default timePromotionAPI;
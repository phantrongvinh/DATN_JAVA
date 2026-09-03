import axiosClient from './axiosClient'

export default {
  sendMessage: (history, message) => axiosClient.post('/chat', { history, message }),
}

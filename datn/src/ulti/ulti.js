const formatLabel = (str) => {
  return str.replace(/([A-Z])/g, ' $1').replace(/^./, (c) => c.toUpperCase())
}

function getFirstThreeWords(str) {
  return str.trim().split(/\s+/).slice(0, 3).join(' ')
}

function formatDate(dateString) {
  if (!dateString) return ''

  const date = new Date(dateString)

  return date.toLocaleDateString('vi-VN')
}

function formatVND(amount) {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
    minimumFractionDigits: 0,
  }).format(amount)
}

const formatLocalDateTime = (date) => {
  if (!date) return null

  const pad = (n) => String(n).padStart(2, '0')

  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

const ulti = {
  formatLabel,
  getFirstThreeWords,
  formatDate,
  formatVND,
  formatLocalDateTime,
}

export default ulti

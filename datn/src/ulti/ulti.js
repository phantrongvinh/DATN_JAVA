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

function formatBirthDay(dateStr) {
  if (!dateStr) return null
  const d = new Date(dateStr)
  const months = [
    'Tháng 1',
    'Tháng 2',
    'Tháng 3',
    'Tháng 4',
    'Tháng 5',
    'Tháng 6',
    'Tháng 7',
    'Tháng 8',
    'Tháng 9',
    'Tháng 10',
    'Tháng 11',
    'Tháng 12',
  ]
  return `${d.getDate()} ${months[d.getMonth()]}, ${d.getFullYear()}`
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
  formatBirthDay,
}

export default ulti

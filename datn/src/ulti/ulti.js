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

const ulti = {
  formatLabel,
  getFirstThreeWords,
  formatDate,
}

export default ulti

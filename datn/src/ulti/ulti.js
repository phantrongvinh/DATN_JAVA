const formatLabel = (str) => {
  return str.replace(/([A-Z])/g, ' $1').replace(/^./, (c) => c.toUpperCase())
}

function getFirstThreeWords(str) {
  return str.trim().split(/\s+/).slice(0, 3).join(' ')
}

const ulti = {
  formatLabel,
  getFirstThreeWords,
}

export default ulti

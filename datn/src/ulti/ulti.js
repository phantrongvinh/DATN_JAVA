const formatLabel = (str) => {
  return str.replace(/([A-Z])/g, ' $1').replace(/^./, (c) => c.toUpperCase())
}

const ulti = {
  formatLabel,
}

export default ulti

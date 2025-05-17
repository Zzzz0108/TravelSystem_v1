import axios from 'axios'

// 创建axios实例
const instance = axios.create({
  baseURL: 'http://localhost:9090', // 后端API地址
  timeout: 30000, // 请求超时时间
  withCredentials: true // 允许跨域请求携带cookie
})

// 请求拦截器
instance.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
instance.interceptors.response.use(
  response => {
    return response
  },
  error => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 未授权，跳转到登录页
          window.location.href = '/login'
          break
        case 403:
          // 权限不足
          console.error('权限不足')
          break
        default:
          console.error('请求失败:', error.response.data)
      }
    }
    return Promise.reject(error)
  }
)

export default instance 
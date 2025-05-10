import axios from 'axios'

// 创建 axios 实例
const service = axios.create({
  baseURL: 'http://localhost:9090', // 后端服务地址
  timeout: 10000, // 请求超时时间
  withCredentials: true // 允许跨域携带cookie
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么
    console.log('发送请求:', {
      url: config.url,
      method: config.method,
      baseURL: config.baseURL,
      headers: config.headers,
      data: config.data
    })
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    console.log('接收响应:', {
      url: response.config.url,
      status: response.status,
      data: response.data
    })
    return response
  },
  error => {
    // 对响应错误做点什么
    if (error.response) {
      console.error('响应错误:', {
        status: error.response.status,
        data: error.response.data,
        headers: error.response.headers
      })
    } else if (error.request) {
      console.error('请求未收到响应:', {
        request: error.request,
        message: error.message
      })
    } else {
      console.error('请求配置错误:', {
        message: error.message,
        config: error.config
      })
    }
    return Promise.reject(error)
  }
)

export default service 
 
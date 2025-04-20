import { defineStore } from 'pinia'
import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  withCredentials: true
})

// 添加请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export const useUserStore = defineStore('user', {
  state: () => {
    // 初始化时设置默认用户
    const initUser = () => {
      if (!localStorage.getItem('isLogin')) {
        localStorage.setItem('isLogin', 'false')
        localStorage.setItem('user', JSON.stringify({
          id: null,
          username: '',
          avatar: ''
        }))
      }
      return {
        isLogin: localStorage.getItem('isLogin') === 'true',
        user: JSON.parse(localStorage.getItem('user')) || {
          id: null,
          username: '',
          avatar: ''
        }
      }
    }
    
    return initUser()
  },
  actions: {
    async register(form) {
      try {
        console.log('开始注册请求:', form)
        const response = await api.post('/auth/register', form)
        console.log('注册响应:', response)
        if (response.status === 200) {
          return true
        }
        throw new Error(response.data)
      } catch (error) {
        console.error('注册错误:', error)
        throw new Error(error.response?.data || '注册失败')
      }
    },
    async login(form) {
      try {
        console.log('开始登录请求:', form)
        const response = await api.post('/auth/login', form)
        console.log('登录响应:', response)
        if (response.status === 200 && response.data) {
          this.isLogin = true
          this.user = {
            id: response.data.user.id,
            username: response.data.user.username,
            avatar: response.data.user.avatar || `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(response.data.user.username)}`
          }
          localStorage.setItem('isLogin', 'true')
          localStorage.setItem('user', JSON.stringify(this.user))
          localStorage.setItem('token', response.data.token)
          return true
        }
        throw new Error(response.data)
      } catch (error) {
        console.error('登录错误:', error)
        throw new Error(error.response?.data || '登录失败')
      }
    },
    logout() {
      this.isLogin = false
      this.user = {
        id: null,
        username: '',
        avatar: ''
      }
      localStorage.removeItem('isLogin')
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    },
    async checkLoginStatus() {
      try {
        const response = await api.get('/auth/check')
        if (response.status === 200 && response.data.username) {
          this.isLogin = true
          this.user = {
            id: response.data.id,
            username: response.data.username,
            avatar: response.data.avatar || `https://api.dicebear.com/7.x/initials/svg?seed=${encodeURIComponent(response.data.username)}`
          }
          localStorage.setItem('isLogin', 'true')
          localStorage.setItem('user', JSON.stringify(this.user))
          return true
        }
        this.logout()
        return false
      } catch (error) {
        console.error('检查登录状态错误:', error)
        this.logout()
        return false
      }
    }
  }
})

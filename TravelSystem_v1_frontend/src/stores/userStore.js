import { defineStore } from 'pinia'
import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  withCredentials: true
})

export const useUserStore = defineStore('user', {
  state: () => {
    // 初始化时设置默认用户
    const initUser = () => {
      if (!localStorage.getItem('isLogin')) {
        localStorage.setItem('isLogin', 'false')
        localStorage.setItem('user', JSON.stringify({
          username: '',
          avatar: ''
        }))
      }
      return {
        isLogin: localStorage.getItem('isLogin') === 'true',
        user: JSON.parse(localStorage.getItem('user')) || {
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
        if (response.status === 200) {
          this.isLogin = true
          this.user = {
            username: form.username,
            avatar: `https://api.dicebear.com/7.x/initials/svg?seed=${form.username}`
          }
          localStorage.setItem('isLogin', 'true')
          localStorage.setItem('user', JSON.stringify(this.user))
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
        username: '',
        avatar: ''
      }
      localStorage.removeItem('isLogin')
      localStorage.removeItem('user')
    },
    async checkLoginStatus() {
      try {
        const response = await api.get('/auth/check')
        if (response.status === 200) {
          this.isLogin = true
          this.user = {
            username: response.data.username,
            avatar: `https://api.dicebear.com/7.x/initials/svg?seed=${response.data.username}`
          }
          localStorage.setItem('isLogin', 'true')
          localStorage.setItem('user', JSON.stringify(this.user))
          return true
        }
        return false
      } catch (error) {
        console.error('检查登录状态错误:', error)
        this.logout()
        return false
      }
    }
  }
})

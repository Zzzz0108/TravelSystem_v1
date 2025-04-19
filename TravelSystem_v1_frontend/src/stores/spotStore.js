import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  withCredentials: true // 允许跨域请求携带认证信息
})

export const useSpotStore = defineStore('spot', () => {
  const spots = ref([])
  const favoriteSpots = ref([])
  const loading = ref(false)
  const error = ref(null)

  // 按热度排序的计算属性
  const sortedSpots = computed(() => {
    return [...spots.value].sort((a, b) => b.popularity - a.popularity)
  })

  const fetchSpots = async () => {
    try {
      loading.value = true
      const response = await api.get('/spots')
      spots.value = response.data
    } catch (err) {
      error.value = err.message
      console.error('获取景点列表失败:', err)
    } finally {
      loading.value = false
    }
  }

  const searchSpots = async (keyword, type) => {
    try {
      loading.value = true
      const response = await api.get('/spots/search', {
        params: { keyword, type }
      })
      spots.value = response.data
    } catch (err) {
      error.value = err.message
      console.error('搜索景点失败:', err)
    } finally {
      loading.value = false
    }
  }

  const incrementPopularity = async (id) => {
    if (!id) {
      console.error('spotId is undefined')
      throw new Error('景点ID不能为空')
    }
    
    try {
      const response = await api.post(`/spots/${id}/increment-popularity`)
      const updatedSpot = response.data.spot
      const index = spots.value.findIndex(s => s.id === id)
      if (index !== -1) {
        spots.value[index] = updatedSpot
      }
      return response.data.redirectUrl
    } catch (err) {
      console.error('增加热度失败:', err)
      throw err
    }
  }

  const addFavorite = async (id) => {
    if (!id) {
      console.error('spotId is undefined')
      throw new Error('景点ID不能为空')
    }
    
    try {
      await api.post(`/spots/${id}/favorite`)
      const spot = spots.value.find(s => s.id === id)
      if (spot) {
        favoriteSpots.value.push(spot)
      }
    } catch (err) {
      console.error('收藏景点失败:', err)
      throw err
    }
  }

  const removeFavorite = async (id) => {
    try {
      await api.delete(`/spots/${id}/favorite`)
      favoriteSpots.value = favoriteSpots.value.filter(s => s.id !== id)
    } catch (err) {
      console.error('取消收藏失败:', err)
      throw err
    }
  }

  const fetchFavoriteSpots = async () => {
    try {
      const response = await api.get('/spots/favorites')
      favoriteSpots.value = response.data
    } catch (err) {
      console.error('获取收藏列表失败:', err)
      throw err
    }
  }

  const getFavoriteSpots = async () => {
    try {
      loading.value = true
      const response = await api.get('/spots/favorites')
      favoriteSpots.value = response.data
    } catch (error) {
      console.error('获取收藏景点失败:', error)
      error.value = error.message
    } finally {
      loading.value = false
    }
  }

  const toggleFavorite = async (spotId) => {
    if (!spotId) {
      console.error('spotId is undefined')
      return false
    }
    
    try {
      const response = await api.post(`/spots/${spotId}/favorite`)
      if (response.data.favorited) {
        const spot = spots.value.find(s => s.id === spotId)
        if (spot) {
          favoriteSpots.value.push(spot)
        }
      } else {
        favoriteSpots.value = favoriteSpots.value.filter(s => s.id !== spotId)
      }
      return response.data.favorited
    } catch (error) {
      console.error('切换收藏状态失败:', error)
      throw error
    }
  }

  return {
    spots,
    sortedSpots, // 导出排序后的景点列表
    favoriteSpots,
    loading,
    error,
    fetchSpots,
    searchSpots,
    incrementPopularity,
    addFavorite,
    removeFavorite,
    fetchFavoriteSpots,
    getFavoriteSpots,
    toggleFavorite
  }
}) 
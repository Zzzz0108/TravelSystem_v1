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
    if (!spots.value || !Array.isArray(spots.value)) {
      return []
    }
    return [...spots.value]
      .filter(spot => spot && typeof spot === 'object' && spot.id && spot.name)
      .sort((a, b) => {
        if (!a || !b) return 0
        if (!a.popularity || !b.popularity) return 0
        return b.popularity - a.popularity  // 按人气降序排序
      })
  })

  const fetchSpots = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await api.get('/spots')
      if (response.data && Array.isArray(response.data)) {
        // 确保数据格式正确
        spots.value = response.data.map(spot => ({
          id: spot.id,
          name: spot.name,
          city: spot.city,
          popularity: spot.popularity || 0,
          type: spot.type,
          image: spot.image,
          created_at: spot.created_at,
          updated_at: spot.updated_at,
          isFavorited: spot.isFavorited || false,
          userRating: spot.userRating || null
        }))
      } else {
        spots.value = []
        error.value = '获取景点数据格式错误'
      }
    } catch (err) {
      error.value = '获取景点数据失败，请稍后重试'
      console.error('获取景点数据失败:', err)
      spots.value = []
    } finally {
      loading.value = false
    }
  }

  const searchSpots = async (keyword = '', type = '') => {
    loading.value = true
    error.value = null
    try {
      const response = await api.get('/spots/search', {
        params: {
          keyword,
          type
        }
      })
      if (response.data && Array.isArray(response.data)) {
        // 确保数据格式正确
        spots.value = response.data.map(spot => ({
          id: spot.id,
          name: spot.name,
          city: spot.city,
          popularity: spot.popularity || 0,
          type: spot.type,
          image: spot.image,
          created_at: spot.created_at,
          updated_at: spot.updated_at,
          isFavorited: spot.isFavorited || false,
          userRating: spot.userRating || null
        }))
      } else {
        spots.value = []
        error.value = '搜索数据格式错误'
      }
    } catch (err) {
      error.value = '搜索失败，请稍后重试'
      console.error('搜索失败:', err)
      spots.value = []
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

      const token = localStorage.getItem('token')
      if (!token) {
        throw new Error('未登录，请先登录')
      }

      const response = await api.post(`/spots/${id}/increment-popularity`, null, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })


      if (response.data && response.data.spot) {
        const updatedSpot = {
          id: response.data.spot.id,
          name: response.data.spot.name,
          city: response.data.spot.city,
          popularity: response.data.spot.popularity || 0,
          type: response.data.spot.type,
          image: response.data.spot.image,
          created_at: response.data.spot.created_at,
          updated_at: response.data.spot.updated_at
        }
        const index = spots.value.findIndex(s => s.id === id)
        if (index !== -1) {
          spots.value[index] = updatedSpot
        }
        return response.data.redirectUrl
      }
      throw new Error('更新热度失败')
    } catch (err) {
      console.error('增加热度失败:', err)
      throw err
    }
  }

  const addFavorite = async (id) => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('用户未登录');
      }
      
      console.log('开始添加收藏，景点ID:', id);
      const response = await api.post(`/spots/${id}/toggle-favorite`, null, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      
      if (response.data) {
        console.log('添加收藏成功，响应数据:', response.data);
        // 更新收藏列表
        favoriteSpots.value = [...favoriteSpots.value, response.data];
        // 更新景点列表中的收藏状态
        const spotIndex = spots.value.findIndex(spot => spot.id === id);
        if (spotIndex !== -1) {
          spots.value[spotIndex].favorited = true;
        }
      }
    } catch (error) {
      console.error('添加收藏失败:', error);
      throw error;
    }
  }

  const removeFavorite = async (id) => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        throw new Error('用户未登录');
      }
      
      console.log('开始取消收藏，景点ID:', id);
      const response = await api.post(`/spots/${id}/toggle-favorite`, null, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      
      if (response.data) {
        console.log('取消收藏成功，响应数据:', response.data);
        // 更新收藏列表
        favoriteSpots.value = favoriteSpots.value.filter(spot => spot.id !== id);
        // 更新景点列表中的收藏状态
        const spotIndex = spots.value.findIndex(spot => spot.id === id);
        if (spotIndex !== -1) {
          spots.value[spotIndex].favorited = false;
        }
      }
    } catch (error) {
      console.error('取消收藏失败:', error);
      throw error;
    }
  }

  const fetchFavoriteSpots = async () => {
    try {
      const token = localStorage.getItem('token')
      if (!token) {
        throw new Error('未登录，请先登录')
      }

      const response = await api.get('/spots/favorites', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })

      if (response.data && Array.isArray(response.data)) {
        favoriteSpots.value = response.data.map(spot => ({
          id: spot.id,
          name: spot.name,
          city: spot.city,
          popularity: spot.popularity || 0,
          type: spot.type,
          image: spot.image,
          created_at: spot.created_at,
          updated_at: spot.updated_at
        }))
      } else {
        favoriteSpots.value = []
        throw new Error('获取收藏列表数据格式错误')
      }
    } catch (err) {
      console.error('获取收藏列表失败:', err)
      favoriteSpots.value = []
      throw err
    }
  }

  const toggleFavorite = async (spotId) => {
    if (!spotId) {
      console.error('spotId is undefined')
      return false
    }
    
    try {
      const token = localStorage.getItem('token')
      if (!token) {
        return false
      }
      
      const response = await api.post(`/spots/${spotId}/toggle-favorite`, null, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
      
      if (response.data && typeof response.data === 'boolean') {
        if (response.data) {
          const spot = spots.value.find(s => s.id === spotId)
          if (spot) {
            favoriteSpots.value.push(spot)
          }
        } else {
          favoriteSpots.value = favoriteSpots.value.filter(s => s.id !== spotId)
        }
        return response.data
      }
      return false
    } catch (error) {
      console.error('切换收藏状态失败:', error)
      return false
    }
  }

  const rateSpot = async (spotId, rating) => {
    try {
      const token = localStorage.getItem('token')
      if (!token) {
        throw new Error('未登录，请先登录')
      }

      const response = await api.post(`/spots/${spotId}/rate`, null, {
        params: { rating },
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })

      if (response.data) {
        // 更新本地景点数据
        const index = spots.value.findIndex(s => s.id === spotId)
        if (index !== -1) {
          spots.value[index] = response.data
        }
        return response.data
      }
      throw new Error('评分失败')
    } catch (err) {
      console.error('评分失败:', err)
      throw err
    }
  }

  const getSpotRatings = async (spotId) => {
    try {
      const response = await api.get(`/spots/${spotId}/ratings`)
      return response.data
    } catch (err) {
      console.error('获取评分信息失败:', err)
      throw err
    }
  }

  return {
    spots,
    sortedSpots,
    favoriteSpots,
    loading,
    error,
    fetchSpots,
    searchSpots,
    incrementPopularity,
    addFavorite,
    removeFavorite,
    fetchFavoriteSpots,
    toggleFavorite,
    rateSpot,
    getSpotRatings
  }
}) 
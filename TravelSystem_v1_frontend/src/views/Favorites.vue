<template>
  <main class="favorites-container">
    <h2 class="section-title">我的收藏</h2>
    <div v-if="loading" class="loading-state">
      <p>加载中...</p>
    </div>
    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
    </div>
    <div v-else-if="favorites.length" class="card-grid">
      <div 
        v-for="fav in favorites"
        :key="fav.id"
        class="spot-card"
      >
        <button 
          class="favorite-btn"
          @click.stop="toggleFavorite(fav)"
          :class="{ 'favorited': isFavorited(fav.id) }"
        >
          <svg class="heart-icon" viewBox="0 0 24 24">
            <path :d="heartPath(fav.id)" fill="currentColor"/>
          </svg>
        </button>

        <!-- 卡片媒体内容 -->
        <div class="card-media">
          <img 
            :src="fav.image" 
            :alt="fav.name" 
            class="card-image"
            loading="lazy"
          >
        </div>
        <div class="card-content">
          <h3 class="spot-name">{{ fav.name }}</h3>
          <p class="spot-city">{{ fav.city }}</p>
          <p class="spot-type">{{ fav.type }}</p>
        </div>
      </div>
    </div>
    <div v-else class="empty-state">
      <svg class="empty-icon" viewBox="0 0 24 24">
        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-1-13h2v6h-2zm0 8h2v2h-2z"/>
      </svg>
      <p>暂无收藏内容</p>
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useSpotStore } from '@/stores/spotStore'

const spotStore = useSpotStore()
const favorites = ref([])
const loading = ref(true)
const error = ref(null)

const fetchFavorites = async () => {
  try {
    loading.value = true
    error.value = null
    await spotStore.fetchFavoriteSpots()
    favorites.value = spotStore.favoriteSpots
  } catch (err) {
    console.error('获取收藏列表失败:', err)
    error.value = '获取收藏列表失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const isFavorited = (id) => {
  return favorites.value.some(f => f.id === id)
}

const heartPath = (id) => {
  return isFavorited(id) 
    ? 'M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z'
    : 'M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z'
}

const toggleFavorite = async (spot) => {
  try {
    await spotStore.toggleFavorite(spot.id)
    await fetchFavorites() // 刷新收藏列表
  } catch (err) {
    console.error('切换收藏状态失败:', err)
  }
}

onMounted(() => {
  fetchFavorites()
})
</script>

<style lang="scss" scoped>
.favorites-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.loading-state,
.error-state {
  text-align: center;
  padding: 80px 0;
  color: #86868b;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  color: #86868b;
  
  .empty-icon {
    width: 80px;
    height: 80px;
    fill: #d1d1d6;
    margin-bottom: 20px;
  }
  
  p {
    font-size: 18px;
  }
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-top: 24px;
}

.spot-card {
  position: relative;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;

  &:hover {
    transform: translateY(-4px);
  }
}

.favorite-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 1;

  &:hover {
    background: rgba(255, 255, 255, 1);
    transform: scale(1.1);
  }

  &.favorited .heart-icon path {
    fill: #ff2d55;
  }
}

.heart-icon {
  width: 20px;
  height: 20px;
}

.heart-icon path {
  fill: #86868b;
  transition: fill 0.2s ease;
}

.card-media {
  position: relative;
  padding-top: 56.25%;
  overflow: hidden;
}

.card-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-content {
  padding: 16px;
}

.spot-name {
  margin: 0 0 8px;
  font-size: 18px;
  font-weight: 600;
}

.spot-city {
  margin: 0 0 4px;
  color: #86868b;
  font-size: 14px;
}

.spot-type {
  margin: 0;
  color: #86868b;
  font-size: 14px;
}
</style>
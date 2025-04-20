<template>
  <div class="spot-card" @click="handleCardClick">
    <div class="media-container">
      <img 
        :src="spot.image"
        class="card-image"
        loading="lazy"
      >
      <div class="view-count">
        <svg class="view-icon" viewBox="0 0 24 24">
          <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
        </svg>
        <span>{{ spot.popularity }}</span>
      </div>
    </div>
    
    <div class="card-content">
      <h3 class="title">{{ spot.name }}</h3>
      <div class="meta">
        <p class="city">{{ spot.city }}</p>
        <span class="type">{{ spot.type === 'SCENIC_AREA' ? '景区' : '学校' }}</span>
      </div>
      <div class="action-bar">
        <favorite-button 
          :spot-id="spot.id"
          :initial-favorited="isFavorite"
          @update:favorited="handleFavoriteUpdate"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import FavoriteButton from '@/components/common/FavoriteButton.vue'
import { useSpotStore } from '@/stores/spotStore'

const props = defineProps({
  spot: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && typeof value === 'object' && 
             typeof value.id === 'number' && 
             typeof value.name === 'string' &&
             typeof value.image === 'string' &&
             typeof value.popularity === 'number' &&
             typeof value.city === 'string' &&
             typeof value.type === 'string'
    }
  }
})

const router = useRouter()
const spotStore = useSpotStore()

const isFavorite = computed(() => {
  if (!props.spot || !props.spot.id) return false
  return spotStore.favoriteSpots.some(s => s.id === props.spot.id)
})

const handleCardClick = async (event) => {
  if (!props.spot || !props.spot.id) {
    console.error('无效的景点数据')
    return
  }
  
  // 如果点击的是收藏按钮，不进行跳转
  if (event.target.closest('.action-bar')) {
    return
  }
  
  try {
    await spotStore.incrementPopularity(props.spot.id)
    router.push('/navigation')
  } catch (error) {
    console.error('增加热度失败:', error)
    // 即使增加热度失败，也进行跳转
    router.push('/navigation')
  }
}

const handleFavoriteUpdate = async (favorited) => {
  if (!props.spot || !props.spot.id) {
    console.error('无效的景点数据')
    return
  }
  
  try {
    if (favorited) {
      await spotStore.addFavorite(props.spot.id)
    } else {
      await spotStore.removeFavorite(props.spot.id)
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
  }
}
</script>

<style lang="scss" scoped>
.spot-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-4px);
  }
}

.media-container {
  position: relative;
  padding-top: 56.25%;
  
  .card-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .view-count {
    position: absolute;
    left: 12px;
    bottom: 12px;
    display: flex;
    align-items: center;
    gap: 4px;
    background: rgba(0, 0, 0, 0.6);
    color: white;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 14px;
    
    .view-icon {
      width: 16px;
      height: 16px;
      fill: white;
    }
  }
}

.card-content {
  padding: 16px;
  
  .title {
    font-size: 18px;
    margin: 0 0 12px;
    color: #1d1d1f;
  }
  
  .meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
    
    .city {
      color: #666;
    }
    
    .type {
      background: #f5f5f7;
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 14px;
      color: #666;
    }
  }
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #eee;
}
</style> 
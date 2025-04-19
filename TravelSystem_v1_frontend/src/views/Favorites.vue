<template>
  <main class="favorites-container">
    <h2 class="section-title">我的收藏</h2>
    <div v-if="userStore.favorites.length" class="card-grid">
      <div 
        v-for="fav in userStore.favorites"
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
          <div class="rating-badge">
            <span class="rating-star">★</span>
            {{ fav.rating.toFixed(1) }}
          </div>
        </div>
        <div class="card-content">
          <h3 class="spot-name">{{ fav.name }}</h3>
          <p class="spot-category">{{ fav.category }}</p>
          <div class="tag-container">
            <span 
              v-for="tag in fav.tags" 
              :key="tag" 
              class="tag"
            >
              {{ tag }}
            </span>
          </div>
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
import { useUserStore } from '@/stores/userStore'
import { computed } from 'vue'

const userStore = useUserStore()
const isFavorited = (id) => {
  return userStore.favorites.some(f => f.id === id)
}

const heartPath = (id) => {
  return isFavorited(id) 
    ? 'M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z'
    : 'M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z'
}

const toggleFavorite = (spot) => {
  userStore.toggleFavorite(spot)
}
</script>

<style lang="scss" scoped>
.favorites-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
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
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  padding: 0 10px;
}

.spot-card {
  position: relative;
  background: #ffffff;
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }
}
.card-media {
    position: relative;
    height: 200px;
    overflow: hidden;
  }
.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}


.rating-badge {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 14px;
  display: flex;
  align-items: center;
  backdrop-filter: blur(4px);
}
.rating-star {
    color: #ffd700;
    margin-right: 4px;
  }

  .card-content {
    padding: 18px;
  }

  .spot-name {
    font-size: 20px;
    color: #1d1d1f;
    margin: 0 0 8px;
    font-weight: 600;
  }

  .spot-category {
    color: #86868b;
    font-size: 15px;
    margin: 0 0 12px;
  }

  .tag-container {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .tag {
    background: #f5f5f7;
    color: #1d1d1f;
    padding: 6px 12px;
    border-radius: 20px;
    font-size: 13px;
    transition: background 0.2s ease;
    
    &:hover {
      background: #e0e0e5;
    }
  }
  .favorite-btn {
  position: absolute;
  top: 12px;
  left: 12px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 2;
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 1);
    transform: scale(1.1);
  }

  &.favorited {
    .heart-icon path {
      fill: #ff2d55; // 实心颜色
    }
  }
}

.heart-icon {
  width: 20px;
  height: 20px;
  path {
    fill: #86868b; // 默认颜色
    transition: fill 0.2s ease;
  }
}
</style>
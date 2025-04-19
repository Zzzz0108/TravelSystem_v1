<template>
  <div class="spot-card">
    <img :src="spot.image" :alt="spot.name" class="spot-image">
    <div class="spot-info">
      <h3 class="spot-name">{{ spot.name }}</h3>
      <p class="spot-city">{{ spot.city }}</p>
      <div class="spot-meta">
        <span class="spot-type">{{ formatType(spot.type) }}</span>
        <span class="spot-popularity">热度: {{ spot.popularity }}</span>
      </div>
      <div class="spot-actions">
        <FavoriteButton
          :spot-id="spot.id"
          :initial-favorited="spot.favorited"
          @update:favorited="updateFavorited"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'
import FavoriteButton from '@/components/common/FavoriteButton.vue'

const props = defineProps({
  spot: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['update:favorited'])

const formatType = (type) => {
  const typeMap = {
    UNIVERSITY: '高校',
    PARK: '公园',
    MUSEUM: '博物馆',
    HISTORICAL_SITE: '历史遗迹',
    SHOPPING: '购物中心',
    ENTERTAINMENT: '娱乐场所'
  }
  return typeMap[type] || type
}

const updateFavorited = (favorited) => {
  if (props.spot && props.spot.id) {
    emit('update:favorited', { spotId: props.spot.id, favorited })
  } else {
    console.error('Spot ID is missing')
  }
}
</script>

<style scoped>
.spot-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.spot-card:hover {
  transform: translateY(-4px);
}

.spot-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.spot-info {
  padding: 1rem;
}

.spot-name {
  margin: 0 0 0.5rem;
  font-size: 1.25rem;
  color: #333;
}

.spot-city {
  margin: 0 0 0.5rem;
  color: #666;
  font-size: 0.9rem;
}

.spot-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.spot-type {
  color: #2196f3;
  background: #e3f2fd;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
}

.spot-popularity {
  color: #ff4081;
}

.spot-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}
</style> 
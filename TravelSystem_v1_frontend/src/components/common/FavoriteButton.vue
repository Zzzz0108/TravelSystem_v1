<template>
  <button
    class="favorite-button"
    :class="{ 'favorited': isFavorited }"
    @click.stop="handleClick"
    :disabled="loading"
  >
    <svg class="heart-icon" viewBox="0 0 24 24">
      <path :d="heartPath"/>
    </svg>
  </button>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useSpotStore } from '@/stores/spotStore'
import { ElMessage } from 'element-plus'

const props = defineProps({
  spotId: {
    type: Number,
    required: true
  },
  initialFavorited: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:favorited'])

const spotStore = useSpotStore()
const loading = ref(false)
const isFavorited = ref(props.initialFavorited)

const heartPath = computed(() => {
  return isFavorited.value 
    ? 'M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z'
    : 'M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z'
})

const handleClick = async () => {
  console.log('FavoriteButton: handleClick 开始执行', {
    spotId: props.spotId,
    isFavorited: isFavorited.value,
    timestamp: new Date().toISOString()
  });
  
  try {
    loading.value = true
    const success = await spotStore.toggleFavorite(props.spotId)
    console.log('FavoriteButton: toggleFavorite 执行结果', {
      success,
      spotId: props.spotId,
      timestamp: new Date().toISOString()
    });
    isFavorited.value = success
    emit('update:favorited', success)
  } catch (error) {
    console.error('FavoriteButton: 收藏操作失败', {
      error,
      spotId: props.spotId,
      timestamp: new Date().toISOString()
    });
    ElMessage.error('操作失败，请重试');
    // 不再手动恢复状态，而是重新获取收藏状态
    await spotStore.fetchFavoriteSpots()
    const isCurrentlyFavorited = spotStore.favoriteSpots.some(spot => spot.id === props.spotId)
    isFavorited.value = isCurrentlyFavorited
    emit('update:favorited', isCurrentlyFavorited)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.favorite-button {
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
}

.favorite-button:hover {
  background: rgba(255, 255, 255, 1);
  transform: scale(1.1);
}

.favorite-button.favorited .heart-icon path {
  fill: #ff2d55;
}

.favorite-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.heart-icon {
  width: 20px;
  height: 20px;
}

.heart-icon path {
  fill: #86868b;
  transition: fill 0.2s ease;
}
</style> 
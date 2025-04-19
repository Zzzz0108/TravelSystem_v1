<!-- src/components/common/LikeButton.vue -->
<template>
  <button 
    class="like-button"
    :class="{ 'liked': isLiked }"
    @click="handleLike"
  >
    <svg class="icon" viewBox="0 0 24 24">
      <path :d="likeIconPath"/>
    </svg>
    <span class="count">{{ count }}</span>
  </button>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  count: {
    type: Number,
    default: 0
  },
  isLiked: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:isLiked', 'update:count'])

const likeIconPath = computed(() => 
  props.isLiked 
    ? 'M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z'
    : 'M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z'
)

const handleLike = () => {
  const newCount = props.isLiked ? props.count - 1 : props.count + 1
  emit('update:isLiked', !props.isLiked)
  emit('update:count', newCount)
}
</script>

<style scoped>
.like-button {
  display: flex;
  align-items: center;
  gap: 4px;
  background: none;
  border: none;
  padding: 6px 12px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background: rgba(0, 0, 0, 0.05);
  }

  &.liked {
    color: #ff2d55;

    .icon {
      fill: #ff2d55;
      filter: drop-shadow(0 0 6px rgba(255,45,85,0.5)); /* 霓虹效果 */
    }
  }
}

.icon {
  width: 18px;
  height: 18px;
  fill: #666;
}

.count {
  font-size: 14px;
  font-weight: 500;
}

.heart-icon {
  path {
    transition: fill 0.3s ease, filter 0.3s ease;
  }
  
  &.active {
    path {
      fill: #FF2D55;
      filter: drop-shadow(0 0 8px rgba(255,45,85,0.5));
    }
    animation: heartbeat 0.6s ease;
  }
}

@keyframes heartbeat {
  0% { transform: scale(1); }
  30% { transform: scale(1.3); }
  50% { transform: scale(0.9); }
  70% { transform: scale(1.2); }
  100% { transform: scale(1); }
}



</style>
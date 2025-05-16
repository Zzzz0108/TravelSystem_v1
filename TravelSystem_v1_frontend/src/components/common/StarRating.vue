<template>
  <div class="star-rating">
    <div class="stars">
      <span
        v-for="star in 5"
        :key="star"
        class="star"
        :class="{ 'active': star <= modelValue }"
        @click="updateRating(star)"
      >
        ★
      </span>
    </div>
    <span class="rating-text" v-if="showText">
      {{ modelValue }}分 ({{ ratingCount }}人评分)
    </span>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  modelValue: {
    type: Number,
    required: true
  },
  ratingCount: {
    type: Number,
    default: 0
  },
  showText: {
    type: Boolean,
    default: true
  },
  readonly: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:modelValue'])

const updateRating = (rating) => {
  if (!props.readonly) {
    emit('update:modelValue', rating)
  }
}
</script>

<style scoped>
.star-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.stars {
  display: flex;
  gap: 4px;
}

.star {
  font-size: 24px;
  color: #ddd;
  cursor: pointer;
  transition: color 0.2s;
}

.star.active {
  color: #ffd700;
}

.rating-text {
  font-size: 14px;
  color: #666;
}
</style> 
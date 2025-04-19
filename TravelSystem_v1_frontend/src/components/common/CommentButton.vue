<template>
  <button class="comment-button" @click="handleClick">
    <i class="iconfont icon-comment"></i>
    <span v-if="count !== undefined">{{ count }}</span>
  </button>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  count: {
    type: Number,
    default: undefined
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click')
}
</script>

<style scoped>
.comment-button {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border: none;
  background: none;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
}

.comment-button:hover {
  color: #006aff;
}

.icon-comment {
  font-size: 16px;
}
</style> 
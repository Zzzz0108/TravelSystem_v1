<template>
  <button class="share-button" @click="handleClick">
    <i class="iconfont icon-share"></i>
  </button>
</template>

<script setup>
import { defineEmits } from 'vue'

const emit = defineEmits(['click'])

const handleClick = () => {
  emit('click')
}
</script>

<style scoped>
.share-button {
  display: flex;
  align-items: center;
  padding: 4px 8px;
  border: none;
  background: none;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
}

.share-button:hover {
  color: #006aff;
}

.icon-share {
  font-size: 16px;
}
</style> 
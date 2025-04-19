<!-- src/components/common/LocationPicker.vue -->
<template>
  <div class="location-picker">
    <input
      v-model="searchQuery"
      placeholder="输入地点名称"
      @input="handleSearch"
    >
    <!-- 地图选择功能需后续实现 -->
  </div>
</template>

<script setup>
import { ref } from 'vue'

const searchQuery = ref('')

const handleSearch = () => {
  // 后续实现地点搜索逻辑
}
</script>
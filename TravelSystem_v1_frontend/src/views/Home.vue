<template>
  <div class="home">
    <div class="search-bar">
      <input 
        type="text" 
        v-model="searchKeyword"
        placeholder="搜索景点名称..."
        @input="handleSearch"
        aria-label="搜索景点"
      >
      <select 
        v-model="selectedType" 
        @change="handleSearch"
        aria-label="景点类型"
      >
        <option value="">全部类型</option>
        <option 
          v-for="type in spotTypes" 
          :key="type.value" 
          :value="type.value"
        >
          {{ type.label }}
        </option>
      </select>
    </div>

    <div v-if="spotStore.error" class="error-message">
      {{ spotStore.error }}
    </div>

    <div v-if="!spotStore.loading && spotStore.sortedSpots.length === 0" class="empty-state">
      没有找到相关景点
    </div>

    <div class="spot-list" v-if="!spotStore.loading && spotStore.sortedSpots.length > 0">
      <div class="search-info">
        找到 {{ spotStore.sortedSpots.length }} 个景点
      </div>
      <template v-for="spot in spotStore.sortedSpots" :key="spot.id">
        <spot-card 
          v-if="spot && typeof spot === 'object' && spot.id && spot.name"
          :spot="spot"
        />
      </template>
    </div>

    <div v-if="spotStore.loading" class="loading">
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { debounce } from 'lodash-es'
import SpotCard from '@/components/spot/SpotCard.vue'
import { useSpotStore } from '@/stores/spotStore'

const spotStore = useSpotStore()
const searchKeyword = ref('')
const selectedType = ref('')

const spotTypes = [
  { value: 'SCENIC_AREA', label: '景区' },
  { value: 'UNIVERSITY', label: '学校' }
]

onMounted(async () => {
  try {
    await spotStore.fetchSpots()
  } catch (error) {
    console.error('获取景点数据失败:', error)
    spotStore.error = '获取景点数据失败，请稍后重试'
  }
})

const debouncedSearch = debounce(async () => {
  try {
    await spotStore.searchSpots(searchKeyword.value, selectedType.value)
  } catch (error) {
    console.error('搜索失败:', error)
    spotStore.error = '搜索失败，请稍后重试'
  }
}, 300)

const handleSearch = () => {
  spotStore.error = null
  debouncedSearch()
}
</script>

<style lang="scss" scoped>
.home {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  
  @media (max-width: 768px) {
    flex-direction: column;
  }
  
  input, select {
    padding: 12px 16px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 16px;
    background: white;
    
    &:focus {
      outline: none;
      border-color: #007AFF;
    }
  }
  
  input {
    flex: 1;
  }
}

.spot-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.search-info {
  grid-column: 1 / -1;
  margin-bottom: 16px;
  color: #666;
  font-size: 14px;
}

.loading, .empty-state, .error-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #666;
}

.error-message {
  color: #ff4d4f;
  background: #fff2f0;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}

.empty-state {
  color: #999;
  font-size: 16px;
}
</style>
<template>
  <div class="home">
    <div class="search-bar">
      <input 
        type="text" 
        v-model="searchKeyword"
        placeholder="搜索景点名称..."
        @input="handleSearch"
      >
      <select v-model="selectedType" @change="handleSearch">
        <option value="">全部类型</option>
        <option value="SCENIC_AREA">景区</option>
        <option value="UNIVERSITY">学校</option>
      </select>
    </div>

    <div class="spot-list" v-if="!spotStore.loading">
      <spot-card 
        v-for="spot in spotStore.sortedSpots" 
        :key="spot.id"
        :spot="spot"
      />
    </div>
    <div v-else class="loading">
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import SpotCard from '@/components/spot/SpotCard.vue'
import { useSpotStore } from '@/stores/spotStore'

const spotStore = useSpotStore()
const searchKeyword = ref('')
const selectedType = ref('')

onMounted(async () => {
  await spotStore.fetchSpots()
})

const handleSearch = async () => {
  await spotStore.searchSpots(searchKeyword.value, selectedType.value)
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
  
  input {
    flex: 1;
    padding: 12px 16px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 16px;
    
    &:focus {
      outline: none;
      border-color: #007AFF;
    }
  }
  
  select {
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
}

.spot-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #666;
}
</style>
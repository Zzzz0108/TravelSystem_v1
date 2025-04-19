
<template>
  <div class="visited-container">
    <h1>我去过的景点</h1>
    <div class="visited-grid">
      <div 
        v-for="spot in visitedSpots"
        :key="spot.id"
        class="visited-card"
      >
        <img :src="spot.image" :alt="spot.name">
        <div class="card-overlay">
          <h3>{{ spot.name }}</h3>
          <p>{{ formatDate(spot.visitedDate) }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
const visitedSpots = ref([]) // 实际应从用户数据获取

const formatDate = (dateString) => {
  return new Date(dateString).toLocaleDateString()
}
</script>

<style lang="scss">
.visited-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
  
  h1 {
    color: white;
    margin-bottom: 40px;
  }
}

.visited-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.visited-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  aspect-ratio: 1;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  
  .card-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 20px;
    background: linear-gradient(transparent, rgba(0,0,0,0.8));
    color: white;
  }
}
</style>

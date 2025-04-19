<template>
  <div class="diary-container">

    <!-- 搜索和过滤 -->
    <div class="diary-header">
      <search-bar 
        placeholder="搜索旅行日记..."
        @search="handleSearch"
      />
      <div class="filter-tags">
        <span 
          v-for="tag in popularTags"
          :key="tag"
          class="tag"
          :class="{ active: selectedTags.includes(tag) }"
          @click="toggleTag(tag)"
        >
          #{{ tag }}
        </span>
      </div>
      <router-view></router-view>
    </div>
    <floating-action-button @click="openEditor"/>

    <!-- 瀑布流容器 -->
    <div class="masonry-container">
      <div 
        v-for="item in filteredDiaries" 
        :key="item.id" 
        class="masonry-item"
      >
        <diary-card :diary="item"/>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useDiaryStore } from '@/stores/diaryStore'
import SearchBar from '@/components/common/SearchBar.vue'
import DiaryCard from '@/components/diary/DiaryCard.vue'
import FloatingActionButton from '@/components/common/FloatingActionButton.vue'
// DiaryView.vue

const router = useRouter()
const diaryStore = useDiaryStore()
const searchQuery = ref('')
const selectedTags = ref([])
const popularTags = ['旅行攻略', '美食探店', '摄影圣地', '自驾游', '亲子旅行']

const handleSearch = (query) => {
  searchQuery.value = query;
};

// 数据过滤
const filteredDiaries = computed(() => {
  return diaryStore.diaries.filter(diary => {
    const matchSearch = diary.content.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
                       diary.title.toLowerCase().includes(searchQuery.value.toLowerCase())
    const matchTags = selectedTags.value.length === 0 || 
                     selectedTags.value.some(tag => diary.tags.includes(tag))
    return matchSearch && matchTags
  })
})

const toggleTag = (tag) => {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
}

const openDetail = (id) => {
  router.push(`/diary/${id}`)
}

const openEditor = () => {
  router.push('/diary/create')
}
</script>

<style lang="scss" scoped>
.diary-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.diary-header {
  position: sticky;
  top: 60px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  z-index: 100;
  padding: 20px 0;
}

.filter-tags {
  display: flex;
  gap: 12px;
  margin-top: 16px;
  flex-wrap: wrap;
  
  .tag {
    padding: 6px 12px;
    border-radius: 20px;
    background: #f5f5f7;
    color: #666;
    cursor: pointer;
    transition: all 0.2s ease;
    
    &.active {
      background: #0071e3;
      color: white;
    }
    
    &:hover {
      transform: scale(1.05);
    }
  }
}

.masonry-container {
  margin-top: 40px;
  padding-bottom: 80px;
}
.masonry-container {
  column-count: 3; // 默认3列
  column-gap: 24px; // 列间距
  margin-top: 40px;

  .masonry-item {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 8px 24px rgba(0,0,0,0.12);
  }
}
}
/* 响应式适配 */
@media (max-width: 1200px) {
  .masonry-container {
    column-count: 3;
  }
}

@media (max-width: 992px) {
  .masonry-container {
    column-count: 2;
  }
}

@media (max-width: 768px) {
  .masonry-container {
    column-count: 1;
  }
}
</style>
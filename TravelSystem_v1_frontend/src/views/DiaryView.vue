<template>
  <div class="diary-container">

    <!-- 搜索和过滤 -->
    <div class="diary-header">
      <div class="search-container">
        <el-select v-model="searchMode" placeholder="搜索模式" class="search-mode">
          <el-option label="目的地" value="destination"></el-option>
          <el-option label="标题" value="title"></el-option>
          <el-option label="文章内容" value="content"></el-option>
        </el-select>
        <search-bar 
          v-model="searchQuery"
          placeholder="搜索旅行日记..."
          @search="handleSearch"
        />
      </div>
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
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDiaryStore } from '@/stores/diaryStore'
import SearchBar from '@/components/common/SearchBar.vue'
import DiaryCard from '@/components/diary/DiaryCard.vue'
import FloatingActionButton from '@/components/common/FloatingActionButton.vue'
import { ElMessage } from 'element-plus'
// DiaryView.vue

const router = useRouter()
const diaryStore = useDiaryStore()
const searchQuery = ref('')
const searchMode = ref('destination') // 默认搜索模式为目的地
const selectedTags = ref([])
const popularTags = ['旅行攻略', '美食探店', '摄影圣地', '自驾游', '亲子旅行']

// 初始化数据
const initData = async () => {
  try {
    await diaryStore.fetchPopularDiaries() // 默认使用后端的热度+评分排序
  } catch (error) {
    console.error('获取日记数据失败:', error)
  }
}

// 组件挂载时获取数据
onMounted(() => {
  initData()
})

const handleSearch = (query) => {
  searchQuery.value = query;
  if (!query) {
    // 如果没有搜索内容，重新获取所有日记
    initData();
  } else {
    // 有搜索内容时才进行搜索
    diaryStore.searchDiaries(query, searchMode.value);
  }
};

// 数据过滤
const filteredDiaries = computed(() => {
  console.log('Current diaries:', diaryStore.diaries);
  console.log('Search query:', searchQuery.value);
  console.log('Search mode:', searchMode.value);
  
  // 如果后端已经返回了搜索结果，直接使用
  if (searchQuery.value) {
    return diaryStore.diaries;
  }
  
  // 如果没有搜索内容，显示所有日记
  return diaryStore.diaries;
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

.search-container {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;
}

.search-mode {
  width: 120px;
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
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-top: 40px;
  padding-bottom: 80px;

  .masonry-item {
    width: 100%;
  }
}

/* 响应式适配 */
@media (max-width: 1200px) {
  .masonry-container {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .masonry-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 600px) {
  .masonry-container {
    grid-template-columns: 1fr;
  }
}
</style>
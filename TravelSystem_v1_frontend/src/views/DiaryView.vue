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
    </div>
    
    <!-- 路由视图 -->
    <router-view></router-view>
    
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
    
    <!-- 浮动按钮 -->
    <floating-action-button @click="openEditor"/>
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

const openDetail = (id) => {
  router.push(`/diary/${id}`)
}

const openEditor = () => {
  router.push('/diary/create')
}
</script>

<style lang="scss" scoped>
.diary-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.diary-header {
  margin-bottom: 20px;
  
  .search-container {
    display: flex;
    gap: 10px;
    align-items: center;
    
    .search-mode {
      width: 120px;
    }
  }
}

.masonry-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.masonry-item {
  break-inside: avoid;
  margin-bottom: 20px;
}
</style>
<template>
  <div class="diary-detail">
    <div class="media-container">
      <template v-if="diary.images && diary.images.length > 0">
        <img 
          v-for="(img, index) in diary.images" 
          :key="index"
          :src="getImageUrl(img.imageUrl)"
          class="diary-image"
          loading="lazy"
          @click="handlePreview(index)"
          @error="handleImageError"
        >
      </template>
      <img 
        v-else
        :src="defaultImage"
        class="diary-image"
        loading="lazy"
        @error="handleImageError"
      >
    </div>
    <div class="content">
      <h1 class="title">{{ diary.title }}</h1>
      <div class="meta">
        <user-avatar :name="diary.author.username" :src="diary.author.avatar"/>
        <div class="info">
          <p class="username">{{ diary.author.username }}</p>
          <p class="date">{{ formatDate(diary.createdAt) }}</p>
        </div>
      </div>
      <p class="text">{{ diary.content }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { format } from 'date-fns'
import UserAvatar from '@/components/common/UserAvatar.vue'
import defaultImageUrl from '@/assets/images/diaries/default.jpg'

const defaultImage = defaultImageUrl

const props = defineProps({
  diary: {
    type: Object,
    required: true
  }
})

const formatDate = (dateString) => {
  return format(new Date(dateString), 'yyyy-MM-dd HH:mm')
}

const getImageUrl = (url) => {
  if (!url) return defaultImage
  if (url.startsWith('http')) return url
  if (url.startsWith('/api')) return url
  if (url.startsWith('./')) return url  // 对于相对路径的图片，直接返回
  return `/api/${url}`
}

const handlePreview = (index) => {
  // 处理图片预览逻辑
}

const handleImageError = (e) => {
  console.log('图片加载失败，尝试使用默认图片')
  console.log('当前图片路径:', e.target.src)
  console.log('默认图片路径:', defaultImage)
  // 如果当前失败的图片已经是默认图片，不要再次设置，避免无限循环
  if (!e.target.src.endsWith(defaultImage)) {
    e.target.src = defaultImage
  }
}

onMounted(() => {
  console.log('DiaryDetail mounted')
  console.log('Default image path:', defaultImage)
  console.log('Diary images:', props.diary.images)
})
</script>

<style lang="scss" scoped>
.diary-detail {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  
  .media-container {
    position: relative;
    padding-top: 56.25%;
    
    .diary-image {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      object-fit: cover;
      cursor: zoom-in;
    }
  }
  
  .content {
    padding: 24px;
    
    .title {
      font-size: 24px;
      margin: 0 0 16px;
      color: #1d1d1f;
    }
    
    .meta {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 24px;
      
      .info {
        p {
          margin: 0;
          
          &.username {
            font-weight: 500;
            color: #1d1d1f;
          }
          
          &.date {
            color: #666;
            font-size: 14px;
          }
        }
      }
    }
    
    .text {
      color: #333;
      line-height: 1.8;
      white-space: pre-wrap;
    }
  }
}
</style> 
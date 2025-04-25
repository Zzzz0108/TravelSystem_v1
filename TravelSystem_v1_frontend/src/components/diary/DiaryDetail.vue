<template>
  <div class="diary-detail" v-if="diary">
    <div class="media-container">
      <template v-if="diary.images && diary.images.length > 0">
        <img 
          v-for="(img, index) in diary.images" 
          :key="index"
          :src="img.imageUrl"
          class="detail-image"
          loading="lazy"
          @click="handlePreview(index)"
          @error="handleImageError"
        >
      </template>
      <img 
        v-else
        src="./images/diaries/default.jpg"
        class="detail-image"
        loading="lazy"
      >
      <video 
        v-if="diary.video"
        :src="diary.video"
        class="detail-video"
        controls
      ></video>
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
      <div class="text-content">{{ diary.content }}</div>
      
      <div class="action-bar">
        <like-button :count="diary.likes" @click="handleLike"/>
        <comment-button :count="diary.comments.length" @click="scrollToComments"/>
        <share-button @click="handleShare"/>
      </div>
      
      <div class="comments-section" id="comments">
        <h2>评论 ({{ diary.comments.length }})</h2>
        <div class="comment-list">
          <div v-for="comment in diary.comments" :key="comment.id" class="comment">
            <user-avatar :name="comment.author.name" :src="comment.author.avatar"/>
            <div class="comment-content">
              <div class="comment-header">
                <span class="username">{{ comment.author.name }}</span>
                <span class="date">{{ formatDate(comment.createdAt) }}</span>
              </div>
              <p class="text">{{ comment.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div v-else class="loading">
    <p>加载中...</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { format } from 'date-fns'
import { useDiaryStore } from '@/stores/diaryStore'
import UserAvatar from '@/components/common/UserAvatar.vue'
import LikeButton from '@/components/common/LikeButton.vue'
import CommentButton from '@/components/common/CommentButton.vue'
import ShareButton from '@/components/common/ShareButton.vue'

const route = useRoute()
const diaryStore = useDiaryStore()
const diary = ref(null)

const formatDate = (dateString) => {
  return format(new Date(dateString), 'yyyy-MM-dd HH:mm')
}

onMounted(async () => {
  try {
    const diaryId = parseInt(route.params.id)
    console.log('正在获取日记:', diaryId)
    const fetchedDiary = await diaryStore.fetchDiaryById(diaryId)
    console.log('获取到的日记:', fetchedDiary)
    diary.value = fetchedDiary
  } catch (error) {
    console.error('获取日记失败:', error)
  }
  console.log('DiaryDetail mounted')
  console.log('Default image path:', './images/diaries/default.jpg')
})

const handleLike = async () => {
  try {
    const updatedDiary = await diaryStore.addRating(diary.value.id)
    diary.value = updatedDiary
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

const handleShare = () => {
  // 处理分享逻辑
}

const scrollToComments = () => {
  const commentsSection = document.getElementById('comments')
  if (commentsSection) {
    commentsSection.scrollIntoView({ behavior: 'smooth' })
  }
}

const handlePreview = (index) => {
  // 处理图片预览逻辑
}

const handleImageError = (e) => {
  console.error('图片加载失败:', e.target.src)
  console.log('当前图片路径:', e.target.src)
  console.log('尝试加载默认图片')
  e.target.src = './images/diaries/default.jpg'
}
</script>

<style lang="scss" scoped>
.diary-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.media-container {
  margin-bottom: 24px;
  border-radius: 12px;
  overflow: hidden;
  
  .detail-image {
    width: 100%;
    height: auto;
    object-fit: cover;
    cursor: zoom-in;
  }
  
  .detail-video {
    width: 100%;
    height: auto;
  }
}

.content {
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
  }
  
  .text-content {
    color: #333;
    line-height: 1.8;
    margin-bottom: 24px;
  }
}

.action-bar {
  display: flex;
  gap: 24px;
  margin: 24px 0;
  padding: 16px 0;
  border-top: 1px solid #eee;
  border-bottom: 1px solid #eee;
}

.comments-section {
  margin-top: 32px;
  
  h2 {
    font-size: 20px;
    margin-bottom: 16px;
  }
  
  .comment {
    display: flex;
    gap: 12px;
    padding: 16px 0;
    border-bottom: 1px solid #eee;
    
    &:last-child {
      border-bottom: none;
    }
    
    .comment-content {
      flex: 1;
      
      .comment-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;
        
        .username {
          font-weight: 500;
        }
        
        .date {
          color: #666;
          font-size: 14px;
        }
      }
      
      .text {
        color: #333;
        line-height: 1.6;
      }
    }
  }
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #666;
}
</style>
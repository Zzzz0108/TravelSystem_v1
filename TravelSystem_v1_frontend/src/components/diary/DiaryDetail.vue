<template>
  <div class="diary-detail" v-if="diaryStore.currentDiary">
    <div class="media-container">
      <template v-if="diaryStore.currentDiary.images && diaryStore.currentDiary.images.length > 0">
        <img 
          v-for="(img, index) in diaryStore.currentDiary.images" 
          :key="index"
          :src="getImageUrl(img.imageUrl)"
          class="detail-image"
          loading="lazy"
          @click="handlePreview(index)"
          @error="handleImageError"
        >
      </template>
      <img 
        v-else-if="!diaryStore.currentDiary.videoUrl"
        src="/images/diaries/default.jpg"
        class="detail-image"
        loading="lazy"
      >
      <video 
        v-if="diaryStore.currentDiary.videoUrl"
        :src="getImageUrl(diaryStore.currentDiary.videoUrl)"
        class="detail-video"
        controls
        @error="handleVideoError"
        @loadeddata="handleVideoLoaded"
      ></video>
    </div>
    
    <div class="content">
      <h1 class="title">{{ diaryStore.currentDiary.title }}</h1>
      <div class="meta">
        <user-avatar 
          :name="diaryStore.currentDiary.author?.username || '未知用户'" 
          :src="diaryStore.currentDiary.author?.avatar || '/images/diaries/default.jpg'"
        />
        <div class="info">
          <p class="username">{{ diaryStore.currentDiary.author?.username || '未知用户' }}</p>
          <p class="date">{{ formatDate(diaryStore.currentDiary.createdAt) }}</p>
        </div>
      </div>
      <div class="text-content">{{ diaryStore.currentDiary.content }}</div>
      
      <div class="action-bar">
        <like-button :count="diaryStore.currentDiary.likes || 0" @click="handleLike"/>
        <comment-button :count="diaryStore.currentDiary.comments?.length || 0" @click="scrollToComments"/>
        <el-rate
          v-model="currentRating"
          :max="5"
          :disabled="!userStore.user"
          show-score
          text-color="#ff9900"
          score-template="{value} 分"
          @change="handleRating"
        />
        <share-button @click="handleShare"/>
      </div>
      
      <div class="comments-section" id="comments">
        <h2>评论 ({{ diaryStore.comments?.length || 0 }})</h2>
        
        <!-- 使用Element Plus重新设计评论输入框 -->
        <el-card class="comment-input-card" shadow="hover">
          <div class="comment-input">
            <el-avatar :size="40" :src="userStore.user?.avatar || '/images/diaries/default.jpg'">
              {{ userStore.user?.username?.charAt(0)?.toUpperCase() || '?' }}
            </el-avatar>
            <div class="input-container">
              <el-input
                v-model="newComment"
                type="textarea"
                :rows="3"
                placeholder="分享你的想法..."
                :maxlength="500"
                show-word-limit
                resize="none"
              />
              <div class="input-actions">
                <el-button 
                  type="primary" 
                  :disabled="!newComment.trim() || !userStore.user"
                  @click="submitComment"
                  :loading="loading"
                >
                  {{ userStore.user ? '发布评论' : '请先登录' }}
                </el-button>
              </div>
            </div>
          </div>
        </el-card>

        <!-- 使用Element Plus重新设计评论列表 -->
        <div class="comment-list">
          <el-card 
            v-for="comment in diaryStore.comments || []" 
            :key="comment.id" 
            class="comment-card"
            shadow="hover"
          >
            <div class="comment">
              <el-avatar :size="40" :src="comment.author?.avatar">
                {{ comment.author?.username?.charAt(0)?.toUpperCase() }}
              </el-avatar>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="username">{{ comment.author?.username || '未知用户' }}</span>
                  <span class="date">{{ formatDate(comment.createdAt) }}</span>
                </div>
                <p class="text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <el-button link size="small">
                    <el-icon><ChatDotRound /></el-icon>
                    回复
                  </el-button>
                  <el-button link size="small">
                    <el-icon><Star /></el-icon>
                    点赞
                  </el-button>
                </div>
              </div>
            </div>
          </el-card>
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
import { useRoute, useRouter } from 'vue-router'
import { format } from 'date-fns'
import { useDiaryStore } from '@/stores/diaryStore'
import { useUserStore } from '@/stores/userStore'
import { ElMessage } from 'element-plus'
import { ChatDotRound, Star } from '@element-plus/icons-vue'
import UserAvatar from '@/components/common/UserAvatar.vue'
import LikeButton from '@/components/common/LikeButton.vue'
import CommentButton from '@/components/common/CommentButton.vue'
import ShareButton from '@/components/common/ShareButton.vue'

const route = useRoute()
const router = useRouter()
const diaryStore = useDiaryStore()
const userStore = useUserStore()
const newComment = ref('')
const loading = ref(false)
const currentRating = ref(0)

const formatDate = (dateString) => {
  try {
    if (!dateString) return '未知时间'
    // 尝试解析日期字符串
    const date = new Date(dateString)
    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      console.error('无效的日期格式:', dateString)
      return '未知时间'
    }
    return format(date, 'yyyy-MM-dd HH:mm')
  } catch (error) {
    console.error('日期格式化错误:', error)
    return '未知时间'
  }
}

onMounted(async () => {
  try {
    const diaryId = parseInt(route.params.id)
    console.log('正在获取日记:', diaryId)
    await diaryStore.fetchDiaryById(diaryId)
    console.log('获取到的日记:', diaryStore.currentDiary)
    
    // 获取评论，添加分页参数
    await diaryStore.fetchComments(diaryId, {
      page: 0,
      size: 100,
      sort: 'createdAt,desc'
    })
    
    // 如果用户已登录，获取用户对该日记的评分
    if (userStore.user) {
      const userRating = await diaryStore.getUserRating(diaryId)
      currentRating.value = userRating
    }
  } catch (error) {
    console.error('获取日记失败:', error)
  }
  console.log('DiaryDetail mounted')
  console.log('Default image path:', '/images/diaries/default.jpg')
})

const handleLike = async () => {
  try {
    const updatedDiary = await diaryStore.addRating(diaryStore.currentDiary.id)
    diaryStore.currentDiary = updatedDiary
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

const getImageUrl = (url) => {
  if (!url) return '/images/diaries/default.jpg'
  if (url.startsWith('http')) return url
  return `http://localhost:9090${url}`
}

const handleImageError = (e) => {
  console.log('图片加载失败:', e.target.src)
  console.log('当前图片路径:', e.target.src)
  console.log('尝试加载默认图片')
  e.target.src = '/images/diaries/default.jpg'
}

const handleVideoError = (e) => {
  console.error('视频加载失败:', e)
  console.error('视频URL:', diaryStore.currentDiary.videoUrl)
  console.error('处理后的URL:', getImageUrl(diaryStore.currentDiary.videoUrl))
  console.error('视频元素:', e.target)
  console.error('错误代码:', e.target.error?.code)
  console.error('错误信息:', e.target.error?.message)
}

const handleVideoLoaded = () => {
  console.log('视频加载成功')
  console.log('视频URL:', diaryStore.currentDiary.videoUrl)
  console.log('处理后的URL:', getImageUrl(diaryStore.currentDiary.videoUrl))
  console.log('视频元素:', document.querySelector('video'))
}

const submitComment = async () => {
  if (!userStore.user || !userStore.isLogin) {
    ElMessage.warning('请先登录后再发表评论');
    return;
  }
  
  if (!newComment.value.trim()) {
    ElMessage.warning('评论内容不能为空');
    return;
  }

  if (!diaryStore.currentDiary?.id) {
    ElMessage.error('无法获取日记信息，请刷新页面重试');
    return;
  }

  try {
    loading.value = true;
    await diaryStore.createComment(diaryStore.currentDiary.id, newComment.value.trim());
    newComment.value = '';
    ElMessage.success('评论发布成功');
  } catch (error) {
    console.error('发布评论失败:', error);
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录');
    } else if (error.response?.status === 403) {
      ElMessage.error('没有权限发表评论，请检查登录状态');
    } else {
      ElMessage.error('评论发布失败，请稍后重试');
    }
  } finally {
    loading.value = false;
  }
};

const handleRating = async (rating) => {
  if (!userStore.user) {
    ElMessage.warning('请先登录后再评分')
    return
  }
  
  if (!userStore.isLogin) {
    ElMessage.warning('登录已过期，请重新登录')
    router.push('/login')
    return
  }

  try {
    loading.value = true
    const updatedDiary = await diaryStore.rateDiary(diaryStore.currentDiary.id, rating)
    if (updatedDiary) {
      diaryStore.currentDiary = updatedDiary
      ElMessage.success('评分成功')
    } else {
      ElMessage.error('评分失败，请稍后重试')
    }
  } catch (error) {
    console.error('评分失败:', error)
    if (error.response?.status === 401) {
      ElMessage.error('登录已过期，请重新登录')
      router.push('/login')
    } else if (error.response?.status === 403) {
      ElMessage.error('没有权限进行评分')
    } else {
      ElMessage.error('评分失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
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
  
  .comment-input-card {
    margin-bottom: 24px;
    border-radius: 12px;
    border: none;
    
    :deep(.el-card__body) {
      padding: 16px;
    }
  }

  .comment-input {
    display: flex;
    gap: 16px;
    
    .input-container {
      flex: 1;
      
      :deep(.el-textarea__inner) {
        border-radius: 8px;
        padding: 12px;
        font-size: 14px;
        line-height: 1.6;
        resize: none;
        border: 1px solid #e0e0e0;
        transition: all 0.3s;
        
        &:focus {
          border-color: #409eff;
          box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
        }
      }
    }
    
    .input-actions {
      margin-top: 12px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .comment-list {
    .comment-card {
      margin-bottom: 16px;
      border-radius: 12px;
      border: none;
      transition: all 0.3s;
      
      &:hover {
        transform: translateY(-2px);
      }
      
      :deep(.el-card__body) {
        padding: 16px;
      }
    }
    
    .comment {
      display: flex;
      gap: 16px;
      
      .comment-content {
        flex: 1;
        
        .comment-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
          
          .username {
            font-weight: 600;
            color: #1a1a1a;
          }
          
          .date {
            color: #8e8e8e;
            font-size: 12px;
          }
        }
        
        .text {
          color: #262626;
          line-height: 1.6;
          margin-bottom: 12px;
        }
        
        .comment-actions {
          display: flex;
          gap: 16px;
          
          :deep(.el-button) {
            padding: 0;
            color: #8e8e8e;
            
            &:hover {
              color: #262626;
            }
            
            .el-icon {
              margin-right: 4px;
            }
          }
        }
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
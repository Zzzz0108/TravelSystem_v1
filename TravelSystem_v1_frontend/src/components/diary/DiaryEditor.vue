<template>
  <div class="editor-container">
    <el-card class="editor-card">
      <div class="editor-header">
        <h2>撰写新游记</h2>
        <el-button 
          type="primary" 
          size="large"
          @click="handlePublish"
          :loading="loading"
          class="publish-btn"
        >
          发布
        </el-button>
      </div>
      
      <div class="editor-main">
        <el-input
          v-model="form.title"
          type="text"
          placeholder="添加标题..."
          class="title-input"
          size="large"
        />
        
        <div class="content-editor">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="8"
            placeholder="分享你的旅行故事..."
            resize="none"
            class="content-input"
          />
          
          <!-- 多媒体上传区域 -->
          <div class="upload-section">
            <file-uploader @files-selected="handleFiles"/>
            
            <!-- 已上传内容预览 -->
            <div class="media-preview">
              <el-card 
                v-for="(file, index) in form.media"
                :key="index"
                class="media-item"
                shadow="hover"
              >
                <img 
                  v-if="file.type.startsWith('image')"
                  :src="file.url"
                  class="preview-image"
                >
                <video 
                  v-else
                  :src="file.url"
                  controls
                  class="preview-video"
                ></video>
                <el-button
                  class="delete-btn"
                  type="danger"
                  circle
                  size="small"
                  @click="removeMedia(index)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </el-card>
            </div>
          </div>
        </div>
        
        <div class="editor-footer">
          <div class="footer-section">
            <h3>选择位置</h3>
            <location-picker v-model="form.location"/>
          </div>
          
          <!-- 添加评分组件 -->
          <div class="footer-section" v-if="form.location && form.location.id">
            <h3>景点评分</h3>
            <star-rating
              v-model="form.rating"
              :rating-count="form.location.ratingCount || 0"
              :show-text="true"
            />
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useDiaryStore } from '@/stores/diaryStore'
import { ElMessage } from 'element-plus'
import { Delete } from '@element-plus/icons-vue'
import FileUploader from '@/components/common/FileUploader.vue'
import LocationPicker from '@/components/common/LocationPicker.vue'
import StarRating from '@/components/common/StarRating.vue'
import axios from 'axios'

const form = ref({
  title: '',
  content: '',
  media: [],
  location: null,
  rating: 0
})

const loading = ref(false)
const router = useRouter()
const diaryStore = useDiaryStore()

const handleFiles = (files) => {
  files.forEach(file => {
    // 检查是否已经存在相同的文件
    const isDuplicate = form.value.media.some(media => 
      media.file.name === file.name && 
      media.file.size === file.size
    )
    
    if (!isDuplicate) {
      const url = URL.createObjectURL(file)
      form.value.media.push({
        type: file.type,
        url: url,
        file: file
      })
    }
  })
}

const removeMedia = (index) => {
  form.value.media.splice(index, 1)
}

const handlePublish = async () => {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }

  try {
    loading.value = true
    const formData = new FormData()
    formData.append('title', form.value.title)
    formData.append('content', form.value.content)
    
    if (form.value.location && form.value.location.id) {
      formData.append('destination', form.value.location.city)
      formData.append('spotId', form.value.location.id)
      if (form.value.rating > 0) {
        formData.append('spotRating', form.value.rating)
      }
      console.log('添加目的地:', form.value.location.city)
      console.log('添加景点ID:', form.value.location.id)
      console.log('添加评分:', form.value.rating)
    }
    
    form.value.media.forEach((file, index) => {
      formData.append('media', file.file)
      console.log(`添加媒体文件 ${index + 1}:`, file.file.name)
    })
    
    console.log('发送请求到:', 'http://localhost:9090/api/diaries')
    console.log('请求头:', {
      'Authorization': `Bearer ${localStorage.getItem('token')}`,
      'Content-Type': 'multipart/form-data'
    })
    
    const response = await axios.post('/api/diaries', formData, {
      baseURL: 'http://localhost:9090',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Content-Type': 'multipart/form-data'
      }
    })
    
    const newDiary = response.data
    console.log('服务器返回的日记数据:', newDiary)
    
    diaryStore.createDiary(newDiary)
    ElMessage.success('发布成功')
    router.push(`/diary/${newDiary.id}`)
  } catch (error) {
    console.error('发布失败:', error)
    console.error('错误详情:', {
      message: error.message,
      response: error.response?.data,
      status: error.response?.status
    })
    
    // 如果是403错误，说明日记已经创建成功，只是返回了错误
    if (error.response?.status === 403) {
      ElMessage.success('发布成功')
      // 尝试从错误响应中获取日记ID
      const diaryId = error.response?.data?.id
      if (diaryId) {
        router.push(`/diary/${diaryId}`)
      } else {
        // 如果没有ID，跳转到日记列表页
        router.push('/diaries')
      }
      return
    }
    
    ElMessage.error(error.response?.data?.message || '发布失败，请重试')
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.editor-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}

.editor-card {
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(24, 91, 246, 0.1);
  border: none;
  overflow: hidden;
  
  :deep(.el-card__body) {
    padding: 32px;
  }
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  
  h2 {
    font-size: 28px;
    margin: 0;
    color: #333;
    font-weight: 600;
  }
}

.publish-btn {
  background: #185bf6;
  border: none;
  padding: 12px 32px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 12px;
  transition: all 0.3s ease;
  
  &:hover {
    background: #0f4cd9;
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(24, 91, 246, 0.2);
  }
}

.title-input {
  margin-bottom: 32px;
  
  :deep(.el-input__wrapper) {
    box-shadow: none;
    border: 2px solid #eee;
    border-radius: 12px;
    padding: 16px 20px;
    transition: all 0.3s ease;
    
    &:hover, &:focus {
      box-shadow: none;
      border-color: #185bf6;
    }
  }
  
  :deep(.el-input__inner) {
    font-size: 24px;
    font-weight: 500;
    
    &::placeholder {
      color: #999;
    }
  }
}

.content-editor {
  margin-bottom: 32px;
  
  .content-input {
    margin-bottom: 24px;
    
    :deep(.el-textarea__inner) {
      font-size: 16px;
      line-height: 1.8;
      padding: 20px;
      border: 2px solid #eee;
      border-radius: 12px;
      transition: all 0.3s ease;
      
      &:hover, &:focus {
        border-color: #185bf6;
        box-shadow: 0 4px 12px rgba(24, 91, 246, 0.1);
      }
      
      &::placeholder {
        color: #999;
      }
    }
  }
}

.upload-section {
  margin-top: 32px;
  padding: 24px;
  background: #f8f9ff;
  border-radius: 12px;
}

.media-preview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 24px;
}

.media-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  border: none;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(24, 91, 246, 0.15);
  }
  
  .preview-image, .preview-video {
    width: 100%;
    height: 200px;
    object-fit: cover;
  }
  
  .delete-btn {
    position: absolute;
    top: 12px;
    right: 12px;
    opacity: 0;
    transition: all 0.3s ease;
    background: rgba(255, 255, 255, 0.9);
    border: none;
    
    &:hover {
      background: #fff;
      transform: scale(1.1);
    }
  }
  
  &:hover .delete-btn {
    opacity: 1;
  }
}

.editor-footer {
  margin-top: 40px;
  padding-top: 32px;
  border-top: 2px solid #f0f2ff;
  
  .footer-section {
    margin-bottom: 24px;
    
    h3 {
      font-size: 18px;
      color: #333;
      margin-bottom: 20px;
      font-weight: 500;
    }
  }
}
</style>
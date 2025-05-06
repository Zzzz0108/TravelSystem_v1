<template>
  <div class="editor-container">
    <div class="editor-header">
      <h2>撰写新游记</h2>
      <button class="publish-btn" @click="handlePublish">
        发布
      </button>
    </div>
    
    <div class="editor-main">
      <div class="form-group">
        <label>标题</label>
        <input 
          v-model="form.title"
          type="text"
          placeholder="添加标题..."
          class="title-input"
          :class="{ 'error': !form.title && isSubmitted }"
        >
        <span class="error-message" v-if="!form.title && isSubmitted">标题不能为空</span>
      </div>
      
      <div class="form-group">
        <label>目的地</label>
        <input 
          v-model="form.destination"
          type="text"
          placeholder="添加目的地..."
          class="destination-input"
        >
      </div>
      
      <div class="form-group">
        <label>内容</label>
        <textarea
          v-model="form.content"
          placeholder="分享你的旅行故事..."
          :class="{ 'error': !form.content && isSubmitted }"
        ></textarea>
        <span class="error-message" v-if="!form.content && isSubmitted">内容不能为空</span>
      </div>
      
      <div class="form-group">
        <label>图片上传</label>
        <file-uploader @files-selected="handleFiles"/>
        
        <div class="media-preview">
          <div 
            v-for="(file, index) in form.media"
            :key="index"
            class="media-item"
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
            <button 
              class="delete-btn"
              @click="removeMedia(index)"
            >
              ×
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useDiaryStore } from '@/stores/diaryStore'
import FileUploader from '@/components/common/FileUploader.vue'
import { ElMessage } from 'element-plus'

const form = ref({
  title: '',
  destination: '',
  content: '',
  media: []
})

const isSubmitted = ref(false)
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

const validateForm = () => {
  isSubmitted.value = true
  return form.value.title && form.value.content
}

const handlePublish = async () => {
  if (!validateForm()) {
    ElMessage.error('请填写必填项')
    return
  }

  try {
    const formData = new FormData()
    formData.append('title', form.value.title)
    formData.append('content', form.value.content)
    formData.append('destination', form.value.destination)
    form.value.media.forEach(file => {
      formData.append('media', file.file)
    })
    
    // 提交到后端
    const response = await fetch('/api/diaries', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: formData
    })
    
    if (!response.ok) {
      throw new Error('发布失败')
    }
    
    const newDiary = await response.json()
    
    // 更新前端store
    diaryStore.addDiary(newDiary)
    
    // 跳转到详情页
    router.push(`/diary/${newDiary.id}`)
    ElMessage.success('发布成功')
  } catch (error) {
    console.error('发布失败:', error)
    ElMessage.error('发布失败，请重试')
  }
}
</script>

<style lang="scss" scoped>
.editor-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  
  h2 {
    font-size: 24px;
    margin: 0;
  }
}

.publish-btn {
  background: #0071e3;
  color: white;
  padding: 10px 24px;
  border: none;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background: #0051a3;
  }
}

.form-group {
  margin-bottom: 24px;
  
  label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: #333;
  }
}

.title-input, .destination-input {
  width: 100%;
  font-size: 24px;
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 8px;
  
  &:focus {
    outline: none;
    border-color: #0071e3;
  }
  
  &.error {
    border-color: #ff4d4f;
  }
}

textarea {
  width: 100%;
  height: 300px;
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  resize: none;
  font-size: 16px;
  line-height: 1.6;
  
  &:focus {
    outline: none;
    border-color: #0071e3;
  }
  
  &.error {
    border-color: #ff4d4f;
  }
}

.error-message {
  color: #ff4d4f;
  font-size: 14px;
}

.media-preview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.media-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  
  .preview-image {
    width: 100%;
    height: 200px;
    object-fit: cover;
  }
  
  .preview-video {
    width: 100%;
    height: 200px;
    object-fit: cover;
  }
  
  .delete-btn {
    position: absolute;
    top: 8px;
    right: 8px;
    background: rgba(0, 0, 0, 0.6);
    color: white;
    border: none;
    width: 24px;
    height: 24px;
    border-radius: 50%;
    cursor: pointer;
  }
}
</style>
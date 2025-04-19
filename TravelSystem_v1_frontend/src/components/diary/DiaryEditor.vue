<template>
  <div class="editor-container">
    <div class="editor-header">
      <h2>撰写新游记</h2>
      <button class="publish-btn" @click="handlePublish">
        发布
      </button>
    </div>
    
    <div class="editor-main">
      <input 
        v-model="form.title"
        type="text"
        placeholder="添加标题..."
        class="title-input"
      >
      
      <div class="content-editor">
        <textarea
          v-model="form.content"
          placeholder="分享你的旅行故事..."
        ></textarea>
        
        <!-- 多媒体上传区域 -->
        <file-uploader @files-selected="handleFiles"/>
        
        <!-- 已上传内容预览 -->
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
      
      <div class="editor-footer">
        <tag-selector v-model="form.tags"/>
        <location-picker v-model="form.location"/>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useDiaryStore } from '@/stores/diaryStore'
import FileUploader from '@/components/common/FileUploader.vue'
import TagSelector from '@/components/common/TagSelector.vue'
import LocationPicker from '@/components/common/LocationPicker.vue'

const form = ref({
  title: '',
  content: '',
  media: [],
  tags: [],
  location: null
})

const router = useRouter()
const diaryStore = useDiaryStore()

const handleFiles = (files) => {
  files.forEach(file => {
    const url = URL.createObjectURL(file)
    form.value.media.push({
      type: file.type,
      url: url,
      file: file
    })
  })
}

const removeMedia = (index) => {
  form.value.media.splice(index, 1)
}

const handlePublish = async () => {
  if (!form.value.title || !form.value.content) {
    alert('请填写标题和内容')
    return
  }

  try {
    const formData = new FormData()
    formData.append('title', form.value.title)
    formData.append('content', form.value.content)
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
    
    const newDiary = await response.json()
    
    // 更新前端store
    diaryStore.addDiary(newDiary)
    
    // 跳转到详情页
    router.push(`/diary/${newDiary.id}`)
  } catch (error) {
    console.error('发布失败:', error)
    alert('发布失败，请重试')
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

.title-input {
  width: 100%;
  font-size: 24px;
  padding: 16px;
  border: none;
  border-bottom: 1px solid #eee;
  margin-bottom: 24px;
  
  &:focus {
    outline: none;
    border-color: #0071e3;
  }
}

.content-editor {
  min-height: 400px;
  
  textarea {
    width: 100%;
    height: 200px;
    padding: 16px;
    border: none;
    resize: none;
    font-size: 16px;
    line-height: 1.6;
    
    &:focus {
      outline: none;
    }
  }
}

.media-preview {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
  margin-top: 24px;
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
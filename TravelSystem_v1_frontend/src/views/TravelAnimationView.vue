<template>
  <div class="travel-animation-container">
    <!-- 标题区域 -->
    <div class="header-section">
      <h1 class="title">AI旅游动画生成</h1>
      <p class="subtitle">上传景点照片，生成精彩旅游动画</p>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧：上传区域 -->
      <div class="upload-section glassmorphism">
        <div class="upload-area" 
             @dragover.prevent 
             @drop.prevent="handleDrop"
             @click="triggerFileInput">
          <input 
            type="file" 
            ref="fileInput" 
            @change="handleFileSelect" 
            accept="image/*" 
            multiple 
            class="hidden"
          >
          <div class="upload-content">
            <i class="upload-icon">📸</i>
            <p>点击或拖拽上传照片</p>
            <p class="upload-hint">支持多张照片上传</p>
          </div>
        </div>
        
        <!-- 已上传照片预览 -->
        <div class="preview-section" v-if="uploadedImages.length > 0">
          <h3>已上传照片</h3>
          <div class="preview-grid">
            <div v-for="(image, index) in uploadedImages" 
                 :key="index" 
                 class="preview-item">
              <img :src="image.preview" :alt="'预览图 ' + (index + 1)">
              <button class="remove-btn" @click="removeImage(index)">×</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：生成选项和预览 -->
      <div class="preview-section glassmorphism">
        <div class="options-panel">
          <h3>生成选项</h3>
          <div class="option-group">
            <label>动画风格</label>
            <select v-model="animationStyle">
              <option value="realistic">写实风格</option>
              <option value="cartoon">卡通风格</option>
              <option value="watercolor">水彩风格</option>
            </select>
          </div>
          <div class="option-group">
            <label>动画时长</label>
            <select v-model="animationDuration">
              <option value="30">30秒</option>
              <option value="60">1分钟</option>
              <option value="120">2分钟</option>
            </select>
          </div>
          <div class="option-group">
            <label>背景音乐</label>
            <select v-model="backgroundMusic">
              <option value="none">无音乐</option>
              <option value="peaceful">舒缓音乐</option>
              <option value="energetic">活力音乐</option>
            </select>
          </div>
        </div>

        <!-- 预览区域 -->
        <div class="animation-preview">
          <div v-if="!generatedAnimation" class="preview-placeholder">
            <p>上传照片并点击生成按钮开始创建动画</p>
          </div>
          <video v-else :src="generatedAnimation" controls></video>
        </div>

        <!-- 生成按钮 -->
        <button 
          class="generate-btn" 
          :disabled="!canGenerate"
          @click="generateAnimation"
        >
          生成动画
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 状态变量
const fileInput = ref(null)
const uploadedImages = ref([])
const animationStyle = ref('realistic')
const animationDuration = ref('60')
const backgroundMusic = ref('none')
const generatedAnimation = ref(null)

// 计算属性
const canGenerate = computed(() => uploadedImages.value.length > 0)

// 方法
const triggerFileInput = () => {
  fileInput.value.click()
}

const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  handleFiles(files)
}

const handleDrop = (event) => {
  const files = Array.from(event.dataTransfer.files)
  handleFiles(files)
}

const handleFiles = (files) => {
  files.forEach(file => {
    if (file.type.startsWith('image/')) {
      const reader = new FileReader()
      reader.onload = (e) => {
        uploadedImages.value.push({
          file: file,
          preview: e.target.result
        })
      }
      reader.readAsDataURL(file)
    }
  })
}

const removeImage = (index) => {
  uploadedImages.value.splice(index, 1)
}

const generateAnimation = async () => {
  // TODO: 实现动画生成逻辑
  console.log('生成动画...')
}
</script>

<style lang="scss" scoped>
.travel-animation-container {
  padding: 2rem;
  min-height: 100vh;
  background: linear-gradient(135deg, #e2e3e6 0%, #b7cef8 100%);
  border-radius: 20px;
}

.header-section {
  text-align: center;
  margin-bottom: 3rem;

  .title {
    font-size: 3rem;
    color: #0041d8;
    margin-bottom: 1rem;
  }

  .subtitle {
    font-size: 1.2rem;
    color: #255dcc;
  }
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.glassmorphism {
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.15);
}

.upload-section {
  .upload-area {
    border: 2px dashed #ccc;
    border-radius: 12px;
    padding: 2rem;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      border-color: #4a90e2;
      background: rgba(74, 144, 226, 0.05);
    }

    .upload-content {
      .upload-icon {
        font-size: 3rem;
        margin-bottom: 1rem;
      }

      p {
        margin: 0.5rem 0;
        color: #666;

        &.upload-hint {
          font-size: 0.9rem;
          color: #999;
        }
      }
    }
  }
}

.preview-section {
  h3 {
    margin-bottom: 1rem;
    color: #2c3e50;
  }

  .preview-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 1rem;
    margin-top: 1rem;

    .preview-item {
      position: relative;
      aspect-ratio: 1;
      border-radius: 8px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .remove-btn {
        position: absolute;
        top: 4px;
        right: 4px;
        background: rgba(255, 255, 255, 0.9);
        border: none;
        border-radius: 50%;
        width: 24px;
        height: 24px;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        color: #666;

        &:hover {
          background: #ff4444;
          color: white;
        }
      }
    }
  }
}

.options-panel {
  margin-bottom: 2rem;

  .option-group {
    margin-bottom: 1rem;

    label {
      display: block;
      margin-bottom: 0.5rem;
      color: #2c3e50;
    }

    select {
      width: 100%;
      padding: 0.5rem;
      border: 1px solid #ddd;
      border-radius: 8px;
      background: white;
    }
  }
}

.animation-preview {
  aspect-ratio: 16/9;
  background: #f8f9fa;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 1rem;

  .preview-placeholder {
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #666;
  }

  video {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.generate-btn {
  width: 100%;
  padding: 1rem;
  background: #1760ff;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  cursor: pointer;
  transition: all 0.3s ease;

  &:hover:not(:disabled) {
    background: #357abd;
  }

  &:disabled {
    background: #ccc;
    cursor: not-allowed;
  }
}

.hidden {
  display: none;
}
</style> 
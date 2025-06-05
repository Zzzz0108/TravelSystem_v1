<template>
  <div class="uploader">
    <input 
      type="file"
      multiple
      accept="image/*, video/*"
      @change="handleFileSelect"
      hidden
      ref="fileInput"
    >
    <button @click="triggerFileSelect">
      <svg viewBox="0 0 24 24">
        <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
      </svg>
      添加图片/视频
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['files-selected'])
const fileInput = ref(null)

const triggerFileSelect = () => {
  fileInput.value.click()
}

const handleFileSelect = (event) => {
  const files = Array.from(event.target.files)
  
  // 检查文件大小和格式
  const validFiles = files.filter(file => {
    // 检查文件大小（100MB限制）
    if (file.size > 100 * 1024 * 1024) {
      ElMessage.error(`文件 ${file.name} 超过100MB大小限制`)
      return false
    }
    
    // 检查文件格式
    const validImageTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
    const validVideoTypes = ['video/mp4', 'video/webm', 'video/ogg']
    
    if (!validImageTypes.includes(file.type) && !validVideoTypes.includes(file.type)) {
      ElMessage.error(`文件 ${file.name} 格式不支持，仅支持图片(jpg/png/gif/webp)和视频(mp4/webm/ogg)格式`)
      return false
    }
    
    return true
  })
  
  if (validFiles.length > 0) {
    emit('files-selected', validFiles)
  }
}
</script>
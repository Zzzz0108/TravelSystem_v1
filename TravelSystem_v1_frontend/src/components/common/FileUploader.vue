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

const emit = defineEmits(['files-selected'])
const fileInput = ref(null)

const triggerFileSelect = () => {
  fileInput.value.click()
}

const handleFileSelect = (e) => {
  const files = Array.from(e.target.files)
  emit('files-selected', files)
}
</script>
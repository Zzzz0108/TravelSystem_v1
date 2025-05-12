<!-- src/components/common/LocationPicker.vue -->
<template>
  <div class="location-picker">
    <el-input
      v-model="searchQuery"
      placeholder="输入地点名称"
      class="location-input"
      @input="handleSearch"
    >
      <template #prefix>
        <el-icon><Location /></el-icon>
      </template>
    </el-input>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Location } from '@element-plus/icons-vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const searchQuery = ref(props.modelValue)

// 监听输入变化
watch(searchQuery, (newValue) => {
  emit('update:modelValue', newValue)
})

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  if (newValue !== searchQuery.value) {
    searchQuery.value = newValue
  }
})

const handleSearch = () => {
  emit('update:modelValue', searchQuery.value)
}
</script>

<style lang="scss" scoped>
.location-picker {
  width: 100%;
  
  .location-input {
    :deep(.el-input__wrapper) {
      box-shadow: none;
      border: 2px solid #eee;
      border-radius: 12px;
      padding: 12px 16px;
      transition: all 0.3s ease;
      
      &:hover, &:focus {
        box-shadow: none;
        border-color: #185bf6;
      }
    }
    
    :deep(.el-input__inner) {
      font-size: 16px;
      
      &::placeholder {
        color: #999;
      }
    }
  }
}
</style>
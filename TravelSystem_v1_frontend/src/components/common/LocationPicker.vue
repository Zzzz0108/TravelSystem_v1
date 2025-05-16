<!-- src/components/common/LocationPicker.vue -->
<template>
  <div class="location-picker">
    <el-autocomplete
      v-model="searchQuery"
      :fetch-suggestions="querySearch"
      placeholder="输入地点名称"
      class="location-input"
      @select="handleSelect"
      :trigger-on-focus="false"
    >
      <template #prefix>
        <el-icon><Location /></el-icon>
      </template>
      <template #default="{ item }">
        <div class="suggestion-item">
          <span class="name">{{ item.name }}</span>
          <span class="city">{{ item.city }}</span>
        </div>
      </template>
    </el-autocomplete>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { Location } from '@element-plus/icons-vue'
import axios from 'axios'

const props = defineProps({
  modelValue: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['update:modelValue'])

const searchQuery = ref('')

// 监听外部值变化
watch(() => props.modelValue, (newValue) => {
  if (newValue) {
    searchQuery.value = newValue.name
  }
})

const querySearch = async (query, callback) => {
  if (query.length < 2) {
    callback([])
    return
  }

  try {
    const response = await axios.get(`/api/spots/search`, {
      params: { keyword: query },
      baseURL: 'http://localhost:9090'
    })
    
    if (response.data && Array.isArray(response.data)) {
      const suggestions = response.data.map(spot => ({
        value: spot.name,
        name: spot.name,
        city: spot.city,
        id: spot.id,
        type: spot.type
      }))
      callback(suggestions)
    } else {
      callback([])
    }
  } catch (error) {
    console.error('搜索地点失败:', error)
    callback([])
  }
}

const handleSelect = (item) => {
  emit('update:modelValue', {
    id: item.id,
    name: item.name,
    city: item.city,
    type: item.type
  })
}
</script>

<style lang="scss" scoped>
.location-picker {
  width: 100%;
  
  .location-input {
    width: 100%;
    
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

.suggestion-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  
  .name {
    font-weight: 500;
    color: #333;
  }
  
  .city {
    color: #666;
    font-size: 14px;
  }
}
</style>
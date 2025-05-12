<!-- src/components/common/TagSelector.vue -->
<template>
  <div class="tag-selector">
    <div 
      v-for="tag in availableTags"
      :key="tag"
      class="tag"
      :class="{ 
        'selected': selectedTags.includes(tag),
        [theme]: true 
      }"
      @click="toggleTag(tag)"
    >
      <span class="tag-text">#{{ tag }}</span>
      <div class="checkmark" v-if="selectedTags.includes(tag)">
        <svg viewBox="0 0 24 24">
          <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
        </svg>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  availableTags: {
    type: Array,
    default: () => []
  },
  theme: {
    type: String,
    default: 'light',
    validator: value => ['light', 'dark'].includes(value)
  }
})

const emit = defineEmits(['update:modelValue'])

const selectedTags = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const toggleTag = (tag) => {
  console.log('标签被点击:', tag)
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
  console.log('当前选中的标签:', selectedTags.value)
}
</script>

<style lang="scss" scoped>
.tag-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-family: 'Inter', sans-serif;
}

.tag {
  padding: 8px 16px;
  border-radius: 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  .checkmark {
    width: 16px;
    height: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    svg {
      width: 12px;
      height: 12px;
      fill: currentColor;
    }
  }

  // Light Theme
  &.light {
    background: rgba(255, 255, 255, 0.9);
    color: #1d1d1f;
    
    
    &.selected {
      background: #0071e3;
      color: white;
      border-color: transparent;
    }
  }

  // Dark Theme
  &.dark {
    background: rgba(255, 255, 255, 0.1);
    color: rgba(255, 255, 255, 0.8);
    
    
    &.selected {
      background: linear-gradient(45deg, #1b17ff, #4fc8f8);
      border-color: transparent;
    }
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
}

.tag-text {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: -0.02em;
}
</style>
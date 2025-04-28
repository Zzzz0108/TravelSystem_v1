<!-- src/components/common/SearchBar.vue -->
<template>
  <div class="search-bar">
    <input
      :value="modelValue"
      @input="$emit('update:modelValue', $event.target.value)"
      @keyup.enter="$emit('search', modelValue)"
      :placeholder="placeholder"
      class="search-input"
    />
    <button class="search-button" @click="$emit('search', modelValue)">
      <svg class="search-icon" viewBox="0 0 24 24">
        <path d="M15.5 14h-.79l-.28-.27A6.47 6.47 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"/>
      </svg>
    </button>
  </div>
</template>

<script setup>
defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  placeholder: {
    type: String,
    default: '搜索...'
  }
})

defineEmits(['update:modelValue', 'search'])
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
  border: 1px solid #e0e0e0;
  border-radius: 24px;
  padding: 8px 16px;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: #0071e3;
  }
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 16px;
  padding: 4px 8px;
}

.search-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
}

.search-icon {
  width: 20px;
  height: 20px;
  fill: #666;
  
  &:hover {
    fill: #0071e3;
  }
}
</style>
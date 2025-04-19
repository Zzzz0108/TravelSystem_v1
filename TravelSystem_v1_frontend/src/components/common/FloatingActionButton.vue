<!-- src/components/common/FloatingActionButton.vue -->
<template>
  <button 
    class="fab-container"
    @click="$emit('click')"
  >
    <svg class="fab-icon" viewBox="0 0 24 24">
      <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
    </svg>
  </button>
</template>

<script setup>
// 空脚本标签保持组件结构
</script>

<style scoped>
.fab-container {
  position: fixed;
  bottom: 32px;
  right: 32px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #0071e3;
  color: white;
  border: none;
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  
  &:hover {
    background: #0051a3;
    transform: scale(1.1);
  }
}

.fab-icon {
  width: 24px;
  height: 24px;
  fill: currentColor;
}

/* 在FloatingActionButton组件中添加样式 */
.floating-btn {
  position: fixed;
  bottom: 40px;
  right: 40px;
  background: #0071e3;
  color: white;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: scale(1.1);
    background: #0051a3;
  }
}

</style>
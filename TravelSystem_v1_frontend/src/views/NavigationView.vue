<template>
  <div class="navigation-view">
    <!-- 导航功能组件 -->
    <Navigation />
  </div>
</template>

<script setup>
import Navigation from '@/components/Navigation.vue'
</script>

<style scoped>
.navigation-view {
  width: 100%;
  min-height: 100vh;
  padding: 0;
  margin: 0;
  overflow: hidden;
}
</style>
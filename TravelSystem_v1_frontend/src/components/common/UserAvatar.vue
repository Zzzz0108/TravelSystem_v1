<template>
  
  <div class="user-avatar">
    <img v-if="src" :src="src" :alt="name" class="avatar-image" />
    <div v-else class="avatar-placeholder">
      {{ name?.charAt(0) ?? '?' }}
    </div>
  </div>
</template>

<script setup>
defineProps({
  src: {
    type: String,
    required: false
  },
  name: {
    type: String,
    required: true
  },
  size: {
    type: [String, Number],
    default: 32
  }
})
</script>

<style scoped>

.user-avatar {
  display: block;
  width: v-bind(size + 'px');
  height: v-bind(size + 'px');
  border-radius: 50%;
  overflow: hidden;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 2px solid #006aff; 
  box-shadow: 0 0 8px rgba(0, 51, 203, 0.6);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}


.avatar-placeholder {
  font-size: calc(v-bind(size) / 2);
  font-weight: bold;
  color: #666;
}
</style>
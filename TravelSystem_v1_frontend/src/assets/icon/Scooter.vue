<!-- src/assets/icons/ScooterIcon.vue -->
<template>
  <svg 
    class="scooter-icon"
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <!-- 车体 -->
    <path 
      d="M5 19L19 19L21 15L17 7L13 5L9 7L5 15L3 19"
      :stroke="color"
      stroke-width="1.5"
      stroke-linecap="round"
    />
    <!-- 车轮 -->
    <circle cx="7" cy="19" r="2" :stroke="color" stroke-width="1.5"/>
    <circle cx="17" cy="19" r="2" :stroke="color" stroke-width="1.5"/>
    <!-- 把手 -->
    <path 
      d="M5 15L9 13M19 15L15 13"
      :stroke="color"
      stroke-width="1.5"
      stroke-linecap="round"
    />
    <!-- 动态电量显示 -->
    <rect 
      class="battery"
      x="11" y="3" 
      width="6" height="3" 
      rx="1" 
      :stroke="color"
      stroke-width="1"
    />
    <path 
      class="charge"
      d="M14 4L14 5"
      :stroke="color"
      stroke-width="1.5"
    />
  </svg>
</template>

<script setup>
defineProps({
  size: {
    type: [Number, String],
    default: 24
  },
  color: {
    type: String,
    default: 'currentColor'
  }
})
</script>

<style scoped>
.scooter-icon {
  transition: all 0.3s ease;
}

.battery {
  animation: pulse 1.5s ease-in-out infinite;
}

.charge {
  animation: charge-flow 1s linear infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.3; }
}

@keyframes charge-flow {
  from { transform: translateY(-1px); }
  to { transform: translateY(1px); }
}
</style>
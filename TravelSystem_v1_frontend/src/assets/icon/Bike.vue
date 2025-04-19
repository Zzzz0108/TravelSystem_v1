<!-- src/assets/icons/BikeIcon.vue -->
<template>
  <svg 
    class="bike-icon"
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <!-- 车架 -->
    <path 
      d="M5 19L10 5L15 19"
      :stroke="color"
      stroke-width="1.5"
      stroke-linecap="round"
    />
    <!-- 车轮 -->
    <circle cx="7" cy="19" r="3" :stroke="color" stroke-width="1.5"/>
    <circle cx="17" cy="19" r="3" :stroke="color" stroke-width="1.5"/>
    <!-- 动态辐条 -->
    <g class="spokes">
      <line x1="7" y1="19" x2="7" y2="16" :stroke="color" stroke-width="1"/>
      <line x1="17" y1="19" x2="17" y2="16" :stroke="color" stroke-width="1"/>
    </g>
    <!-- 踏板 -->
    <path 
      class="pedal"
      d="M10 12L14 12"
      :stroke="color"
      stroke-width="1.5"
      stroke-linecap="round"
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
.bike-icon {
  transition: all 0.3s ease;
}

.spokes {
  animation: rotate 2s linear infinite;
  transform-origin: center;
}

.pedal {
  animation: pedal-rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes pedal-rotate {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(180deg); }
}
</style>
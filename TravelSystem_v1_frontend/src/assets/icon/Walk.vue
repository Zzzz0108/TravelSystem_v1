<!-- src/assets/icons/Walk.vue -->
<template>
  <svg 
    class="walk-icon"
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    aria-hidden="true"
  >
    <!-- 动态小人 -->
    <g class="walking-man">
      <!-- 头部 -->
      <circle cx="12" cy="4" r="2" :fill="color"/>
      <!-- 躯干 -->
      <path 
        d="M12 6L10 14L12 18L14 14L12 6Z" 
        :stroke="color"
        stroke-width="1.5"
      />
      <!-- 动态腿 -->
      <path 
        class="leg-left"
        d="M10 14L8 22"
        :stroke="color"
        stroke-width="1.5"
        stroke-linecap="round"
      />
      <path 
        class="leg-right"
        d="M14 14L16 22"
        :stroke="color"
        stroke-width="1.5"
        stroke-linecap="round"
      />
      <!-- 摆动手臂 -->
      <path 
        class="arm"
        d="M12 8L8 10M12 8L16 6"
        :stroke="color"
        stroke-width="1.5"
        stroke-linecap="round"
      />
    </g>
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
.walk-icon {
  transition: all 0.3s ease;
}

.walking-man {
  animation: walk 1s ease-in-out infinite;
}

.leg-left {
  animation: leg-swing 1s ease-in-out infinite;
}

.leg-right {
  animation: leg-swing 1s ease-in-out infinite;
  animation-delay: 0.5s;
}

.arm {
  animation: arm-swing 1s ease-in-out infinite;
}

@keyframes walk {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-2px); }
}

@keyframes leg-swing {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(10deg); }
}

@keyframes arm-swing {
  0%, 100% { transform: rotate(0deg); }
  50% { transform: rotate(-15deg); }
}
</style>
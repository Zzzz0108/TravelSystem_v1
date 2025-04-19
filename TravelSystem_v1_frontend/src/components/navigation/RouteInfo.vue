<template>
  <div class="route-info">
    <p>距离：{{ route.distance }} 米</p>
    <p>预计时间：{{ route.duration }} 分钟</p>
  </div>
</template>

<script setup>
defineProps({
  route: {
    type: Object,
    required: true
  }
})
</script>
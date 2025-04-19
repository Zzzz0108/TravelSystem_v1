<template>
  <div class="route-input">
    <input v-model="target" placeholder="输入目标（景点或学校）" />
    <select v-model="transport">
      <option value="walking">步行</option>
      <option value="electric-car">电瓶车</option>
      <option value="bicycle">自行车</option>
    </select>
    <button @click="handleCalculate">计算路线</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const target = ref('')
const transport = ref('walking')

const emit = defineEmits(['calculate-route'])

const handleCalculate = () => {
  emit('calculate-route', {
    target: target.value,
    transport: transport.value
  })
}
</script>
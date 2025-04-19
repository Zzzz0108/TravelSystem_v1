<template>
  <div class="filter">
    <select v-model="category" @change="handleFilterChange">
      <option value="all">全部</option>
      <option value="超市">超市</option>
      <option value="卫生间">卫生间</option>
    </select>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const category = ref('all')

const emit = defineEmits(['filter-change'])

const handleFilterChange = () => {
  emit('filter-change', category.value)
}
</script>
<template>
  <div class="diary-list">
    <h2>旅行日记列表</h2>
    <div class="list-container">
      <diary-card 
        v-for="diary in diaries" 
        :key="diary.id"
        :diary="diary"
      />
    </div>
  </div>
</template>

<script setup>
import { useDiaryStore } from '@/stores/diaryStore'
import DiaryCard from '@/components/diary/DiaryCard.vue'

const diaryStore = useDiaryStore()
const diaries = diaryStore.diaries // 直接使用 store 中的日记数据
</script>
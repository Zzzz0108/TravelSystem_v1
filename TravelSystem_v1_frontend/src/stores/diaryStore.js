import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as diaryApi from '@/api/diaryApi'
import * as commentApi from '@/api/commentApi'
import { ElMessage } from 'element-plus'

export const useDiaryStore = defineStore('diary', () => {
  const diaries = ref([])
  const currentDiary = ref(null)
  const comments = ref([])
  const loading = ref(false)
  const error = ref(null)

  // 获取日记列表
  const fetchDiaries = async (params) => {
    try {
      loading.value = true
      const response = await diaryApi.getDiaries(params)
      diaries.value = response.content
    } catch (err) {
      error.value = err.message
      ElMessage.error('获取日记列表失败')
    } finally {
      loading.value = false
    }
  }

  // 获取热门日记
  const fetchPopularDiaries = async (params) => {
    try {
      loading.value = true
      const response = await diaryApi.getPopularDiaries(params)
      diaries.value = response.content
    } catch (err) {
      error.value = err.message
      ElMessage.error('获取热门日记失败')
    } finally {
      loading.value = false
    }
  }

  // 获取最新日记
  const fetchLatestDiaries = async (params) => {
    try {
      loading.value = true
      const response = await diaryApi.getLatestDiaries(params)
      diaries.value = response.content
    } catch (err) {
      error.value = err.message
      ElMessage.error('获取最新日记失败')
    } finally {
      loading.value = false
    }
  }

  // 获取日记详情
  const fetchDiaryById = async (id) => {
    try {
      loading.value = true
      const diary = await diaryApi.getDiaryById(id)
      currentDiary.value = diary
      return diary
    } catch (err) {
      error.value = err.message
      ElMessage.error('获取日记详情失败')
      return null
    } finally {
      loading.value = false
    }
  }

  // 创建日记
  const createDiary = async (diary) => {
    try {
      loading.value = true
      const newDiary = await diaryApi.createDiary(diary)
      diaries.value.unshift(newDiary)
      ElMessage.success('创建日记成功')
      return newDiary
    } catch (err) {
      error.value = err.message
      ElMessage.error('创建日记失败')
      return null
    } finally {
      loading.value = false
    }
  }

  // 更新日记
  const updateDiary = async (id, diary) => {
    try {
      loading.value = true
      const updatedDiary = await diaryApi.updateDiary(id, diary)
      const index = diaries.value.findIndex(d => d.id === id)
      if (index !== -1) {
        diaries.value[index] = updatedDiary
      }
      if (currentDiary.value?.id === id) {
        currentDiary.value = updatedDiary
      }
      ElMessage.success('更新日记成功')
      return updatedDiary
    } catch (err) {
      error.value = err.message
      ElMessage.error('更新日记失败')
      return null
    } finally {
      loading.value = false
    }
  }

  // 删除日记
  const deleteDiary = async (id) => {
    try {
      loading.value = true
      await diaryApi.deleteDiary(id)
      diaries.value = diaries.value.filter(d => d.id !== id)
      if (currentDiary.value?.id === id) {
        currentDiary.value = null
      }
      ElMessage.success('删除日记成功')
    } catch (err) {
      error.value = err.message
      ElMessage.error('删除日记失败')
    } finally {
      loading.value = false
    }
  }

  // 搜索日记
  const searchDiaries = async (keyword, params) => {
    try {
      loading.value = true
      const response = await diaryApi.searchDiaries(keyword, params)
      diaries.value = response.content
    } catch (err) {
      error.value = err.message
      ElMessage.error('搜索日记失败')
    } finally {
      loading.value = false
    }
  }

  // 按标签搜索日记
  const getDiariesByTag = async (tag, params) => {
    try {
      loading.value = true
      const response = await diaryApi.getDiariesByTag(tag, params)
      diaries.value = response.content
    } catch (err) {
      error.value = err.message
      ElMessage.error('获取标签日记失败')
    } finally {
      loading.value = false
    }
  }

  // 点赞/取消点赞
  const toggleLike = async (id) => {
    try {
      const updatedDiary = await diaryApi.toggleLike(id)
      const index = diaries.value.findIndex(d => d.id === id)
      if (index !== -1) {
        diaries.value[index] = updatedDiary
      }
      if (currentDiary.value?.id === id) {
        currentDiary.value = updatedDiary
      }
      return updatedDiary
    } catch (err) {
      error.value = err.message
      ElMessage.error('操作失败')
      return null
    }
  }

  // 获取评论
  const fetchComments = async (diaryId, params) => {
    try {
      loading.value = true
      const response = await commentApi.getCommentsByDiaryId(diaryId, params)
      comments.value = response.content
    } catch (err) {
      error.value = err.message
      ElMessage.error('获取评论失败')
    } finally {
      loading.value = false
    }
  }

  // 创建评论
  const createComment = async (diaryId, comment) => {
    try {
      const newComment = await commentApi.createComment(diaryId, comment)
      comments.value.unshift(newComment)
      if (currentDiary.value) {
        currentDiary.value.comments = currentDiary.value.comments || []
        currentDiary.value.comments.unshift(newComment)
      }
      ElMessage.success('评论成功')
      return newComment
    } catch (err) {
      error.value = err.message
      ElMessage.error('评论失败')
      return null
    }
  }

  // 删除评论
  const deleteComment = async (id) => {
    try {
      await commentApi.deleteComment(id)
      comments.value = comments.value.filter(c => c.id !== id)
      if (currentDiary.value) {
        currentDiary.value.comments = currentDiary.value.comments.filter(c => c.id !== id)
      }
      ElMessage.success('删除评论成功')
    } catch (err) {
      error.value = err.message
      ElMessage.error('删除评论失败')
    }
  }

  return {
    diaries,
    currentDiary,
    comments,
    loading,
    error,
    fetchDiaries,
    fetchPopularDiaries,
    fetchLatestDiaries,
    fetchDiaryById,
    createDiary,
    updateDiary,
    deleteDiary,
    searchDiaries,
    getDiariesByTag,
    toggleLike,
    fetchComments,
    createComment,
    deleteComment
  }
})


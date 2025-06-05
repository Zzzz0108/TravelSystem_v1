import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as diaryApi from '@/api/diaryApi'
import * as commentApi from '@/api/commentApi'
import { ElMessage } from 'element-plus'
import axios from 'axios'

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

  // 获取日记列表（使用后端的热度+评分排序）
  const fetchPopularDiaries = async (params) => {
    try {
      loading.value = true
      const response = await diaryApi.getPopularDiaries(params)
      diaries.value = response.content
    } catch (err) {
      error.value = err.message
      ElMessage.error('获取日记列表失败')
    } finally {
      loading.value = false
    }
  }

  // 获取按评分排序的日记
  const fetchPopularDiariesByScore = async (params) => {
    try {
      loading.value = true
      const response = await diaryApi.getPopularDiaries(params)
      diaries.value = response.content
    } catch (err) {
      error.value = err.message
      ElMessage.error('获取评分日记失败')
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
      console.log('获取到的原始日记数据:', diary)
      
      // 处理视频URL
      if (diary.videoUrl) {
        console.log('找到视频URL:', diary.videoUrl)
        diary.videoUrl = diary.videoUrl.startsWith('http') 
          ? diary.videoUrl 
          : `http://localhost:9090${diary.videoUrl}`
        console.log('处理后的视频URL:', diary.videoUrl)
      }
      
      // 处理图片URLs
      if (diary.images && diary.images.length > 0) {
        console.log('找到图片:', diary.images)
        diary.images = diary.images.map(img => ({
          ...img,
          url: img.url.startsWith('http') ? img.url : `http://localhost:9090${img.url}`
        }))
        console.log('处理后的图片:', diary.images)
      }
      
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
  const searchDiaries = async (keyword, searchMode = 'destination') => {
    try {
      if (!keyword) {
        // 如果没有搜索内容，获取所有日记
        await fetchPopularDiaries();
        return;
      }

      let url = '/api/diaries/search';
      let params = {};
      
      if (searchMode === 'destination') {
        url = '/api/diaries/search/destination';
        params = { destination: keyword };
      } else if (searchMode === 'title') {
        url = '/api/diaries/search';
        params = { title: keyword };
      } else if (searchMode === 'content') {
        url = '/api/diaries/search/content';
        params = { content: keyword };
      }
      
      console.log('Search URL:', url);
      console.log('Search Params:', params);
      
      const response = await axios.get(url, { params });
      console.log('Search Response:', response.data);
      
      if (response.data && response.data.content) {
        diaries.value = response.data.content;
        console.log('Updated diaries:', diaries.value);
      } else {
        console.warn('No content in response');
        diaries.value = [];
      }
    } catch (error) {
      console.error('搜索日记失败:', error);
      ElMessage.error('搜索失败，请稍后重试');
    }
  };

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
      if (response && response.content) {
        // 确保每个评论对象都包含必要的字段
        const formattedComments = response.content.map(comment => ({
          id: comment.id,
          content: comment.content,
          createdAt: comment.createdAt,
          author: {
            id: comment.author?.id,
            username: comment.author?.username,
            avatar: comment.author?.avatar
          }
        }))
        comments.value = formattedComments
        console.log('获取到的评论:', comments.value)
      } else {
        comments.value = []
        console.log('没有评论数据')
      }
    } catch (err) {
      error.value = err.message
      ElMessage.error('获取评论失败')
      console.error('获取评论失败:', err)
      comments.value = []
    } finally {
      loading.value = false
    }
  }

  // 添加评论
  const createComment = async (diaryId, content) => {
    try {
      if (!diaryId || !content) {
        throw new Error('参数错误');
      }

      const response = await commentApi.createComment(diaryId, { content });
      console.log('评论创建响应:', response);
      
      if (!response?.data) {
        throw new Error('服务器响应数据格式错误');
      }

      if (currentDiary.value?.id === diaryId) {
        if (!currentDiary.value.comments) {
          currentDiary.value.comments = [];
        }
        // 确保新评论数据格式正确
        const newComment = {
          id: response.data.id,
          content: response.data.content,
          createdAt: response.data.createdAt,
          author: {
            id: response.data.author?.id,
            username: response.data.author?.username,
            avatar: response.data.author?.avatar
          }
        };
        currentDiary.value.comments = [newComment, ...currentDiary.value.comments];
        currentDiary.value.commentsCount = (currentDiary.value.commentsCount || 0) + 1;
      }
      
      return response.data;
    } catch (err) {
      console.error('创建评论失败:', err);
      error.value = err.message;
      ElMessage.error(err.message || '评论发布失败');
      throw err;
    }
  };

  // 删除评论
  const deleteComment = async (commentId) => {
    try {
      loading.value = true
      await commentApi.deleteComment(commentId)
      if (currentDiary.value) {
        currentDiary.value.comments = currentDiary.value.comments.filter(
          comment => comment.id !== commentId
        )
        currentDiary.value.commentsCount--
      }
      ElMessage.success('评论删除成功')
    } catch (err) {
      error.value = err.message
      ElMessage.error('评论删除失败')
      throw err
    } finally {
      loading.value = false
    }
  }

  // 评分日记
  const rateDiary = async (id, rating) => {
    try {
      loading.value = true
      const updatedDiary = await diaryApi.rateDiary(id, rating)
      if (!updatedDiary) {
        throw new Error('评分失败')
      }
      
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
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取用户对日记的评分
  const getUserRating = async (id) => {
    try {
      loading.value = true
      const rating = await diaryApi.getUserRating(id)
      return rating
    } catch (err) {
      error.value = err.message
      return 0
    } finally {
      loading.value = false
    }
  }

  // 根据目的地搜索日记
  const searchByDestination = async (destination, params) => {
    try {
      loading.value = true
      const response = await diaryApi.searchByDestination(destination, params)
      diaries.value = response.content
    } catch (err) {
      error.value = err.message
      ElMessage.error('搜索失败')
    } finally {
      loading.value = false
    }
  }

  // 精确查询日记标题
  const searchByExactTitle = async (title) => {
    try {
      loading.value = true
      const diary = await diaryApi.searchByExactTitle(title)
      currentDiary.value = diary
      return diary
    } catch (err) {
      error.value = err.message
      ElMessage.error('查询失败')
      return null
    } finally {
      loading.value = false
    }
  }

  // 全文检索
  const fullTextSearch = async (keyword, params) => {
    try {
      loading.value = true
      const response = await diaryApi.fullTextSearch(keyword, params)
      diaries.value = response.content
    } catch (err) {
      error.value = err.message
      ElMessage.error('搜索失败')
    } finally {
      loading.value = false
    }
  }

  // 压缩日记内容
  const compressDiaryContent = async (id) => {
    try {
      loading.value = true
      const compressedContent = await diaryApi.compressDiaryContent(id)
      return compressedContent
    } catch (err) {
      error.value = err.message
      ElMessage.error('压缩失败')
      return null
    } finally {
      loading.value = false
    }
  }

  // 解压日记内容
  const decompressDiaryContent = async (id) => {
    try {
      loading.value = true
      const decompressedContent = await diaryApi.decompressDiaryContent(id)
      return decompressedContent
    } catch (err) {
      error.value = err.message
      ElMessage.error('解压失败')
      return null
    } finally {
      loading.value = false
    }
  }

  // 增加浏览量
  const incrementViews = async (id) => {
    try {
      await diaryApi.incrementViews(id);
      // 更新本地状态
      const index = diaries.value.findIndex(d => d.id === id);
      if (index !== -1) {
        diaries.value[index].views += 1;
      }
      if (currentDiary.value?.id === id) {
        currentDiary.value.views += 1;
      }
    } catch (err) {
      error.value = err.message;
      ElMessage.error('增加浏览量失败');
    }
  };

  return {
    diaries,
    currentDiary,
    comments,
    loading,
    error,
    fetchDiaries,
    fetchPopularDiaries,
    fetchPopularDiariesByScore,
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
    deleteComment,
    rateDiary,
    getUserRating,
    searchByDestination,
    searchByExactTitle,
    fullTextSearch,
    compressDiaryContent,
    decompressDiaryContent,
    incrementViews
  }
})


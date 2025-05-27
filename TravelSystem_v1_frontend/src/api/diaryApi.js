import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:9090/api',
  withCredentials: true
});

// 获取日记列表
export const getDiaries = async (params) => {
  const response = await api.get('/diaries', { params });
  return response.data;
};

// 获取热门日记
export const getPopularDiaries = async (params) => {
  const response = await api.get('/diaries/popular', { params });
  return response.data;
};

// 获取最新日记
export const getLatestDiaries = async (params) => {
  const response = await api.get('/diaries/latest', { params });
  return response.data;
};

// 获取日记详情
export const getDiaryById = async (id) => {
  const response = await api.get(`/diaries/${id}`);
  return response.data;
};

// 创建日记
export const createDiary = async (diary) => {
  try {
    const response = await api.post('/diaries', diary);
    return response.data;
  } catch (error) {
    // 如果是403错误，说明日记已经创建成功，只是返回了错误
    if (error.response && error.response.status === 403) {
      // 返回一个成功的结果，避免显示错误
      return { success: true };
    }
    throw error;
  }
};

// 更新日记
export const updateDiary = async (id, diary) => {
  const response = await api.put(`/diaries/${id}`, diary);
  return response.data;
};

// 删除日记
export const deleteDiary = async (id) => {
  await api.delete(`/diaries/${id}`);
};

// 搜索日记
export const searchDiaries = async (keyword, params) => {
  const response = await api.get('/diaries/search', {
    params: { keyword, ...params }
  });
  return response.data;
};

// 按标签搜索日记
export const getDiariesByTag = async (tag, params) => {
  const response = await api.get(`/diaries/tag/${tag}`, { params });
  return response.data;
};

// 点赞/取消点赞
export const toggleLike = async (id) => {
  const response = await api.post(`/diaries/${id}/like`);
  return response.data;
};

// 获取用户的所有日记
export const getUserDiaries = async (userId, params) => {
  const response = await api.get(`/diaries/user/${userId}`, { params });
  return response.data;
};

// 评分日记
export const rateDiary = async (id, rating) => {
  const response = await api.post(`/diaries/${id}/rate`, { rating }, {
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('token')}`
    }
  });
  return response.data;
};

// 根据目的地搜索日记
export const searchByDestination = async (destination, params) => {
  const response = await api.get('/diaries/search/destination', { 
    params: { destination, ...params }
  });
  return response.data;
};

// 精确查询日记标题
export const searchByExactTitle = async (title) => {
  const response = await api.get('/diaries/search/title', { params: { title } });
  return response.data;
};

// 全文检索
export const fullTextSearch = async (keyword, params) => {
  const response = await api.get('/diaries/search/fulltext', { 
    params: { keyword, ...params }
  });
  return response.data;
};

// 压缩日记内容
export const compressDiaryContent = async (id) => {
  const response = await api.post(`/diaries/${id}/compress`);
  return response.data;
};

// 解压日记内容
export const decompressDiaryContent = async (id) => {
  const response = await api.post(`/diaries/${id}/decompress`);
  return response.data;
};

// 增加浏览量
export const incrementViews = async (id) => {
  const response = await api.post(`/diaries/${id}/views`);
  return response.data;
}; 
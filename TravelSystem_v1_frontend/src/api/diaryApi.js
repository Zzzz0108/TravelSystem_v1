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
  const response = await api.post('/diaries', diary);
  return response.data;
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
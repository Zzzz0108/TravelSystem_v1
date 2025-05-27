import axios from 'axios';
import { useUserStore } from '@/stores/userStore';

const api = axios.create({
  baseURL: '/api',
  withCredentials: true
});

// 添加请求拦截器
api.interceptors.request.use(config => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 添加响应拦截器
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // token 过期或无效，清除用户信息
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// 获取日记的评论
export const getCommentsByDiaryId = async (diaryId, params) => {
  const response = await api.get(`/comments/diary/${diaryId}`, { params });
  return response.data;
};

// 创建评论
export const createComment = async (diaryId, comment) => {
  try {
    const response = await api.post(`/comments/diary/${diaryId}`, comment);
    // 确保返回的数据格式正确
    if (!response.data) {
      throw new Error('服务器响应数据格式错误');
    }
    return {
      data: {
        id: response.data.id,
        content: response.data.content,
        createdAt: response.data.createdAt,
        author: {
          id: response.data.author?.id,
          username: response.data.author?.username,
          avatar: response.data.author?.avatar
        }
      }
    };
  } catch (error) {
    console.error('创建评论失败:', error);
    throw error;
  }
};

// 删除评论
export const deleteComment = async (id) => {
  await api.delete(`/comments/${id}`);
};

// 获取用户的所有评论
export const getCommentsByUserId = async (userId, params) => {
  const response = await api.get(`/comments/user/${userId}`, { params });
  return response.data;
}; 
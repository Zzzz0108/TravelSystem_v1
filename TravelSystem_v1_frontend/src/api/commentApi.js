import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  withCredentials: true
});

// 获取日记的评论
export const getCommentsByDiaryId = async (diaryId, params) => {
  const response = await api.get(`/comments/diary/${diaryId}`, { params });
  return response.data;
};

// 创建评论
export const createComment = async (diaryId, comment) => {
  const response = await api.post(`/comments/diary/${diaryId}`, comment);
  return response.data;
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
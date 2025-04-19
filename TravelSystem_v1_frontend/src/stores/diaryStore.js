import { defineStore } from 'pinia'

export const useDiaryStore = defineStore('diary', {
  state: () => ({
    diaries: [
      // 示例数据
      {
        id: 1,
        title: '云南七日游攻略',
        content: '探索丽江古城与玉龙雪山的绝美风光...',
        images: [
          'https://picsum.photos/600/400?random=1',
          'https://picsum.photos/600/400?random=2'
        ],
        video: null,
        tags: ['自驾游', '摄影'],
        location: '云南',
        likes: 245,
        views: 1234, // 浏览量
        comments: [],
        author: {
          id: 1,
          name: '旅行达人',
          avatar: 'https://randomuser.me/api/portraits/women/1.jpg'
        },
        createdAt: '2024-03-01',
        ratings: [], // 新增评分数组
      }
    ]
  }),
  actions: {
    async fetchDiaryById(id) {
      try {
        // 先尝试从本地数据中获取
        const localDiary = this.diaries.find(d => d.id === Number(id))
        if (localDiary) {
          return localDiary
        }
        
        // 如果本地没有，则从服务器获取
        const response = await fetch(`/api/diaries/${id}`)
        if (!response.ok) throw new Error('日记不存在')
        const diary = await response.json()
        this.diaries.push(diary)
        return diary
      } catch (error) {
        console.error('获取日记失败:', error)
        return null
      }
    },
    addComment(diaryId, comment) {
      const diary = this.diaries.find(d => d.id === Number(diaryId))
      if (diary) {
        diary.comments.push({
          id: Date.now(),
          text: comment.text,
          author: {
            id: 2, // 需要替换为真实用户数据
            name: '当前用户',
            avatar: 'https://randomuser.me/api/portraits/men/1.jpg'
          },
          createdAt: new Date().toISOString()
        })
      }
    },
    addDiary(newDiary) {
      this.diaries.unshift({
        id: Date.now(),
        ...newDiary,
        likes: 0,
        views: 0,  // 新增浏览量初始化
        comments: [],
        createdAt: new Date().toISOString(),
        isLiked: false  // 新增点赞状态
      })
    },
    addRating(id, rating) {
      const diary = this.diaries.find(d => d.id === Number(id))
      if (diary) {
        // 简单实现：记录当前用户评分
        diary.ratings.push({
          userId: localStorage.getItem('userId'),
          rating
        })
      }
    }
  }
})


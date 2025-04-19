<template>
  <nav class="nav-container">
    <div class="nav-content">
      <router-link to="/" class="nav-logo">
        <svg class="logo-icon" viewBox="0 0 24 24">
          <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5S10.62 6.5 12 6.5 14.5 7.62 14.5 9 13.38 11.5 12 11.5z"/>
        </svg>
          <!-- 下拉菜单 -->
          <select class="province-select" @change="handleProvinceChange">
            <option value="/">选择省份</option>
            <option v-for="province in provinces" :key="province.path" :value="province.path">
              {{ province.name }}
            </option>
          </select>
      </router-link>
      
      <div class="nav-links">
        <router-link 
          v-for="link in links"
          :key="link.path"
          :to="link.path"
          class="nav-item"
        >
          {{ link.name }}
          
        </router-link>
        <div class="nav-divider"></div>
        <template v-if="userStore.isLogin">
          
          <router-link to="/visited" class="nav-item">我去过的</router-link>
          <router-link 
            to="/favorites" 
            class="nav-link"
          >
            我的收藏
          </router-link>
          <!-- 头像和用户名 -->
          <div class="user-info">
            <UserAvatar :src="avatarUrl" :name="userStore.username" size="32" />
            <span class="username">{{ userStore.username }}</span>
          </div>
          <!-- 退出登录按钮 -->
          <button class="nav-item" @click="logout">
            退出登录
          </button>
        </template>
        <router-link v-else to="/login" class="nav-item">
          登录/注册
        </router-link>
        
      </div>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import UserAvatar from '@/components/common/UserAvatar.vue';

const userStore = useUserStore()
const router = useRouter()

const links = [
  { name: '首页', path: '/' },
  { name: '景区导航', path: '/navigation' },
  { name: '旅行日记', path: '/diary' }
]

// 省份和景点数据
const provinces = [
  { name: '北京', path: '/navigation/beijing' },
  { name: '上海', path: '/navigation/shanghai' },
  { name: '云南', path: '/navigation/yunnan' },
  { name: '四川', path: '/navigation/sichuan' },
  { name: '浙江', path: '/navigation/zhejiang' }
]

// 处理省份选择
const handleProvinceChange = (event) => {
  const path = event.target.value;
  if (path) {
    router.push({ path, query: { province: event.target.selectedOptions[0].text } });
  }
};


const heartIcon = computed(() => 
  userStore.favorites.length > 0 
    ? 'M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z'
    : 'M16.5 3c-1.74 0-3.41.81-4.5 2.09C10.91 3.81 9.24 3 7.5 3 4.42 3 2 5.42 2 8.5c0 3.78 3.4 6.86 8.55 11.54L12 21.35l1.45-1.32C18.6 15.36 22 12.28 22 8.5 22 5.42 19.58 3 16.5 3zm-4.4 15.55l-.1.1-.1-.1C7.14 14.24 4 11.39 4 8.5 4 6.5 5.5 5 7.5 5c1.54 0 3.04.99 3.57 2.36h1.87C13.46 5.99 14.96 5 16.5 5c2 0 3.5 1.5 3.5 3.5 0 2.89-3.14 5.74-7.9 10.05z'
)
// 头像 URL（根据用户名动态生成）
const avatarUrl = computed(() => {
  return userStore.avatar || `https://avatars.dicebear.com/api/initials/${userStore.username}.svg`;
});

// 退出登录
const logout = () => {
  userStore.logout()
  window.location.reload() // 刷新页面以更新界面
}
</script>

<style lang="scss" scoped>
.nav-container {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
  
  .logo-icon {
    width: 28px;
    height: 28px;
    fill: #0071e3;
  }
  
  .logo-text {
    font-size: 20px;
    font-weight: 600;
    color: #1d1d1f;
    letter-spacing: -0.5px;
  }
  .province-select {
    font-size: 16px;
    font-weight: 600;
    color: #1d1d1f;
    border: none;
    background: none;
    cursor: pointer;
    outline: none;
    appearance: none; // 移除默认样式
    padding: 4px 8px;
    border-radius: 8px;
    transition: background 0.3s ease;

    &:hover {
      background: rgba(0, 113, 227, 0.1);
    }
  }
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 20px;
}

.nav-item {
  padding: 8px 12px;
  text-decoration: none;
  color: #1d1d1f;
  font-size: 14px;
  border-radius: 8px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
  
  &:hover {
    background: rgba(0, 113, 227, 0.1);
    color: #0071e3;
    
    .nav-icon {
      fill: #0071e3;
    }
  }
  
  &.router-link-active {
    color: #0071e3;
    font-weight: 500;
  }
}

.nav-icon {
  width: 18px;
  height: 18px;
  fill: #86868b;
  transition: fill 0.3s ease;
}

.nav-divider {
  width: 1px;
  height: 24px;
  background: rgba(0, 0, 0, 0.1);
  margin: 0 12px;
}

button.nav-item {
  background: none;
  border: none;
  cursor: pointer;
  font-family: inherit;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;

  .user-avatar{
    width: 22.5px;
  }
}

.username {
  font-size: 14px;
  color: #1d1d1f;
}
</style>
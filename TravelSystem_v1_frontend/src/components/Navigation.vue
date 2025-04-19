<template>
  <div class="navigation-container">
    <!-- 全屏动态背景 -->
    <div class="animated-background"></div>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 标题区 -->
      <div class="hero-section">
        <h1 class="gradient-title">探索数字秘境</h1>
        <p class="subtitle">沉浸式景区智能导航系统</p>
      </div>

      <!-- 搜索与筛选 -->
      <div class="control-panel">
        <div class="search-container glassmorphism">
          <input 
            v-model="destination"
            type="text" 
            placeholder="输入目的地（如二校门）"
            class="search-input"
            @keyup.enter="calculateRoute"
          >
          <select v-model="transportMode" class="transport-select">
            <option value="walking">步行</option>
            <option value="bike">自行车</option>
            <option value="scooter">电瓶车</option>
          </select>
          <button class="search-button" @click="calculateRoute">
            开始导航
          </button>
        </div>

        <div class="filter-group glassmorphism">
          <tag-selector 
            v-model="selectedTags"
            :available-tags="['厕所', '餐厅', '图书馆', '麦当劳','诊所','超市','教室','体育场','保卫处','警察局']"
            theme="dark"
          />
        </div>
      </div>

      <!-- 地图与信息面板 -->
      <div class="main-grid">
        <!-- 地图容器 -->
        <div class="map-container glassmorphism">
          <div id="amap-container" class="amap-wrapper"></div>
        </div>

        <!-- 信息面板 -->
        <div class="info-panel glassmorphism">
          <div class="info-header">
            <h3>推荐路线</h3>
            <div class="transport-selector">
              <button 
                v-for="t in transports"
                :key="t.value"
                :class="{ active: selectedTransport === t.value }"
                @click="selectedTransport = t.value"
              >
                <component :is="t.icon" />
                {{ t.label }}
              </button>
            </div>
          </div>

          <div class="route-list">
            <div 
              v-for="(route, index) in filteredRoutes"
              :key="index"
              class="route-card"
              @mouseenter="highlightRoute(route)"
            >
              <div class="route-meta">
                <span class="route-index">0{{ index + 1 }}</span>
                <div class="route-info">
                  <h4>{{ route.name }}</h4>
                  <p class="route-distance">{{ route.distance }}km · {{ route.duration }}分钟</p>
                </div>
              </div>
              <div class="route-stats">
                <div class="stat-item">
                  <walk-icon class="stat-icon" />
                  <span>步行{{ route.steps }}步</span>
                </div>
                <div class="stat-item">
                  <scenery-icon class="stat-icon" />
                  <span>{{ route.poiCount }}个景点</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
// 样式部分需要引入图标组件和地图初始化逻辑
import { ref, onMounted } from 'vue'
import TagSelector from '@/components/common/TagSelector.vue'
import WalkIcon from '@/assets/icon/Walk.vue'
import BikeIcon from '@/assets/icon/Bike.vue'
import ScooterIcon from '@/assets/icon/Scooter.vue'
import AMapLoader from '@amap/amap-jsapi-loader'

const searchQuery = ref('')
const selectedTags = ref([])
const selectedTransport = ref('walking')

const transports = [
  { value: 'walking', label: '步行', icon: WalkIcon },
  { value: 'bike', label: '自行车', icon: BikeIcon },
  { value: 'scooter', label: '电瓶车', icon: ScooterIcon }
]

// 地图实例
let map = null
let geolocation = null
let driving = null

// 模拟定位坐标（示例：清华大学主楼）
const currentPosition = ref([116.326515, 40.000036])
const destination = ref('')
const transportMode = ref('walking')

// 初始化地图
const initMap = async () => {
  await AMapLoader.load({
    key: '2a92b775baf99c5bddcc6640b82ceb34',
    version: '2.0',
    plugins: [
      'AMap.Scale',
      'AMap.ToolBar',
      'AMap.Geolocation',
      'AMap.PlaceSearch',
      'AMap.Driving'
    ]
  })

  // 创建地图实例
  map = new AMap.Map('amap-container', {
    zoom: 17,
    center: currentPosition.value,
    mapStyle: 'amap://styles/darkblue'
  })

  // 添加定位控件
  geolocation = new AMap.Geolocation({
    enableHighAccuracy: true,
    timeout: 10000
  })
  map.addControl(geolocation)

  // 初始化路线规划服务
  driving = new AMap.Driving({
    map: map,
    policy: AMap.DrivingPolicy.LEAST_TIME
  })
}

// 路线规划方法
const calculateRoute = async () => {
  if (!destination.value) return
  
  // 清除旧路线
  driving.clear()
  
  // 设置交通方式策略
  const policyMap = {
    walking: AMap.DrivingPolicy.LEAST_TIME,
    bike: AMap.DrivingPolicy.LEAST_DISTANCE,
    scooter: AMap.DrivingPolicy.REAL_TRAFFIC
  }

  driving.setPolicy(policyMap[transportMode.value])

  // 执行路线规划
  driving.search(
    currentPosition.value,
    destination.value,
    (status, result) => {
      if (status === 'complete') {
        console.log('路线规划成功', result)
      } else {
        console.error('路线规划失败', result)
      }
    }
  )
}

// 模拟定位
const locateUser = () => {
  map.setCenter(currentPosition.value)
  new AMap.Marker({
    position: currentPosition.value,
    map: map,
    content: '<div class="user-marker">📍</div>'
  })
}

onMounted(() => {
  initMap().then(() => {
    locateUser()
    // 添加3D建筑效果
    map.add(new AMap.Buildings({
      zooms: [15, 20],
      heightFactor: 2
    }))
    // 添加实时路况图层
    map.add(new AMap.Traffic({
      autoRefresh: true
    }))
    // 添加热力图
    map.add(new AMap.HeatMap({
      radius: 25,
      opacity: [0, 0.8]
    }))
  })
})
</script>

<style lang="scss" scoped>
.transport-select {
    background: rgba(255,255,255,0.1);
    border: 1px solid rgba(255,255,255,0.3);
    color: white;
    padding: 0.5rem;
    margin: 0 1rem;
    border-radius: 8px;
  }

  .user-marker {
    font-size: 24px;
    filter: drop-shadow(0 0 8px rgba(0,255,135,0.5));
  }
.navigation-container {
  min-height: 100vh;
  background: #0a0a0e;
  color: rgba(255,255,255,0.9);
  position: relative;
  overflow: hidden;
  border-radius: 16px;
}

.animated-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    45deg,
    #3179ff 0%,
    #7ea9ff 50%,
    #f7f7f8 100%
  );
  opacity: 0.8;
  z-index: 0;
  animation: gradientFlow 15s ease infinite;
}

@keyframes gradientFlow {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

.main-content {
  position: relative;
  z-index: 1;
  padding: 4rem 2rem;
  max-width: 1600px;
  margin: 0 auto;
}

.hero-section {
  text-align: center;
  margin-bottom: 4rem;

  .gradient-title {
    font-size: 4rem;
    background: linear-gradient(45deg, #1b17ff, #4fc8f8);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
    margin-bottom: 1rem;
    font-weight: 700;
    letter-spacing: -0.03em;
  }

  .subtitle {
    font-size: 1.25rem;
    color: rgba(255,255,255,0.7);
    letter-spacing: 0.1em;
  }
}

.control-panel {
  display: grid;
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.glassmorphism {
  background: rgba(255,255,255,0.05);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  border: 1px solid rgba(255,255,255,0.1);
}

.search-container {
  display: flex;
  padding: 8px;
  
  .search-input {
    flex: 1;
    background: transparent;
    border: none;
    padding: 1rem;
    color: white;
    font-size: 1.1rem;

    &::placeholder {
      color: rgba(255,255,255,0.4);
    }
  }

  .search-button {
    background: linear-gradient(45deg, #2335ff, #4fc8f8);
    border: none;
    border-radius: 12px;
    padding: 1rem 2rem;
    cursor: pointer;
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-2px);
    }
  }

  .search-icon {
    width: 24px;
    height: 24px;
    fill: #0a0a0e;
  }
}

.main-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
  height: 60vh;
}

.map-container {
  height: 100%;
  overflow: hidden;
  position: relative;

  .amap-wrapper {
    height: 100%;
    border-radius: 16px;
    overflow: hidden;
  }
}

.info-panel {
  padding: 2rem;
  display: flex;
  flex-direction: column;

  .info-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;

    h3 {
      font-size: 1.5rem;
      margin: 0;
    }
  }
}

.transport-selector {
  display: flex;
  gap: 1rem;

  button {
    background: rgba(255,255,255,0.1);
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 8px;
    color: rgba(255,255,255,0.7);
    cursor: pointer;
    transition: all 0.3s ease;

    &.active {
      background: linear-gradient(45deg, #2335ff, #4fc8f8);
      color: #0a0a0e;
      box-shadow: 0 4px 12px rgba(0,255,135,0.2);
    }

    &:hover {
      transform: translateY(-2px);
    }
  }
}

.route-list {
  flex: 1;
  overflow-y: auto;
}

.route-card {
  background: rgba(255,255,255,0.03);
  padding: 1.5rem;
  border-radius: 12px;
  margin-bottom: 1rem;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    background: rgba(255,255,255,0.06);
    transform: translateX(8px);
  }

  .route-meta {
    display: flex;
    align-items: center;
    gap: 1rem;
    margin-bottom: 1rem;

    .route-index {
      font-size: 2rem;
      font-weight: 700;
      background: linear-gradient(45deg, #2335ff, #4fc8f8);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }
  }

  .route-stats {
    display: flex;
    gap: 1.5rem;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 0.5rem;
      color: rgba(255,255,255,0.7);

      .stat-icon {
        width: 20px;
        height: 20px;
        fill: currentColor;
      }
    }
  }
}
/* 激活状态增强 */
.active .walk-icon {
  filter: drop-shadow(0 0 6px rgba(0, 255, 135, 0.4));
}

.active .bike-icon {
  filter: drop-shadow(0 0 6px rgba(96, 239, 255, 0.4));
}

.active .scooter-icon {
  filter: drop-shadow(0 0 6px rgba(255, 179, 0, 0.4));
}
</style>
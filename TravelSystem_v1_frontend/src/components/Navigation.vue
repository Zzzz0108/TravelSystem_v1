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
          <div class="search-input-group">
            <input 
              v-model="searchQuery"
              type="text" 
              placeholder="输入目的地（如二校门）"
              class="search-input"
              @keyup.enter="addDestination"
            >
            <button class="add-button" @click="addDestination">
              <span>+</span>
            </button>
          </div>
          
          <!-- 已选择的目的地列表 -->
          <div class="selected-destinations" v-if="selectedDestinations.length > 0">
            <div 
              v-for="(dest, index) in selectedDestinations" 
              :key="index"
              class="destination-tag"
            >
              <span>{{ dest }}</span>
              <button class="remove-button" @click="removeDestination(index)">×</button>
            </div>
          </div>

          <select v-model="transportMode" class="transport-select">
            <option value="walking">步行</option>
            <option value="bike">自行车</option>
            <option value="scooter">电瓶车</option>
          </select>
          <button 
            class="search-button" 
            @click="calculateRoute"
            :disabled="selectedDestinations.length === 0"
          >
            开始导航
          </button>
          <button 
            class="clear-button" 
            @click="clearRoute"
            :disabled="routes.length === 0"
          >
            清空路线
          </button>
        </div>

        <div class="filter-group glassmorphism">
          <tag-selector 
            v-model="selectedTags"
            :available-tags="['学习场所', '餐厅', '商店', '厕所', '咖啡馆', '运动场所', '医药', '银行', '快递站', '打印店']"
            theme="dark"
          />
        </div>
      </div>

      <!-- 地图与信息面板 -->
      <div class="main-grid">
        <!-- 地图容器 -->
        <div class="map-container glassmorphism">
          <Map ref="mapComponent" />
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
                  <span>{{ route.poiCount }}个目的地</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 设施选择面板 -->
          <div v-if="filteredFacilities.length > 0" class="facilities-panel">
            <h3>附近设施</h3>
            <div class="facilities-list">
              <div 
                v-for="facility in filteredFacilities" 
                :key="facility.id"
                class="facility-item"
                :class="{ 'selected': selectedFacility?.id === facility.id }"
                @click="selectFacility(facility)"
              >
                <span class="facility-icon">{{ facility.icon || '📍' }}</span>
                <div class="facility-info">
                  <h4>{{ facility.name }}</h4>
                  <p>{{ facility.distance }}米</p>
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
import { ref, onMounted, computed, watch } from 'vue'
import TagSelector from '@/components/common/TagSelector.vue'
import WalkIcon from '@/assets/icon/Walk.vue'
import BikeIcon from '@/assets/icon/Bike.vue'
import ScooterIcon from '@/assets/icon/Scooter.vue'
import Map from '@/components/navigation/Map.vue'

const searchQuery = ref('')
const selectedDestinations = ref([])
const selectedTags = ref([])
const selectedTransport = ref('walking')
const transportMode = ref('walking')
const mapComponent = ref(null)
const currentRoute = ref(null)

// 添加新的响应式变量
const filteredFacilities = ref([])
const selectedFacility = ref(null)

// 添加设施类型映射
const facilityTypeMap = {
  '学习场所': 'LIBRARY',
  '餐厅': 'CANTEEN',
  '商店': 'STORE',
  '厕所': 'TOILET',
  '咖啡馆': 'CAFE',
  '运动场所': 'STADIUM',
  '医药': 'CLINIC',
  '银行': 'BANK',
  '快递站': 'EXPRESS',
  '打印店': 'PRINT'
}

// 添加路线数据
const routes = ref([])

// 计算过滤后的路线
const filteredRoutes = computed(() => {
  return routes.value
})

// 添加高亮路线方法
const highlightRoute = (route) => {
  console.log('高亮路线:', route);
  if (mapComponent.value && typeof mapComponent.value.highlightRoute === 'function') {
    mapComponent.value.highlightRoute(route);
  } else {
    console.warn('mapComponent 或 highlightRoute 方法未定义');
  }
};

// 更新路线信息
const updateRouteInfo = (routeData) => {
  if (!routeData) return
  
  // 计算步行步数（假设每步0.6米）
  const steps = Math.round(routeData.distance / 0.6)
  
  routes.value = [{
    name: '最短路线',
    distance: (routeData.distance / 1000).toFixed(1), // 转换为千米
    duration: Math.round(routeData.time / 60), // 使用后端返回的时间（秒转分钟）
    steps: steps,
    poiCount: routeData.poiCount || 0,
    path: routeData.path
  }]
}

// 监听标签选择变化
watch(selectedTags, async (newTags, oldTags) => {
  if (newTags.length > 0) {
    // 获取最后一个选中的标签对应的设施类型
    const lastTag = newTags[newTags.length - 1]
    const selectedType = facilityTypeMap[lastTag]
    
    if (selectedType && mapComponent.value) {
      try {
        const facilities = await mapComponent.value.filterFacilities(selectedType)
        filteredFacilities.value = facilities
      } catch (error) {
        console.error('获取设施失败:', error)
      }
    }
  } else {
    filteredFacilities.value = []
    selectedFacility.value = null
  }
}, { deep: true })

const transports = [
  { value: 'walking', label: '步行', icon: WalkIcon },
  { value: 'bike', label: '自行车', icon: BikeIcon },
  { value: 'scooter', label: '电瓶车', icon: ScooterIcon }
]

// 模拟定位坐标（书店位置）
const currentPosition = ref([116.3151, 39.9629])

// 添加目的地
const addDestination = () => {
  if (searchQuery.value.trim()) {
    selectedDestinations.value.push(searchQuery.value.trim())
    searchQuery.value = ''
  }
}

// 移除目的地
const removeDestination = (index) => {
  selectedDestinations.value.splice(index, 1)
}

// 路线规划方法
const calculateRoute = async () => {
  if (selectedDestinations.value.length === 0) return
  
  try {
    // 调用地图组件的多目标路线规划方法
    const routeData = await mapComponent.value.calculateMultiDestinationRoute(
      selectedDestinations.value,
      transportMode.value
    )

    // 更新路线信息
    updateRouteInfo(routeData)
  } catch (error) {
    console.error('路线规划失败:', error)
  }
}

// 清空路线方法
const clearRoute = () => {
  if (mapComponent.value) {
    // 清除地图上的路线
    if (mapComponent.value.currentRoute) {
      mapComponent.value.currentRoute.setMap(null)
      mapComponent.value.currentRoute = null
    }
    // 清空路线信息
    routes.value = []
    // 清空已选择的目的地
    selectedDestinations.value = []
  }
}

// 选择设施的方法
const selectFacility = async (facility) => {
  selectedFacility.value = facility
  // 将选中的设施添加到目的地列表
  selectedDestinations.value.push(facility.name)
  // 清空设施列表
  filteredFacilities.value = []
  selectedTags.value = []
}

onMounted(() => {
  // 地图初始化由Map组件内部处理
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
  //适配渐变色
  background-image: linear-gradient(
    90deg,
    #1760ff 0%,
    #598dff 50%,
    #f7f7f8 100%
  );
  //background: #1b17ff ;
  color: rgba(255,255,255,0.9);
  position: relative;
  overflow: scroll;
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

.search-input-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
}

.add-button {
  background: rgba(255,255,255,0.1);
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;

  &:hover {
    background: rgba(255,255,255,0.2);
    transform: scale(1.1);
  }
}

.selected-destinations {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin: 0.5rem 0;
  padding: 0.5rem;
  background: rgba(255,255,255,0.05);
  border-radius: 8px;
}

.destination-tag {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: rgba(255,255,255,0.1);
  padding: 0.25rem 0.75rem;
  border-radius: 16px;
  font-size: 0.9rem;

  .remove-button {
    background: none;
    border: none;
    color: rgba(255,255,255,0.7);
    cursor: pointer;
    font-size: 1.2rem;
    padding: 0;
    line-height: 1;

    &:hover {
      color: white;
    }
  }
}

.search-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.clear-button {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-left: 1rem;

  &:hover {
    background: rgba(255, 255, 255, 0.2);
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
}

.facilities-panel {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.facilities-panel h3 {
  margin: 0 0 16px 0;
  color: #1d1d1f;
  font-size: 18px;
}

.facilities-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 300px;
  overflow-y: auto;
}

.facility-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s ease;
}

.facility-item:hover {
  background: rgba(0, 113, 227, 0.1);
  transform: translateX(4px);
}

.facility-item.selected {
  background: rgba(0, 113, 227, 0.2);
  border: 1px solid #0071e3;
}

.facility-icon {
  font-size: 24px;
}

.facility-info {
  flex: 1;
}

.facility-info h4 {
  margin: 0;
  font-size: 16px;
  color: #1d1d1f;
}

.facility-info p {
  margin: 4px 0 0 0;
  font-size: 14px;
  color: #86868b;
}
</style>
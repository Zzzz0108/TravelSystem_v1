<template>
  <div class="navigation-container">
    <!-- 全屏动态背景 -->
    <div class="animated-background"></div>

    <!-- 主内容区 -->
    <main class="main-content">
      <!-- 标题区 -->
      <div class="hero-section">
        <h1 class="gradient-title">校园智能导航</h1>
        <p class="subtitle">探索校园的每一个角落</p>
      </div>

      <!-- 搜索与筛选 -->
      <div class="control-panel">
        <div class="search-container glassmorphism">
          <input 
            v-model="searchQuery"
            type="text" 
            placeholder="搜索目的地（如第一教学楼、图书馆等）"
            class="search-input"
            @input="handleSearch"
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
            :available-tags="facilityTypes"
            theme="dark"
          />
        </div>
      </div>

      <!-- 地图与信息面板 -->
      <div class="main-grid">
        <!-- 地图容器 -->
        <div class="map-container glassmorphism">
          <canvas 
            ref="mapCanvas" 
            class="campus-map"
            @mousedown="startDrag"
            @mousemove="onDrag"
            @mouseup="endDrag"
            @mouseleave="endDrag"
            @wheel.prevent="handleZoom"
          ></canvas>
          <div class="map-controls">
            <button @click="zoomIn" class="control-button">+</button>
            <button @click="zoomOut" class="control-button">-</button>
            <button @click="resetView" class="control-button">↺</button>
          </div>
        </div>

        <!-- 信息面板 -->
        <div class="info-panel glassmorphism">
          <div class="info-header">
            <h3>导航信息</h3>
            <div class="route-options">
              <label>
                <input type="radio" v-model="routeStrategy" value="distance"> 最短距离
              </label>
              <label>
                <input type="radio" v-model="routeStrategy" value="time"> 最短时间
              </label>
            </div>
          </div>

          <div class="route-details" v-if="currentRoute">
            <div class="route-info">
              <p>距离：{{ currentRoute.distance }}米</p>
              <p>预计时间：{{ currentRoute.duration }}分钟</p>
              <p>途经：{{ currentRoute.points.join(' → ') }}</p>
            </div>
          </div>

          <div class="nearby-facilities" v-if="selectedBuilding">
            <h4>附近设施</h4>
            <div class="facility-list">
              <div 
                v-for="facility in nearbyFacilities" 
                :key="facility.id"
                class="facility-item"
                @click="selectFacility(facility)"
              >
                <span class="facility-name">{{ facility.name }}</span>
                <span class="facility-distance">{{ facility.distance }}米</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import TagSelector from '@/components/common/TagSelector.vue'
import axios from 'axios'

// 状态管理
const searchQuery = ref('')
const selectedTags = ref([])
const transportMode = ref('walking')
const routeStrategy = ref('distance')
const currentRoute = ref(null)
const selectedBuilding = ref(null)
const nearbyFacilities = ref([])
const buildings = ref([])
const facilities = ref([])
const roads = ref([])
const roadPathPoints = ref([])

// 地图状态
const mapCanvas = ref(null)
const ctx = ref(null)
const scale = ref(0.1)
const offsetX = ref(0)
const offsetY = ref(0)
const isDragging = ref(false)
const lastX = ref(0)
const lastY = ref(0)

// 设施类型
const facilityTypes = [
  '图书馆', '食堂', '超市', '卫生间', '咖啡厅',
  '体育场', '诊所', '银行', '快递点', '打印店'
]

// 初始化地图
const initMap = async () => {
  const canvas = mapCanvas.value
  if (!canvas) {
    console.error('Canvas element not found')
    return
  }
  
  // 设置Canvas尺寸为显示尺寸的2倍
  const dpr = window.devicePixelRatio || 1
  const displayWidth = canvas.offsetWidth
  const displayHeight = canvas.offsetHeight
  
  canvas.width = displayWidth * dpr
  canvas.height = displayHeight * dpr
  
  // 设置Canvas的CSS尺寸为显示尺寸
  canvas.style.width = `${displayWidth}px`
  canvas.style.height = `${displayHeight}px`
  
  ctx.value = canvas.getContext('2d')
  if (!ctx.value) {
    console.error('Failed to get canvas context')
    return
  }
  
  // 根据设备像素比缩放上下文
  ctx.value.scale(dpr, dpr)
  
  // 加载校园数据
  await loadCampusData()
  
  // 计算地图中心点
  calculateMapCenter()
  
  // 绘制初始地图
  drawMap()
}

// 计算地图中心点
const calculateMapCenter = () => {
  if (buildings.value.length === 0 && facilities.value.length === 0) return
  
  let minLat = Number.MAX_VALUE
  let maxLat = Number.MIN_VALUE
  let minLng = Number.MAX_VALUE
  let maxLng = Number.MIN_VALUE
  
  // 计算所有建筑物和设施的经纬度范围
  const allPoints = [...buildings.value, ...facilities.value]
  allPoints.forEach(point => {
    minLat = Math.min(minLat, point.latitude)
    maxLat = Math.max(maxLat, point.latitude)
    minLng = Math.min(minLng, point.longitude)
    maxLng = Math.max(maxLng, point.longitude)
  })
  
  // 计算中心点
  const centerLat = (minLat + maxLat) / 2
  const centerLng = (minLng + maxLng) / 2
  
  // 计算合适的缩放比例
  const latRange = maxLat - minLat
  const lngRange = maxLng - minLng
  const maxRange = Math.max(latRange, lngRange)
  
  // 调整缩放比例，使地图内容刚好填充画布
  const canvas = mapCanvas.value
  const scaleFactor = Math.min(
    canvas.width / (lngRange * 100000),
    canvas.height / (latRange * 100000)
  ) * 0.8 // 留出一些边距
  
  scale.value = scaleFactor
  
  // 设置偏移量，使地图居中
  offsetX.value = canvas.width / 2 - centerLng * 100000 * scale.value
  offsetY.value = canvas.height / 2 - centerLat * 100000 * scale.value
  
  console.log('Map center:', { centerLat, centerLng, scale: scale.value })
}

// 加载校园数据
const loadCampusData = async () => {
  try {
    console.log('开始加载校园数据...');
    
    // 获取所有道路
    console.log('正在请求道路数据...');
    const roadsResponse = await axios.get('http://localhost:9090/api/map/roads');
    console.log('道路数据响应:', roadsResponse);
    roads.value = roadsResponse.data;
    console.log('道路数据加载完成，数量:', roads.value.length);
    console.log('道路数据示例:', roads.value.slice(0, 2));
    
    // 获取所有路径点
    console.log('正在请求路径点数据...');
    const roadPathPointsResponse = await axios.get('http://localhost:9090/api/map/road-path-points');
    console.log('路径点数据响应:', roadPathPointsResponse);
    roadPathPoints.value = roadPathPointsResponse.data;
    console.log('路径点数据加载完成，数量:', roadPathPoints.value.length);
    console.log('路径点数据示例:', roadPathPoints.value.slice(0, 2));
    
    // 获取所有建筑物
    console.log('正在请求建筑物数据...');
    const buildingsResponse = await axios.get('http://localhost:9090/api/map/buildings');
    console.log('建筑物数据响应:', buildingsResponse);
    buildings.value = buildingsResponse.data;
    console.log('建筑物数据加载完成，数量:', buildings.value.length);
    console.log('建筑物数据示例:', buildings.value.slice(0, 2));
    
    // 获取所有设施
    console.log('正在请求设施数据...');
    const facilitiesResponse = await axios.get('http://localhost:9090/api/map/facilities');
    console.log('设施数据响应:', facilitiesResponse);
    facilities.value = facilitiesResponse.data;
    console.log('设施数据加载完成，数量:', facilities.value.length);
    console.log('设施数据示例:', facilities.value.slice(0, 2));
    
    console.log('所有数据加载完成，准备绘制地图...');
    // 加载完数据后立即绘制地图
    drawMap();
  } catch (error) {
    console.error('加载校园数据失败:', error);
    console.error('错误详情:', {
      message: error.message,
      response: error.response,
      status: error.response?.status,
      data: error.response?.data
    });
  }
}

// 绘制地图
const drawMap = () => {
  if (!mapCanvas.value || !ctx.value) {
    console.error('Canvas or context not available')
    return
  }
  
  const canvas = mapCanvas.value
  const context = ctx.value
  
  // 清空画布
  context.clearRect(0, 0, canvas.width, canvas.height)
  
  // 设置背景
  context.fillStyle = '#1a1a2e'
  context.fillRect(0, 0, canvas.width, canvas.height)
  
  // 绘制道路
  drawRoads(context, canvas)
  
  // 绘制建筑物
  drawBuildings(context, canvas)
  
  // 绘制设施
  drawFacilities(context, canvas)
  
  // 绘制当前路线
  if (currentRoute.value) {
    drawCurrentRoute(context, canvas)
  }
}

// 绘制道路
const drawRoads = (context, canvas) => {
  console.log('开始绘制道路，道路数量:', roads.value.length);
  console.log('路径点数量:', roadPathPoints.value.length);
  
  context.strokeStyle = '#4a4a6a'
  context.lineWidth = Math.max(1, 1.5 * scale.value)
  
  // 使用地图中心点作为参考
  const centerLng = 116.31434999999999
  const centerLat = 39.9631
  const scaleFactor = 10000 // 调整这个值来改变缩放比例
  
  roads.value.forEach(road => {
    const points = roadPathPoints.value.filter(p => p.roadId === road.id)
    console.log(`道路 ${road.id} 包含 ${points.length} 个路径点`);
    
    if (points.length < 2) {
      console.log(`道路 ${road.id} 路径点不足，跳过绘制`);
      return;
    }
    
    // 按sequence_number排序
    points.sort((a, b) => a.sequenceNumber - b.sequenceNumber)
    
    context.beginPath()
    points.forEach((point, index) => {
      // 使用相对于中心点的坐标
      const x = (point.longitude - centerLng) * scaleFactor * scale.value + canvas.width/2 + offsetX.value
      const y = (point.latitude - centerLat) * scaleFactor * scale.value + canvas.height/2 + offsetY.value
      
      console.log(`绘制点 ${index}: 原始坐标(${point.longitude}, ${point.latitude}) -> 转换后(${x}, ${y})`);
      
      if (index === 0) {
        context.moveTo(x, y)
      } else {
        context.lineTo(x, y)
      }
    })
    context.stroke()
  })
}

// 绘制建筑物
const drawBuildings = (context, canvas) => {
  // 使用地图中心点作为参考
  const centerLng = 116.31434999999999
  const centerLat = 39.9631
  const scaleFactor = 10000 // 调整这个值来改变缩放比例
  
  buildings.value.forEach(building => {
    // 使用相对于中心点的坐标
    const x = (building.longitude - centerLng) * scaleFactor * scale.value + canvas.width/2 + offsetX.value
    const y = (building.latitude - centerLat) * scaleFactor * scale.value + canvas.height/2 + offsetY.value
    
    // 根据建筑物类型设置不同的样式
    switch(building.type) {
      case 'TEACHING':
        context.fillStyle = '#4a90e2'  // 蓝色
        break
      case 'ADMIN':
        context.fillStyle = '#e24a4a'  // 红色
        break
      case 'DORMITORY':
        context.fillStyle = '#4ae24a'  // 绿色
        break
      default:
        context.fillStyle = '#2a2a3a'
    }
    
    // 绘制建筑物图标
    const radius = Math.max(2, 4 * scale.value)
    context.beginPath()
    context.arc(x, y, radius, 0, Math.PI * 2)
    context.fill()
    context.strokeStyle = '#ffffff'
    context.lineWidth = Math.max(0.5, scale.value)
    context.stroke()
    
    // 只在缩放级别足够大时绘制文字
    if (scale.value > 0.1) {
      // 绘制建筑物名称
      context.fillStyle = '#ffffff'
      context.font = `bold ${Math.max(8, 10 * scale.value)}px Arial`
      context.textAlign = 'center'
      context.textBaseline = 'middle'
      context.fillText(building.name, x, y - 12 * scale.value)
    }
  })
}

// 绘制设施
const drawFacilities = (context, canvas) => {
  facilities.value.forEach(facility => {
    const x = facility.longitude * 100000 * scale.value + offsetX.value
    const y = facility.latitude * 100000 * scale.value + offsetY.value
    
    // 只在缩放级别足够大时绘制文字
    if (scale.value > 0.1) {
      // 绘制设施图标
      context.font = `bold ${Math.max(8, 12 * scale.value)}px Arial`
      context.textAlign = 'center'
      context.textBaseline = 'middle'
      context.fillText(facility.icon || '📍', x, y)
      
      // 绘制设施名称
      context.font = `${Math.max(6, 8 * scale.value)}px Arial`
      context.fillText(facility.name, x, y - 12 * scale.value)
    } else {
      // 在缩放级别较小时只绘制小圆点
      context.beginPath()
      context.arc(x, y, Math.max(1, 2 * scale.value), 0, Math.PI * 2)
      context.fillStyle = '#ffd700'
      context.fill()
    }
  })
}

// 绘制当前路线
const drawCurrentRoute = (context, canvas) => {
  if (!currentRoute.value) return
  
  context.strokeStyle = '#00ff87'
  context.lineWidth = 3
  
  context.beginPath()
  currentRoute.value.path.forEach((point, index) => {
    const x = (point[0] - 116.3) * 100000 * scale.value + canvas.width/2 + offsetX.value
    const y = (point[1] - 39.9) * 100000 * scale.value + canvas.height/2 + offsetY.value
    
    if (index === 0) {
      context.moveTo(x, y)
    } else {
      context.lineTo(x, y)
    }
  })
  context.stroke()
}

// 地图交互
const startDrag = (e) => {
  isDragging.value = true
  lastX.value = e.clientX
  lastY.value = e.clientY
}

const onDrag = (e) => {
  if (!isDragging.value) return
  
  const dx = e.clientX - lastX.value
  const dy = e.clientY - lastY.value
  
  offsetX.value += dx
  offsetY.value += dy
  
  lastX.value = e.clientX
  lastY.value = e.clientY
  
  drawMap()
}

const endDrag = () => {
  isDragging.value = false
}

const handleZoom = (e) => {
  const canvas = mapCanvas.value
  const rect = canvas.getBoundingClientRect()
  const mouseX = e.clientX - rect.left
  const mouseY = e.clientY - rect.top
  
  // 计算鼠标位置对应的地图坐标
  const mapX = (mouseX - offsetX.value) / scale.value
  const mapY = (mouseY - offsetY.value) / scale.value
  
  // 计算缩放因子
  const delta = e.deltaY > 0 ? 0.9 : 1.1
  const newScale = scale.value * delta
  
  // 限制缩放范围
  if (newScale < 0.05 || newScale > 2) return
  
  scale.value = newScale
  
  // 调整偏移量，使鼠标位置保持不变
  offsetX.value = mouseX - mapX * scale.value
  offsetY.value = mouseY - mapY * scale.value
  
  drawMap()
}

const zoomIn = () => {
  const canvas = mapCanvas.value
  const centerX = canvas.width / 2
  const centerY = canvas.height / 2
  
  // 计算中心点对应的地图坐标
  const mapX = (centerX - offsetX.value) / scale.value
  const mapY = (centerY - offsetY.value) / scale.value
  
  // 计算新的缩放比例
  const newScale = scale.value * 1.2
  
  // 限制缩放范围
  if (newScale > 2) return
  
  scale.value = newScale
  
  // 调整偏移量，使中心点保持不变
  offsetX.value = centerX - mapX * scale.value
  offsetY.value = centerY - mapY * scale.value
  
  drawMap()
}

const zoomOut = () => {
  const canvas = mapCanvas.value
  const centerX = canvas.width / 2
  const centerY = canvas.height / 2
  
  // 计算中心点对应的地图坐标
  const mapX = (centerX - offsetX.value) / scale.value
  const mapY = (centerY - offsetY.value) / scale.value
  
  // 计算新的缩放比例
  const newScale = scale.value / 1.2
  
  // 限制缩放范围
  if (newScale < 0.05) return
  
  scale.value = newScale
  
  // 调整偏移量，使中心点保持不变
  offsetX.value = centerX - mapX * scale.value
  offsetY.value = centerY - mapY * scale.value
  
  drawMap()
}

const resetView = () => {
  // 重新计算地图中心点和缩放比例
  calculateMapCenter()
  drawMap()
}

// 计算路线
const calculateRoute = async () => {
  if (!selectedBuilding.value) return

  try {
    const response = await axios.post('/api/route', {
      start: currentPosition.value,
      end: selectedBuilding.value,
      transport: transportMode.value,
      strategy: routeStrategy.value
    })

    currentRoute.value = response.data
    drawMap()
  } catch (error) {
    console.error('路线计算失败:', error)
  }
}

// 查找附近设施
const findNearbyFacilities = async (building) => {
  try {
    const response = await axios.get(`/api/facilities/nearby`, {
      params: {
        buildingId: building.id,
        types: selectedTags.value
      }
    })
    nearbyFacilities.value = response.data
  } catch (error) {
    console.error('查找附近设施失败:', error)
  }
}

// 监听数据变化
watch([buildings, facilities, roads, roadPathPoints], () => {
  drawMap()
})

onMounted(() => {
  initMap()
})
</script>

<style lang="scss" scoped>
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
  background: #1a1a2e;

  .campus-map {
    width: 100%;
    height: 100%;
    border-radius: 16px;
    cursor: grab;
    background: #1a1a2e;
    image-rendering: -webkit-optimize-contrast;
    image-rendering: crisp-edges;
    
    &:active {
      cursor: grabbing;
    }
  }
}

.map-controls {
  position: absolute;
  bottom: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  
  .control-button {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    background: rgba(255,255,255,0.1);
    border: 1px solid rgba(255,255,255,0.2);
    color: white;
    font-size: 20px;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(255,255,255,0.2);
    }
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

.route-options {
  display: flex;
  gap: 1rem;
}

.route-details {
  margin-bottom: 2rem;
}

.route-info {
  margin-bottom: 1rem;
}

.nearby-facilities {
  margin-top: 2rem;
}

.facility-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.facility-item {
  background: rgba(255,255,255,0.03);
  padding: 0.5rem 1rem;
  border-radius: 8px;
  cursor: pointer;

  &:hover {
    background: rgba(255,255,255,0.06);
  }

  .facility-name {
    margin-right: 0.5rem;
  }

  .facility-distance {
    color: rgba(255,255,255,0.7);
  }
}
</style>
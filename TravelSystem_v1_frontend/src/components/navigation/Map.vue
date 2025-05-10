<template>
  <div id="map-container" style="width: 100%; height: 500px; padding:0px ; margin: 0px;"></div>
</template>

<script setup>
import { onMounted, ref, onUnmounted, defineExpose } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { getBuildings, getFacilities, getRoadConnections, getRoadPathPoints } from '@/api/map'

const map = ref(null)
const markers = ref([])
const polylines = ref([])
const buildings = ref([])
const facilities = ref([])
const roadConnections = ref([])
const loading = ref(false)
const driving = ref(null)

// 添加调试数据到全局
window._mapDebug = {
  map: map,
  markers: markers,
  polylines: polylines,
  buildings: buildings,
  facilities: facilities,
  roadConnections: roadConnections
}

// 获取所有数据
const fetchAllData = async () => {
  try {
    loading.value = true
    console.log('开始获取数据...')
    
    const [buildingsRes, facilitiesRes, roadConnectionsRes] = await Promise.all([
      getBuildings(),
      getFacilities(),
      getRoadConnections()
    ])
    
    console.log('获取到的建筑数据:', buildingsRes.data)
    console.log('获取到的设施数据:', facilitiesRes.data)
    console.log('获取到的路径数据:', roadConnectionsRes.data)
    
    buildings.value = buildingsRes.data
    facilities.value = facilitiesRes.data
    roadConnections.value = roadConnectionsRes.data

    // 获取每个路径的路径点
    console.log('开始获取路径点数据...')
    const pathPointsPromises = roadConnections.value.map(async (road) => {
      try {
        console.log(`正在获取路径 ${road.id} 的路径点...`)
        const pathPointsRes = await getRoadPathPoints(road.id)
        console.log(`路径 ${road.id} 的路径点数据:`, pathPointsRes.data)
        if (pathPointsRes.data && Array.isArray(pathPointsRes.data)) {
          road.path_points = pathPointsRes.data
          console.log(`路径 ${road.id} 已添加 ${pathPointsRes.data.length} 个路径点`)
        } else {
          console.error(`路径 ${road.id} 的路径点数据格式错误:`, pathPointsRes.data)
        }
      } catch (error) {
        console.error(`获取路径 ${road.id} 的路径点失败:`, error)
      }
    })
    await Promise.all(pathPointsPromises)
    console.log('所有路径点数据获取完成')

    // 更新地图
    if (map.value) {
      console.log('开始创建标记和路径...')
      createMarkers(window.AMap)
      createPolylines(window.AMap)
      console.log('标记和路径创建完成')
    }
  } catch (error) {
    console.error('获取数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 创建标记点
const createMarkers = (AMap) => {
  // 清除现有标记
  markers.value.forEach(marker => marker.setMap(null))
  markers.value = []

  console.log('开始创建建筑标记，建筑数量:', buildings.value.length)
  // 添加建筑标记
  buildings.value.forEach(building => {
    try {
      console.log('创建建筑标记:', building)
      if (!building.longitude || !building.latitude) {
        console.error('建筑坐标缺失:', building)
        return
      }
      const marker = new AMap.Marker({
        position: [building.longitude, building.latitude],
        title: building.name,
        content: `<div style="font-size: 24px;">${building.type === 'TEACHING' ? '🏫' : 
          building.type === 'ADMIN' ? '🏢' : 
          building.type === 'DORMITORY' ? '🏠' : '🏛️'}</div>`,
        offset: new AMap.Pixel(-12, -12)
      })
      marker.setMap(map.value)
      markers.value.push(marker)
    } catch (error) {
      console.error('创建建筑标记失败:', error, building)
    }
  })

  console.log('开始创建设施标记，设施数量:', facilities.value.length)
  // 添加设施标记
  facilities.value.forEach(facility => {
    try {
      console.log('创建设施标记:', facility)
      if (!facility.longitude || !facility.latitude) {
        console.error('设施坐标缺失:', facility)
        return
      }
      const marker = new AMap.Marker({
        position: [facility.longitude, facility.latitude],
        title: facility.name,
        content: `<div style="font-size: 24px;">${facility.icon || '📍'}</div>`,
        offset: new AMap.Pixel(-12, -12)
      })
      marker.setMap(map.value)
      markers.value.push(marker)
    } catch (error) {
      console.error('创建设施标记失败:', error, facility)
    }
  })
}

// 创建路径
const createPolylines = (AMap) => {
  // 清除现有路径
  polylines.value.forEach(polyline => polyline.setMap(null))
  polylines.value = []

  console.log('开始创建路径，路径数量:', roadConnections.value.length)
  // 添加路径
  roadConnections.value.forEach(road => {
    try {
      console.log(`处理路径 ${road.id}:`, road)
      if (road.path_points && road.path_points.length > 0) {
        const path = road.path_points.map(point => {
          if (!point.longitude || !point.latitude) {
            console.error('路径点坐标缺失:', point)
            return null
          }
          const coord = [point.longitude, point.latitude]
          console.log(`路径点坐标: [${coord[0]}, ${coord[1]}]`)
          return coord
        }).filter(point => point !== null)

        if (path.length > 0) {
          console.log(`创建路径 ${road.id} 的路径点:`, JSON.stringify(path))
          const polyline = new AMap.Polyline({
            path: path,
            strokeColor: '#efefef',
            strokeWeight: 2, // 增加线条宽度
            strokeOpacity: 0.5, // 稍微透明
            showDir: true, // 显示方向
            lineJoin: 'round', // 折线拐点连接处样式
            lineCap: 'round', // 折线两端线帽的绘制样式
            isOutline: true, // 是否显示描边
            outlineColor: '#040202', // 描边颜色
            borderWeight: 0.5, // 描边宽度
            zIndex: 100, // 确保路径在最上层
            bubble: true, // 鼠标事件冒泡
            cursor: 'pointer' // 鼠标悬停时显示手型
          })

          // 添加路径点击事件
          polyline.on('click', (e) => {
            console.log('点击路径:', road.id, e)
            // 高亮显示被点击的路径
            polyline.setOptions({
              strokeColor: '#1760ff',
              strokeWeight: 3
            })
          })

          // 添加鼠标悬停事件
          polyline.on('mouseover', () => {
            polyline.setOptions({
              strokeColor: '#1760ff',
              strokeWeight: 3
            })
          })

          // 添加鼠标离开事件
          polyline.on('mouseout', () => {
            polyline.setOptions({
              strokeColor: '#efefef',
              strokeWeight: 2
            })
          })

          // 确保路径添加到地图上
          polyline.setMap(map.value)
          polylines.value.push(polyline)
          console.log(`路径 ${road.id} 创建成功，路径点数量:`, path.length)
          
          // 如果是第一条路径，将地图中心点设置到这条路径的中间点
          if (road.id === 1) {
            const midPoint = path[Math.floor(path.length / 2)]
            console.log('设置地图中心点到第一条路径的中间点:', midPoint)
            map.value.setCenter(midPoint)
            map.value.setZoom(17)
          }
        } else {
          console.warn(`路径 ${road.id} 没有有效的路径点`)
        }
      } else {
        console.warn(`路径 ${road.id} 没有路径点数据`)
      }
    } catch (error) {
      console.error('创建路径失败:', error, road)
    }
  })
  console.log('路径创建完成，共创建', polylines.value.length, '条路径')
}

// 路线规划方法
const calculateRoute = async (destination, transportMode) => {
  if (!map.value || !driving.value) {
    console.error('地图或路线规划服务未初始化')
    return
  }

  try {
    console.log('开始路线规划...')
    console.log('目的地:', destination)
    console.log('交通方式:', transportMode)

    // 清除旧路线
    driving.value.clear()
    
    // 设置交通方式策略
    const policyMap = {
      walking: window.AMap.DrivingPolicy.LEAST_TIME,
      bike: window.AMap.DrivingPolicy.LEAST_DISTANCE,
      scooter: window.AMap.DrivingPolicy.REAL_TRAFFIC
    }

    const policy = policyMap[transportMode]
    console.log('使用的路线策略:', policy)
    driving.value.setPolicy(policy)

    // 获取当前位置（使用第一个建筑物作为起点）
    const start = buildings.value.length > 0 
      ? [buildings.value[0].longitude, buildings.value[0].latitude]
      : [116.326515, 40.000036]
    
    console.log('起点坐标:', start)
    console.log('终点坐标:', destination)

    // 执行路线规划
    driving.value.search(
      start,
      destination,
      {
        waypoints: [], // 途经点
        extensions: 'all', // 返回详细信息
        showTraffic: true // 显示实时路况
      },
      (status, result) => {
        console.log('路线规划状态:', status)
        if (status === 'complete') {
          console.log('路线规划成功，详细信息:', result)
          
          // 处理路线信息
          if (result.routes && result.routes.length > 0) {
            const route = result.routes[0]
            console.log('路线距离:', route.distance, '米')
            console.log('预计时间:', route.time, '秒')
            console.log('路线步骤:', route.steps)
          }
        } else {
          console.error('路线规划失败:', result)
          // 显示错误信息
          if (result.info) {
            console.error('错误信息:', result.info)
          }
        }
      }
    )
  } catch (error) {
    console.error('路线规划过程出错:', error)
  }
}

// 初始化地图
const initMap = async () => {
  try {
    console.log('开始初始化地图...')
    const AMap = await AMapLoader.load({
      key: '2a92b775baf99c5bddcc6640b82ceb34',
      version: '2.0',
      plugins: ['AMap.Scale', 'AMap.ToolBar', 'AMap.PlaceSearch', 'AMap.Driving']
    })

    window.AMap = AMap // 保存到全局变量
    console.log('高德地图加载成功')

    // 先获取数据
    await fetchAllData()

    // 如果有建筑物数据，使用第一个建筑物的位置作为地图中心点
    let center = [116.3156, 39.9612] // 默认中心点
    if (buildings.value.length > 0) {
      const firstBuilding = buildings.value[0]
      if (firstBuilding.longitude && firstBuilding.latitude) {
        center = [firstBuilding.longitude, firstBuilding.latitude]
        console.log('使用第一个建筑物作为地图中心点:', center)
      } else {
        console.error('第一个建筑物坐标缺失:', firstBuilding)
      }
    } else {
      console.warn('没有建筑物数据，使用默认中心点')
    }

    map.value = new AMap.Map('map-container', {
      zoom: 17,
      center: center,
      mapStyle: 'amap://styles/whitesmoke',
      viewMode: '2D',
      backgroundColor: '#ffffff' // 设置背景色为白色
    })

    // 添加控件
    map.value.addControl(new AMap.ToolBar())
    map.value.addControl(new AMap.Scale())

    // 初始化路线规划服务
    driving.value = new AMap.Driving({
      map: map.value,
      policy: AMap.DrivingPolicy.LEAST_TIME
    })

    // 创建标记和路径
    createMarkers(AMap)
    createPolylines(AMap)
    console.log('地图初始化完成')

    // 添加地图加载完成事件
    map.value.on('complete', () => {
      console.log('地图加载完成，重新创建路径')
      createPolylines(AMap)
    })
  } catch (error) {
    console.error('地图初始化失败:', error)
  }
}

onMounted(() => {
  initMap()
})

onUnmounted(() => {
  // 清理地图实例
  if (map.value) {
    map.value.destroy()
  }
})

// 暴露方法给父组件
defineExpose({
  calculateRoute
})
</script>

<style scoped>
#map-container {
  position: relative;
}

.loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
}
</style>
<template>
  <div id="map-container" style="width: 100%; height: 500px; padding:0px ; margin: 0px;"></div>
</template>

<script setup>
  // Map.vue 修改后代码
import { onMounted } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'

let map = null // 全局声明 map 变量

onMounted(() => {
  AMapLoader.load({
    key: '2a92b775baf99c5bddcc6640b82ceb34', // 替换为你的 Key
    version: '2.0',
    plugins: ['AMap.Scale', 'AMap.ToolBar', 'AMap.PlaceSearch'] // 按需加载插件
  })
    .then((AMap) => {
      map = new AMap.Map('map-container', {
        zoom: 15,
        center: [116.397428, 39.90923]
      })
      // 添加控件（示例）
      map.addControl(new AMap.ToolBar())
    })
    .catch((error) => {
      console.error('地图加载失败:', error)
    })
})
</script>
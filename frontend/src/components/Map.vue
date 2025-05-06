const calculateMapCenter = () => {
  if (buildings.value.length === 0 && facilities.value.length === 0) return
  
  let minLat = Number.MAX_VALUE
  let maxLat = Number.MIN_VALUE
  let minLng = Number.MAX_VALUE
  let maxLng = Number.MIN_VALUE
  
  // 计算所有建筑物、设施和路径点的经纬度范围
  const allPoints = [
    ...buildings.value,
    ...facilities.value,
    ...roadPathPoints.value
  ]
  
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
    canvas.width / (lngRange * 1000),
    canvas.height / (latRange * 1000)
  ) * 0.8 // 留出一些边距
  
  scale.value = scaleFactor
  
  // 设置偏移量，使地图居中
  offsetX.value = canvas.width / 2
  offsetY.value = canvas.height / 2
  
  console.log('Map center:', { 
    centerLat, 
    centerLng, 
    scale: scale.value,
    latRange,
    lngRange,
    canvasSize: `${canvas.width}x${canvas.height}`
  })
}

const drawRoads = (context, canvas) => {
  console.log('画布尺寸:', canvas.width, 'x', canvas.height);
  console.log('当前缩放值:', scale.value);
  console.log('当前偏移量:', offsetX.value, offsetY.value);
  console.log('开始绘制道路，道路数量:', roads.value.length);
  console.log('路径点数量:', roadPathPoints.value.length);
  
  context.strokeStyle = '#4a4a6a'
  context.lineWidth = Math.max(1, 1.5 * scale.value)
  
  // 计算所有路径点的经纬度范围
  const lngs = roadPathPoints.value.map(p => p.longitude);
  const lats = roadPathPoints.value.map(p => p.latitude);
  const minLng = Math.min(...lngs);
  const maxLng = Math.max(...lngs);
  const minLat = Math.min(...lats);
  const maxLat = Math.max(...lats);
  
  // 计算经纬度范围
  const lngRange = maxLng - minLng;
  const latRange = maxLat - minLat;
  
  console.log('路径点范围:', {
    minLng, maxLng, minLat, maxLat,
    lngRange, latRange
  });
  
  // 计算缩放因子，使地图内容适合画布
  const scaleX = canvas.width / lngRange;
  const scaleY = canvas.height / latRange;
  const scaleFactor = Math.min(scaleX, scaleY) * 0.8; // 留出一些边距
  
  console.log('计算得到的缩放因子:', scaleFactor);
  
  // 将经纬度转换为画布坐标的函数
  const lngLatToCanvas = (lng, lat) => {
    // 将经纬度归一化到0-1范围
    const normalizedX = (lng - minLng) / lngRange;
    const normalizedY = (lat - minLat) / latRange;
    
    // 转换为画布坐标
    const x = normalizedX * canvas.width + offsetX.value;
    const y = normalizedY * canvas.height + offsetY.value;
    
    return { x, y };
  };
  
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
      const { x, y } = lngLatToCanvas(point.longitude, point.latitude);
      
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
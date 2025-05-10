import request from '@/utils/request'

// 获取所有建筑物
export function getBuildings() {
  return request({
    url: '/api/buildings',
    method: 'get'
  })
}

// 获取所有设施
export function getFacilities() {
  return request({
    url: '/api/facilities',
    method: 'get'
  })
}

// 获取所有道路连接
export function getRoadConnections() {
  return request({
    url: '/api/road-connections',
    method: 'get'
  })
}

// 获取特定道路的路径点
export function getRoadPathPoints(roadId) {
  return request({
    url: `/api/road-path-points/${roadId}`,
    method: 'get'
  })
} 
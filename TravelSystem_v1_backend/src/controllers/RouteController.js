const routeService = require('../services/RouteService');
const { buildings, facilities } = require('../models');
const { Op } = require('sequelize');

class RouteController {
  // 计算路线
  async calculateRoute(req, res) {
    try {
      const { start, end, transport, strategy } = req.body;
      
      // 验证参数
      if (!start || !end) {
        return res.status(400).json({ error: '起点和终点不能为空' });
      }
      
      // 获取起点和终点建筑物
      const startBuilding = await buildings.findOne({ where: { id: start } });
      const endBuilding = await buildings.findOne({ where: { id: end } });
      
      if (!startBuilding || !endBuilding) {
        return res.status(404).json({ error: '起点或终点不存在' });
      }
      
      // 计算路线
      const route = routeService.dijkstra(start, end, strategy);
      
      // 获取路径上的建筑物信息
      const pathBuildings = await Promise.all(
        route.path.map(id => buildings.findByPk(id))
      );
      
      // 构建返回数据
      const response = {
        distance: route.distance,
        duration: route.duration,
        points: pathBuildings.map(b => b.name),
        path: pathBuildings.map(b => [b.longitude, b.latitude])
      };
      
      res.json(response);
    } catch (error) {
      console.error('路线计算错误:', error);
      res.status(500).json({ error: '路线计算失败' });
    }
  }
  
  // 查找附近设施
  async findNearbyFacilities(req, res) {
    try {
      const { buildingId } = req.params;
      const { types } = req.query;
      
      if (!buildingId) {
        return res.status(400).json({ error: '建筑物ID不能为空' });
      }
      
      const facilities = await routeService.findNearbyFacilities(
        buildingId,
        types ? types.split(',') : []
      );
      
      res.json(facilities);
    } catch (error) {
      console.error('查找附近设施错误:', error);
      res.status(500).json({ error: '查找附近设施失败' });
    }
  }
  
  // 搜索建筑物和设施
  async search(req, res) {
    try {
      const { query } = req.query;
      
      if (!query) {
        return res.status(400).json({ error: '搜索关键词不能为空' });
      }
      
      // 搜索建筑物
      const buildingsResults = await buildings.findAll({
        where: {
          name: {
            [Op.like]: `%${query}%`
          }
        }
      });
      
      // 搜索设施
      const facilitiesResults = await facilities.findAll({
        where: {
          name: {
            [Op.like]: `%${query}%`
          }
        }
      });
      
      res.json({
        buildings: buildingsResults,
        facilities: facilitiesResults
      });
    } catch (error) {
      console.error('搜索错误:', error);
      res.status(500).json({ error: '搜索失败' });
    }
  }
}

module.exports = new RouteController(); 
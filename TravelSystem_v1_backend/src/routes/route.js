const express = require('express');
const router = express.Router();
const routeController = require('../controllers/RouteController');

// 计算路线
router.post('/route', routeController.calculateRoute);

// 查找附近设施
router.get('/facilities/nearby/:buildingId', routeController.findNearbyFacilities);

// 搜索建筑物和设施
router.get('/search', routeController.search);

module.exports = router; 
const { Sequelize } = require('sequelize');
const config = require('../config/database');

const sequelize = new Sequelize(config);

// 建筑物模型
const Building = sequelize.define('Building', {
  id: {
    type: Sequelize.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  name: {
    type: Sequelize.STRING,
    allowNull: false
  },
  description: {
    type: Sequelize.TEXT
  },
  latitude: {
    type: Sequelize.FLOAT,
    allowNull: false
  },
  longitude: {
    type: Sequelize.FLOAT,
    allowNull: false
  },
  status: {
    type: Sequelize.INTEGER,
    defaultValue: 1
  }
});

// 设施模型
const Facility = sequelize.define('Facility', {
  id: {
    type: Sequelize.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  name: {
    type: Sequelize.STRING,
    allowNull: false
  },
  type: {
    type: Sequelize.STRING,
    allowNull: false
  },
  latitude: {
    type: Sequelize.FLOAT,
    allowNull: false
  },
  longitude: {
    type: Sequelize.FLOAT,
    allowNull: false
  },
  status: {
    type: Sequelize.INTEGER,
    defaultValue: 1
  }
});

// 道路模型
const Road = sequelize.define('Road', {
  id: {
    type: Sequelize.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  start_building_id: {
    type: Sequelize.INTEGER,
    allowNull: false,
    references: {
      model: Building,
      key: 'id'
    }
  },
  end_building_id: {
    type: Sequelize.INTEGER,
    allowNull: false,
    references: {
      model: Building,
      key: 'id'
    }
  },
  distance: {
    type: Sequelize.FLOAT,
    allowNull: false
  },
  congestion: {
    type: Sequelize.FLOAT,
    defaultValue: 0
  },
  ideal_speed: {
    type: Sequelize.FLOAT,
    defaultValue: 1.4 // 默认步行速度 1.4m/s
  },
  is_bike_road: {
    type: Sequelize.BOOLEAN,
    defaultValue: false
  },
  is_electric_car_road: {
    type: Sequelize.BOOLEAN,
    defaultValue: false
  }
});

// 路径点模型
const PathPoint = sequelize.define('PathPoint', {
  id: {
    type: Sequelize.INTEGER,
    primaryKey: true,
    autoIncrement: true
  },
  road_id: {
    type: Sequelize.INTEGER,
    allowNull: false,
    references: {
      model: Road,
      key: 'id'
    }
  },
  latitude: {
    type: Sequelize.FLOAT,
    allowNull: false
  },
  longitude: {
    type: Sequelize.FLOAT,
    allowNull: false
  },
  sequence: {
    type: Sequelize.INTEGER,
    allowNull: false
  }
});

// 建立关联关系
Building.hasMany(Road, { as: 'StartRoads', foreignKey: 'start_building_id' });
Building.hasMany(Road, { as: 'EndRoads', foreignKey: 'end_building_id' });
Road.belongsTo(Building, { as: 'StartBuilding', foreignKey: 'start_building_id' });
Road.belongsTo(Building, { as: 'EndBuilding', foreignKey: 'end_building_id' });
Road.hasMany(PathPoint, { foreignKey: 'road_id' });
PathPoint.belongsTo(Road, { foreignKey: 'road_id' });

module.exports = {
  sequelize,
  Building,
  Facility,
  Road,
  PathPoint
}; 
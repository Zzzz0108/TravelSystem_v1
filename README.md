### 项目架构：

travel-system/
├── frontend/              # Vue3前端项目
│   ├── src/
│   │   ├── api/          # 接口请求封装
│   │   ├── assets/       # 静态资源
│   │   ├── components/   # 公共组件
│   │   ├── composables/  # 组合式函数
│   │   ├── router/       # 路由配置
│   │   ├── stores/       # Pinia状态管理
│   │   ├── styles/       # 全局样式
│   │   ├── utils/        # 工具函数
│   │   ├── views/        # 页面组件
│   │   └── main.js       # 应用入口
│
├── backend/              # Spring Boot后端项目
│   ├── src/main/java/
│   │   ├── config/       # 配置类
│   │   ├── controller/   # 控制器层
│   │   ├── dto/         # 数据传输对象
│   │   ├── entity/      # 数据库实体
│   │   ├── repository/  # 数据访问层
│   │   ├── service/     # 业务逻辑层
│   │   ├── util/        # 工具类
│   │   └── TravelApplication.java # 启动类
│   ├── src/main/resources/
│   │   ├── application.yml        # 配置文件
│   │   └── ...
│
└── database/            # 数据库脚本
​    ├── init.sql         # 初始化SQL
​    └── ...
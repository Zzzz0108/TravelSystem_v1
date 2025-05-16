CREATE TABLE IF NOT EXISTS `travel_system`.`facilities`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `type` varchar(255) NULL,
    `latitude` double NOT NULL,
    `longitude` double NOT NULL,
    `spot_id` bigint NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    `description` varchar(255) NULL,
    `icon` varchar(255) NULL,
    PRIMARY KEY  (`id` ),
    KEY `spot_id` (`spot_id` ),
    CONSTRAINT `facilities_ibfk_1` FOREIGN KEY (`spot_id`) REFERENCES `travel_system`.`spots` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

INSERT INTO `travel_system`.`facilities` VALUES (1, '图书馆', 'LIBRARY', 39.9632, 116.3186, 1, '2025-05-06 17:16:14', '校园图书馆', '📚');
INSERT INTO `travel_system`.`facilities` VALUES (2, '电子阅览室', 'LIBRARY', 39.9605, 116.3121, 1, '2025-05-06 17:16:14', '电子阅览室', '💻');
INSERT INTO `travel_system`.`facilities` VALUES (3, '自习室A', 'LIBRARY', 39.9640, 116.3166, 1, '2025-05-06 17:16:14', '自习室', '📖');
INSERT INTO `travel_system`.`facilities` VALUES (4, '自习室B', 'LIBRARY', 39.9623, 116.3111, 1, '2025-05-06 17:16:14', '自习室', '📖');
INSERT INTO `travel_system`.`facilities` VALUES (5, '自习室C', 'LIBRARY', 39.9658, 116.3156, 1, '2025-05-06 17:16:14', '自习室', '📖');
INSERT INTO `travel_system`.`facilities` VALUES (6, '第一食堂', 'CANTEEN', 39.9617, 116.3191, 1, '2025-05-06 17:16:14', '学生食堂', '🍽️');
INSERT INTO `travel_system`.`facilities` VALUES (7, '第二食堂', 'CANTEEN', 39.9642, 116.3136, 1, '2025-05-06 17:16:14', '学生食堂', '🍜');
INSERT INTO `travel_system`.`facilities` VALUES (8, '清真食堂', 'CANTEEN', 39.9625, 116.3181, 1, '2025-05-06 17:16:14', '清真食堂', '🥘');
INSERT INTO `travel_system`.`facilities` VALUES (9, '教工食堂', 'CANTEEN', 39.9650, 116.3126, 1, '2025-05-06 17:16:14', '教工食堂', '🍱');
INSERT INTO `travel_system`.`facilities` VALUES (10, '西餐厅', 'CANTEEN', 39.9613, 116.3171, 1, '2025-05-06 17:16:14', '西餐厅', '🍕');
INSERT INTO `travel_system`.`facilities` VALUES (11, '校园超市', 'STORE', 39.9638, 116.3116, 1, '2025-05-06 17:16:14', '校园超市', '🏪');
INSERT INTO `travel_system`.`facilities` VALUES (12, '水果店', 'STORE', 39.9611, 116.3161, 1, '2025-05-06 17:16:14', '水果店', '🍎');
INSERT INTO `travel_system`.`facilities` VALUES (13, '文具店', 'STORE', 39.9646, 116.3106, 1, '2025-05-06 17:16:14', '文具店', '✏️');
INSERT INTO `travel_system`.`facilities` VALUES (14, '书店', 'STORE', 39.9629, 116.3151, 1, '2025-05-06 17:16:14', '书店', '📗');
INSERT INTO `travel_system`.`facilities` VALUES (15, '便利店', 'STORE', 39.9654, 116.3196, 1, '2025-05-06 17:16:14', '便利店', '🏪');
INSERT INTO `travel_system`.`facilities` VALUES (16, '公共卫生间A', 'TOILET', 39.9616, 116.3141, 1, '2025-05-06 17:16:14', '公共卫生间', '🚻');
INSERT INTO `travel_system`.`facilities` VALUES (17, '公共卫生间B', 'TOILET', 39.9641, 116.3186, 1, '2025-05-06 17:16:14', '公共卫生间', '🚻');
INSERT INTO `travel_system`.`facilities` VALUES (18, '公共卫生间C', 'TOILET', 39.9624, 116.3131, 1, '2025-05-06 17:16:14', '公共卫生间', '🚻');
INSERT INTO `travel_system`.`facilities` VALUES (19, '公共卫生间D', 'TOILET', 39.9659, 116.3176, 1, '2025-05-06 17:16:14', '公共卫生间', '🚻');
INSERT INTO `travel_system`.`facilities` VALUES (20, '公共卫生间E', 'TOILET', 39.9612, 116.3121, 1, '2025-05-06 17:16:14', '公共卫生间', '🚻');
INSERT INTO `travel_system`.`facilities` VALUES (21, '星巴克', 'CAFE', 39.9637, 116.3166, 1, '2025-05-06 17:16:14', '星巴克咖啡', '☕');
INSERT INTO `travel_system`.`facilities` VALUES (22, '瑞幸咖啡', 'CAFE', 39.9610, 116.3111, 1, '2025-05-06 17:16:14', '瑞幸咖啡', '☕');
INSERT INTO `travel_system`.`facilities` VALUES (23, '校园咖啡', 'CAFE', 39.9645, 116.3156, 1, '2025-05-06 17:16:14', '校园咖啡', '☕');
INSERT INTO `travel_system`.`facilities` VALUES (24, '咖啡角A', 'CAFE', 39.9628, 116.3101, 1, '2025-05-06 17:16:14', '咖啡角', '☕');
INSERT INTO `travel_system`.`facilities` VALUES (25, '咖啡角B', 'CAFE', 39.9653, 116.3146, 1, '2025-05-06 17:16:14', '咖啡角', '☕');
INSERT INTO `travel_system`.`facilities` VALUES (26, '体育馆', 'STADIUM', 39.9615, 116.3191, 1, '2025-05-06 17:16:14', '体育馆', '🏟️');
INSERT INTO `travel_system`.`facilities` VALUES (27, '游泳馆', 'STADIUM', 39.9640, 116.3136, 1, '2025-05-06 17:16:14', '游泳馆', '🏊');
INSERT INTO `travel_system`.`facilities` VALUES (28, '篮球场A', 'STADIUM', 39.9623, 116.3181, 1, '2025-05-06 17:16:14', '篮球场', '🏀');
INSERT INTO `travel_system`.`facilities` VALUES (29, '篮球场B', 'STADIUM', 39.9658, 116.3126, 1, '2025-05-06 17:16:14', '篮球场', '🏀');
INSERT INTO `travel_system`.`facilities` VALUES (30, '网球场', 'STADIUM', 39.9611, 116.3171, 1, '2025-05-06 17:16:14', '网球场', '🎾');
INSERT INTO `travel_system`.`facilities` VALUES (31, '排球场', 'STADIUM', 39.9646, 116.3116, 1, '2025-05-06 17:16:14', '排球场', '🏐');
INSERT INTO `travel_system`.`facilities` VALUES (32, '羽毛球场', 'STADIUM', 39.9629, 116.3161, 1, '2025-05-06 17:16:14', '羽毛球场', '🏸');
INSERT INTO `travel_system`.`facilities` VALUES (33, '乒乓球室', 'STADIUM', 39.9654, 116.3106, 1, '2025-05-06 17:16:14', '乒乓球室', '🏓');
INSERT INTO `travel_system`.`facilities` VALUES (34, '健身房', 'STADIUM', 39.9617, 116.3151, 1, '2025-05-06 17:16:14', '健身房', '💪');
INSERT INTO `travel_system`.`facilities` VALUES (35, '田径场', 'STADIUM', 39.9642, 116.3196, 1, '2025-05-06 17:16:14', '田径场', '🏃');
INSERT INTO `travel_system`.`facilities` VALUES (36, '校医院', 'CLINIC', 39.9625, 116.3141, 1, '2025-05-06 17:16:14', '校医院', '🏥');
INSERT INTO `travel_system`.`facilities` VALUES (37, '医务室A', 'CLINIC', 39.9650, 116.3186, 1, '2025-05-06 17:16:14', '医务室', '💊');
INSERT INTO `travel_system`.`facilities` VALUES (38, '医务室B', 'CLINIC', 39.9613, 116.3131, 1, '2025-05-06 17:16:14', '医务室', '💊');
INSERT INTO `travel_system`.`facilities` VALUES (39, '药房', 'CLINIC', 39.9648, 116.3176, 1, '2025-05-06 17:16:14', '药房', '💊');
INSERT INTO `travel_system`.`facilities` VALUES (40, '工商银行', 'BANK', 39.9621, 116.3121, 1, '2025-05-06 17:16:14', '工商银行', '🏦');
INSERT INTO `travel_system`.`facilities` VALUES (41, '建设银行', 'BANK', 39.9646, 116.3166, 1, '2025-05-06 17:16:14', '建设银行', '🏦');
INSERT INTO `travel_system`.`facilities` VALUES (42, '农业银行', 'BANK', 39.9619, 116.3111, 1, '2025-05-06 17:16:14', '农业银行', '🏦');
INSERT INTO `travel_system`.`facilities` VALUES (43, 'ATM机A', 'BANK', 39.9654, 116.3156, 1, '2025-05-06 17:16:14', 'ATM机', '🏧');
INSERT INTO `travel_system`.`facilities` VALUES (44, 'ATM机B', 'BANK', 39.9627, 116.3101, 1, '2025-05-06 17:16:14', 'ATM机', '🏧');
INSERT INTO `travel_system`.`facilities` VALUES (45, '菜鸟驿站', 'EXPRESS', 39.9652, 116.3146, 1, '2025-05-06 17:16:14', '菜鸟驿站', '📦');
INSERT INTO `travel_system`.`facilities` VALUES (46, '顺丰快递', 'EXPRESS', 39.9615, 116.3191, 1, '2025-05-06 17:16:14', '顺丰快递', '📦');
INSERT INTO `travel_system`.`facilities` VALUES (47, '京东快递', 'EXPRESS', 39.9640, 116.3136, 1, '2025-05-06 17:16:14', '京东快递', '📦');
INSERT INTO `travel_system`.`facilities` VALUES (48, '快递点A', 'EXPRESS', 39.9623, 116.3181, 1, '2025-05-06 17:16:14', '快递点', '📦');
INSERT INTO `travel_system`.`facilities` VALUES (49, '快递点B', 'EXPRESS', 39.9658, 116.3126, 1, '2025-05-06 17:16:14', '快递点', '📦');
INSERT INTO `travel_system`.`facilities` VALUES (50, '文印中心', 'PRINT', 39.9611, 116.3171, 1, '2025-05-06 17:16:14', '文印中心', '🖨️');
INSERT INTO `travel_system`.`facilities` VALUES (51, '快印店A', 'PRINT', 39.9646, 116.3116, 1, '2025-05-06 17:16:14', '快印店', '🖨️');
INSERT INTO `travel_system`.`facilities` VALUES (52, '快印店B', 'PRINT', 39.9629, 116.3161, 1, '2025-05-06 17:16:14', '快印店', '🖨️');


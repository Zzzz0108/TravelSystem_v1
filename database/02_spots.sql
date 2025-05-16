CREATE TABLE IF NOT EXISTS `travel_system`.`spots`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `city` varchar(255) NOT NULL,
    `popularity` int NOT NULL DEFAULT '0',
    `type` enum('SCENIC_AREA','UNIVERSITY') NOT NULL,
    `latitude` double NOT NULL,
    `longitude` double NOT NULL,
    `image` varchar(255) NULL,
    `created_at` timestamp NULL DEFAULT (CURRENT_TIMESTAMP),
    `updated_at` timestamp NULL DEFAULT (CURRENT_TIMESTAMP) ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY  (`id` )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

INSERT INTO `travel_system`.`spots` VALUES (1, '北京大学', '北京', 1500, 'UNIVERSITY', 39.9927, 116.3057, 'https://picsum.photos/300/200?random=1', '2025-04-19 16:13:34', '2025-04-19 16:13:34');
INSERT INTO `travel_system`.`spots` VALUES (2, '清华大学', '北京', 1804, 'UNIVERSITY', 39.9999, 116.3255, 'https://picsum.photos/300/200?random=2', '2025-04-19 16:13:34', '2025-04-26 13:42:06');
INSERT INTO `travel_system`.`spots` VALUES (3, '故宫', '北京', 2543, 'SCENIC_AREA', 39.9163, 116.3972, 'https://picsum.photos/300/200?random=3', '2025-04-19 16:13:34', '2025-05-12 20:06:22');
INSERT INTO `travel_system`.`spots` VALUES (4, '颐和园', '北京', 2001, 'SCENIC_AREA', 39.9987, 116.2747, 'https://picsum.photos/300/200?random=4', '2025-04-19 16:13:34', '2025-04-19 17:36:37');
INSERT INTO `travel_system`.`spots` VALUES (5, '复旦大学', '上海', 1301, 'UNIVERSITY', 31.2990, 121.4998, 'https://picsum.photos/300/200?random=5', '2025-04-19 16:13:34', '2025-04-22 10:00:45');
INSERT INTO `travel_system`.`spots` VALUES (6, '上海交通大学', '上海', 1300, 'UNIVERSITY', 31.2023, 121.4273, 'https://picsum.photos/300/200?random=6', '2025-04-19 16:13:34', '2025-04-19 16:13:34');
INSERT INTO `travel_system`.`spots` VALUES (7, '外滩', '上海', 2203, 'SCENIC_AREA', 31.2337, 121.4907, 'https://picsum.photos/300/200?random=7', '2025-04-19 16:13:34', '2025-04-22 09:55:20');
INSERT INTO `travel_system`.`spots` VALUES (8, '东方明珠', '上海', 2303, 'SCENIC_AREA', 31.2397, 121.4998, 'https://picsum.photos/300/200?random=8', '2025-04-19 16:13:34', '2025-04-29 01:09:19');
INSERT INTO `travel_system`.`spots` VALUES (9, '中山大学', '广州', 1000, 'UNIVERSITY', 23.0997, 113.2997, 'https://picsum.photos/300/200?random=9', '2025-04-19 16:13:34', '2025-04-19 16:13:34');
INSERT INTO `travel_system`.`spots` VALUES (10, '华南理工大学', '广州', 900, 'UNIVERSITY', 23.0497, 113.3773, 'https://picsum.photos/300/200?random=10', '2025-04-19 16:13:34', '2025-04-19 16:13:34');
INSERT INTO `travel_system`.`spots` VALUES (11, '广州塔', '广州', 2100, 'SCENIC_AREA', 23.1066, 113.3215, 'https://picsum.photos/300/200?random=11', '2025-04-19 16:13:34', '2025-04-19 16:13:34');
INSERT INTO `travel_system`.`spots` VALUES (12, '白云山', '广州', 1907, 'SCENIC_AREA', 23.1833, 113.2667, 'https://picsum.photos/300/200?random=12', '2025-04-19 16:13:34', '2025-04-20 11:25:44');

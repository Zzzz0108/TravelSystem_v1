CREATE TABLE IF NOT EXISTS `travel_system`.`buildings`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `name` varchar(255) NOT NULL,
    `description` varchar(255) NOT NULL,
    `latitude` double NOT NULL,
    `longitude` double NOT NULL,
    `spot_id` bigint NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    `type` varchar(255) NULL,
    PRIMARY KEY  (`id` ),
    KEY `spot_id` (`spot_id` ),
    CONSTRAINT `buildings_ibfk_1` FOREIGN KEY (`spot_id`) REFERENCES `travel_system`.`spots` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;
INSERT INTO `travel_system`.`buildings` VALUES (1, '教一楼', '主要教学楼', 39.9612, 116.3156, 1, '2025-05-06 17:16:04', 'TEACHING');
INSERT INTO `travel_system`.`buildings` VALUES (2, '教二楼', '主要教学楼', 39.9634, 116.3089, 1, '2025-05-06 17:16:04', 'TEACHING');
INSERT INTO `travel_system`.`buildings` VALUES (3, '教三楼', '主要教学楼', 39.9608, 116.3123, 1, '2025-05-06 17:16:04', 'TEACHING');
INSERT INTO `travel_system`.`buildings` VALUES (4, '教四楼', '主要教学楼', 39.9647, 116.3178, 1, '2025-05-06 17:16:04', 'TEACHING');
INSERT INTO `travel_system`.`buildings` VALUES (5, '教五楼', '主要教学楼', 39.9621, 116.3097, 1, '2025-05-06 17:16:04', 'TEACHING');
INSERT INTO `travel_system`.`buildings` VALUES (6, '教六楼', '主要教学楼', 39.9656, 116.3142, 1, '2025-05-06 17:16:04', 'TEACHING');
INSERT INTO `travel_system`.`buildings` VALUES (7, '教七楼', '主要教学楼', 39.9619, 116.3167, 1, '2025-05-06 17:16:04', 'TEACHING');
INSERT INTO `travel_system`.`buildings` VALUES (8, '教八楼', '主要教学楼', 39.9638, 116.3112, 1, '2025-05-06 17:16:04', 'TEACHING');
INSERT INTO `travel_system`.`buildings` VALUES (9, '教九楼', '主要教学楼', 39.9603, 116.3189, 1, '2025-05-06 17:16:04', 'TEACHING');
INSERT INTO `travel_system`.`buildings` VALUES (10, '教十楼', '主要教学楼', 39.9642, 116.3134, 1, '2025-05-06 17:16:04', 'TEACHING');
INSERT INTO `travel_system`.`buildings` VALUES (11, '行政楼', '行政办公', 39.9627, 116.3176, 1, '2025-05-06 17:16:04', 'ADMIN');
INSERT INTO `travel_system`.`buildings` VALUES (12, '综合楼', '行政办公', 39.9649, 116.3109, 1, '2025-05-06 17:16:04', 'ADMIN');
INSERT INTO `travel_system`.`buildings` VALUES (13, '办公楼', '行政办公', 39.9614, 116.3143, 1, '2025-05-06 17:16:04', 'ADMIN');
INSERT INTO `travel_system`.`buildings` VALUES (14, '会议中心', '行政办公', 39.9653, 116.3198, 1, '2025-05-06 17:16:04', 'ADMIN');
INSERT INTO `travel_system`.`buildings` VALUES (15, '校史馆', '行政办公', 39.9628, 116.3117, 1, '2025-05-06 17:16:04', 'ADMIN');
INSERT INTO `travel_system`.`buildings` VALUES (16, '学一楼', '学生宿舍', 39.9636, 116.3162, 1, '2025-05-06 17:16:04', 'DORMITORY');
INSERT INTO `travel_system`.`buildings` VALUES (17, '学二楼', '学生宿舍', 39.9609, 116.3097, 1, '2025-05-06 17:16:04', 'DORMITORY');
INSERT INTO `travel_system`.`buildings` VALUES (18, '学三楼', '学生宿舍', 39.9644, 116.3142, 1, '2025-05-06 17:16:04', 'DORMITORY');
INSERT INTO `travel_system`.`buildings` VALUES (19, '学四楼', '学生宿舍', 39.9627, 116.3187, 1, '2025-05-06 17:16:04', 'DORMITORY');
INSERT INTO `travel_system`.`buildings` VALUES (20, '学五楼', '学生宿舍', 39.9652, 116.3132, 1, '2025-05-06 17:16:04', 'DORMITORY');
INSERT INTO `travel_system`.`buildings` VALUES (21, '学六楼', '学生宿舍', 39.9615, 116.3177, 1, '2025-05-06 17:16:04', 'DORMITORY');
INSERT INTO `travel_system`.`buildings` VALUES (22, '学七楼', '学生宿舍', 39.9640, 116.3122, 1, '2025-05-06 17:16:04', 'DORMITORY');
INSERT INTO `travel_system`.`buildings` VALUES (23, '学八楼', '学生宿舍', 39.9623, 116.3167, 1, '2025-05-06 17:16:04', 'DORMITORY');
INSERT INTO `travel_system`.`buildings` VALUES (24, '学九楼', '学生宿舍', 39.9658, 116.3112, 1, '2025-05-06 17:16:04', 'DORMITORY');
INSERT INTO `travel_system`.`buildings` VALUES (25, '学十楼', '学生宿舍', 39.9611, 116.3157, 1, '2025-05-06 17:16:04', 'DORMITORY');
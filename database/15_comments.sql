CREATE TABLE IF NOT EXISTS `travel_system`.`comments`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `user_id` bigint NOT NULL,
    `content` text NOT NULL,
    `created_at` timestamp NULL DEFAULT (CURRENT_TIMESTAMP),
    `parent_id` bigint NULL,
    `target_type` varchar(20) NOT NULL COMMENT '评论目标类型：spot/facility/building',
    `target_id` bigint NOT NULL COMMENT '评论目标ID',
    PRIMARY KEY  (`id` ),
    KEY `user_id` (`user_id` ),
    KEY `parent_id` (`parent_id` ),
    KEY `target` (`target_type` ,`target_id` ),
    CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `travel_system`.`users` (`id`),
    CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `travel_system`.`comments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

-- 景点评论
INSERT INTO `travel_system`.`comments` VALUES (1, 1, '丽江古城的小巷很有特色，值得慢慢逛', '2025-04-25 10:20:15', NULL, 'spot', 1);
INSERT INTO `travel_system`.`comments` VALUES (2, 2, '确实，每条小巷都有不同的风景', '2025-04-25 10:30:45', 1, 'spot', 1);
INSERT INTO `travel_system`.`comments` VALUES (3, 3, '玉龙雪山的景色太震撼了', '2025-04-25 11:15:30', NULL, 'spot', 2);

-- 设施评论
INSERT INTO `travel_system`.`comments` VALUES (4, 1, '这家餐厅的云南菜很地道', '2025-04-25 12:30:20', NULL, 'facility', 1);
INSERT INTO `travel_system`.`comments` VALUES (5, 2, '服务态度也很好', '2025-04-25 12:45:10', 4, 'facility', 1);
INSERT INTO `travel_system`.`comments` VALUES (6, 3, '酒店位置很好，交通方便', '2025-04-25 13:20:15', NULL, 'facility', 2);

-- 建筑评论
INSERT INTO `travel_system`.`comments` VALUES (7, 1, '布达拉宫的建筑风格很独特', '2025-04-25 14:30:40', NULL, 'building', 1);
INSERT INTO `travel_system`.`comments` VALUES (8, 2, '确实，体现了藏族建筑艺术的精髓', '2025-04-25 14:45:25', 7, 'building', 1);
INSERT INTO `travel_system`.`comments` VALUES (9, 3, '大雁塔的历史感很强', '2025-04-25 15:20:15', NULL, 'building', 2);

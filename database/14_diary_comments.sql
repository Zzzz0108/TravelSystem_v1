CREATE TABLE IF NOT EXISTS `travel_system`.`diary_comments`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `diary_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `content` text NOT NULL,
    `created_at` timestamp NULL DEFAULT (CURRENT_TIMESTAMP),
    PRIMARY KEY  (`id` ),
    KEY `diary_id` (`diary_id` ),
    KEY `user_id` (`user_id` ),
    CONSTRAINT `diary_comments_ibfk_1` FOREIGN KEY (`diary_id`) REFERENCES `travel_system`.`diaries` (`id`),
    CONSTRAINT `diary_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `travel_system`.`users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

-- 西安游记评论
INSERT INTO `travel_system`.`diary_comments` VALUES (1, 5, 1, '大雁塔真的很壮观！', '2025-04-25 20:56:40');
INSERT INTO `travel_system`.`diary_comments` VALUES (2, 5, 2, '城墙夜景太美了', '2025-04-25 21:15:30');
INSERT INTO `travel_system`.`diary_comments` VALUES (3, 5, 3, '华清池的历史感很强', '2025-04-25 22:30:45');

-- 云南游记评论
INSERT INTO `travel_system`.`diary_comments` VALUES (4, 1, 2, '丽江古城的小巷很有特色', '2025-04-26 10:20:15');
INSERT INTO `travel_system`.`diary_comments` VALUES (5, 1, 3, '玉龙雪山的景色太震撼了', '2025-04-26 11:45:30');

-- 成都游记评论
INSERT INTO `travel_system`.`diary_comments` VALUES (6, 2, 1, '春熙路的美食真的太多了', '2025-04-26 14:30:20');
INSERT INTO `travel_system`.`diary_comments` VALUES (7, 2, 3, '宽窄巷子的文化氛围很浓', '2025-04-26 15:45:10');

-- 西藏游记评论
INSERT INTO `travel_system`.`diary_comments` VALUES (8, 3, 1, '布达拉宫的气势太震撼了', '2025-04-27 09:15:40');
INSERT INTO `travel_system`.`diary_comments` VALUES (9, 3, 2, '纳木错的湖水太美了', '2025-04-27 10:30:25');

-- 厦门游记评论
INSERT INTO `travel_system`.`diary_comments` VALUES (10, 4, 1, '鼓浪屿的建筑很有特色', '2025-04-27 13:20:15');
INSERT INTO `travel_system`.`diary_comments` VALUES (11, 4, 3, '曾厝垵的小吃太棒了', '2025-04-27 14:45:30');
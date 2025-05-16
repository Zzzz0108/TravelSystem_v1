CREATE TABLE IF NOT EXISTS `travel_system`.`diary_ratings`(
    `diary_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    `rating` int NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    PRIMARY KEY  (`diary_id` ,`user_id` ),
    KEY `user_id` (`user_id` ),
    CONSTRAINT `diary_ratings_ibfk_1` FOREIGN KEY (`diary_id`) REFERENCES `travel_system`.`diaries` (`id`),
    CONSTRAINT `diary_ratings_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `travel_system`.`users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

-- 添加示例评分数据
INSERT INTO `travel_system`.`diary_ratings` VALUES (1, 1, 4, '2025-04-28 02:55:03');  -- 用户1给云南七日游攻略评4分
INSERT INTO `travel_system`.`diary_ratings` VALUES (2, 1, 5, '2025-04-29 02:04:24');  -- 用户1给成都美食探店评5分
INSERT INTO `travel_system`.`diary_ratings` VALUES (3, 1, 4, '2025-04-28 03:46:07');  -- 用户1给西藏自驾之旅评4分
INSERT INTO `travel_system`.`diary_ratings` VALUES (5, 1, 3, '2025-04-28 03:06:32');  -- 用户1给西安古城文化之旅评3分


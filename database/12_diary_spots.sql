CREATE TABLE IF NOT EXISTS `travel_system`.`diary_spots`(
    `diary_id` bigint NOT NULL,
    `spot_id` bigint NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    PRIMARY KEY  (`diary_id` ,`spot_id` ),
    KEY `spot_id` (`spot_id` ),
    CONSTRAINT `diary_spots_ibfk_1` FOREIGN KEY (`diary_id`) REFERENCES `travel_system`.`diaries` (`id`),
    CONSTRAINT `diary_spots_ibfk_2` FOREIGN KEY (`spot_id`) REFERENCES `travel_system`.`spots` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

-- 添加示例日记景点关联数据
INSERT INTO `travel_system`.`diary_spots` VALUES (1, 1, '2025-04-25 17:38:34');  -- 云南七日游攻略关联丽江古城
INSERT INTO `travel_system`.`diary_spots` VALUES (1, 2, '2025-04-25 17:38:34');  -- 云南七日游攻略关联玉龙雪山
INSERT INTO `travel_system`.`diary_spots` VALUES (2, 3, '2025-04-25 17:38:34');  -- 成都美食探店关联春熙路
INSERT INTO `travel_system`.`diary_spots` VALUES (3, 4, '2025-04-25 17:38:34');  -- 西藏自驾之旅关联布达拉宫
INSERT INTO `travel_system`.`diary_spots` VALUES (3, 5, '2025-04-25 17:38:34');  -- 西藏自驾之旅关联纳木错
INSERT INTO `travel_system`.`diary_spots` VALUES (4, 6, '2025-04-25 17:38:34');  -- 厦门鼓浪屿游记关联鼓浪屿
INSERT INTO `travel_system`.`diary_spots` VALUES (5, 7, '2025-04-25 17:38:34');  -- 西安古城文化之旅关联兵马俑
INSERT INTO `travel_system`.`diary_spots` VALUES (5, 8, '2025-04-25 17:38:34');  -- 西安古城文化之旅关联大雁塔

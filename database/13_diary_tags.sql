CREATE TABLE IF NOT EXISTS `travel_system`.`diary_tags`(
    `diary_id` bigint NOT NULL,
    `tag` varchar(50) NOT NULL,
    `created_at` timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    PRIMARY KEY  (`diary_id` ,`tag` ),
    CONSTRAINT `diary_tags_ibfk_1` FOREIGN KEY (`diary_id`) REFERENCES `travel_system`.`diaries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

INSERT INTO `travel_system`.`diary_tags` VALUES (1, '云南', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (1, '古城', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (1, '雪山', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (2, '成都', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (2, '美食', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (2, '小吃', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (3, '西藏', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (3, '自驾', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (3, '高原', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (4, '厦门', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (4, '海岛', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (4, '文化', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (5, '西安', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (5, '历史', '2025-04-25 17:38:34');
INSERT INTO `travel_system`.`diary_tags` VALUES (5, '古迹', '2025-04-25 17:38:34');

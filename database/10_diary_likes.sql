CREATE TABLE IF NOT EXISTS `travel_system`.`diary_likes`(
    `diary_id` bigint NOT NULL,
    `user_id` bigint NOT NULL,
    KEY `FKp7trlk5854o4xy56umrni6e7m` (`user_id` ),
    KEY `FKjvchdemqkds357m5hhaih7utt` (`diary_id` ),
    CONSTRAINT `FKjvchdemqkds357m5hhaih7utt` FOREIGN KEY (`diary_id`) REFERENCES `travel_system`.`diaries` (`id`),
    CONSTRAINT `FKp7trlk5854o4xy56umrni6e7m` FOREIGN KEY (`user_id`) REFERENCES `travel_system`.`users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

-- 添加示例点赞数据
INSERT INTO `travel_system`.`diary_likes` VALUES (1, 2);  -- 用户2点赞了云南七日游攻略
INSERT INTO `travel_system`.`diary_likes` VALUES (1, 3);  -- 用户3点赞了云南七日游攻略
INSERT INTO `travel_system`.`diary_likes` VALUES (2, 1);  -- 用户1点赞了成都美食探店
INSERT INTO `travel_system`.`diary_likes` VALUES (2, 3);  -- 用户3点赞了成都美食探店
INSERT INTO `travel_system`.`diary_likes` VALUES (3, 1);  -- 用户1点赞了西藏自驾之旅
INSERT INTO `travel_system`.`diary_likes` VALUES (3, 2);  -- 用户2点赞了西藏自驾之旅
INSERT INTO `travel_system`.`diary_likes` VALUES (4, 2);  -- 用户2点赞了厦门鼓浪屿游记
INSERT INTO `travel_system`.`diary_likes` VALUES (4, 3);  -- 用户3点赞了厦门鼓浪屿游记
INSERT INTO `travel_system`.`diary_likes` VALUES (5, 1);  -- 用户1点赞了西安古城文化之旅
INSERT INTO `travel_system`.`diary_likes` VALUES (5, 3);  -- 用户3点赞了西安古城文化之旅


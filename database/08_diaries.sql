CREATE TABLE IF NOT EXISTS `travel_system`.`diaries`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `content` text NULL,
    `content_compressed` LONGBLOB NULL,
    `is_compressed` tinyint(1) NOT NULL DEFAULT '0',
    `created_at` datetime(6) NOT NULL,
    `likes` int NOT NULL,
    `title` varchar(255) NOT NULL,
    `views` int NOT NULL,
    `user_id` bigint NOT NULL,
    `comments_count` int NULL DEFAULT '0',
    `average_rating` double NOT NULL DEFAULT '0',
    `rating_count` int NOT NULL DEFAULT '0',
    `popularity_score` double NOT NULL DEFAULT '0',
    `destination` varchar(255) NULL,
    PRIMARY KEY  (`id` ),
    KEY `FKki7hoimuu910cy56y2695to5e` (`user_id` ),
    CONSTRAINT `FKki7hoimuu910cy56y2695to5e` FOREIGN KEY (`user_id`) REFERENCES `travel_system`.`users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

INSERT INTO `travel_system`.`diaries` VALUES (1, '探索丽江古城与玉龙雪山的绝美风光，感受纳西族文化的独特魅力。古城的小巷中，每一块石板都诉说着历史的故事。', '2025-04-25 17:38:34', 245, '云南七日游攻略', 1234, 1, 0, 4.0, 1, 496.0, '云南');
INSERT INTO `travel_system`.`diaries` VALUES (2, '从火锅到串串，从龙抄手到钟水饺，带你吃遍成都的街头巷尾。特别推荐春熙路附近的老字号，味道正宗，价格实惠。', '2025-04-25 17:38:34', 189, '成都美食探店', 987, 2, 1, 5.0, 1, 397.8, '成都');
INSERT INTO `travel_system`.`diaries` VALUES (3, '沿着318国道，穿越雪山草原，感受最纯净的高原风光。布达拉宫的庄严，纳木错的宁静，都是此生难忘的风景。', '2025-04-25 17:38:34', 312, '西藏自驾之旅', 1567, 3, 0, 4.0, 1, 629.2, '西藏');
INSERT INTO `travel_system`.`diaries` VALUES (4, '漫步在鼓浪屿的小巷中，感受中西文化的交融。每一栋老建筑都有它的故事，每一处风景都值得细细品味。', '2025-04-25 17:38:34', 178, '厦门鼓浪屿游记', 876, 1, 0, 0.0, 0, 350.4, '厦门');
INSERT INTO `travel_system`.`diaries` VALUES (5, '从兵马俑到华清池，从大雁塔到钟鼓楼，感受千年古都的历史沉淀。特别推荐夜晚的城墙漫步，别有一番风味。', '2025-04-25 17:38:34', 267, '西安古城文化之旅', 1345, 2, 0, 3.0, 1, 539.8, '西安');
INSERT INTO `travel_system`.`diaries` VALUES (6, '探索丽江古城与玉龙雪山的绝美风光，感受纳西族文化的独特魅力。古城的小巷中，每一块石板都诉说着历史的故事。', '2025-04-25 17:38:34', 245, '云南七日游攻略', 1234, 1, 0, 0.0, 0, 493.6, '云南');
INSERT INTO `travel_system`.`diaries` VALUES (7, '从火锅到串串，从龙抄手到钟水饺，带你吃遍成都的街头巷尾。特别推荐春熙路附近的老字号，味道正宗，价格实惠。', '2025-04-25 17:38:34', 189, '成都美食探店', 987, 2, 0, 0.0, 0, 394.8, '成都');
INSERT INTO `travel_system`.`diaries` VALUES (8, '沿着318国道，穿越雪山草原，感受最纯净的高原风光。布达拉宫的庄严，纳木错的宁静，都是此生难忘的风景。', '2025-04-25 17:38:34', 312, '西藏自驾之旅', 1567, 3, 0, 0.0, 0, 626.8, '西藏');
INSERT INTO `travel_system`.`diaries` VALUES (9, '漫步在鼓浪屿的小巷中，感受中西文化的交融。每一栋老建筑都有它的故事，每一处风景都值得细细品味。', '2025-04-25 17:38:34', 178, '厦门鼓浪屿游记', 876, 1, 0, 0.0, 0, 350.4, '厦门');
INSERT INTO `travel_system`.`diaries` VALUES (10, '从兵马俑到华清池，从大雁塔到钟鼓楼，感受千年古都的历史沉淀。特别推荐夜晚的城墙漫步，别有一番风味。', '2025-04-25 17:38:34', 267, '西安古城文化之旅', 1345, 2, 0, 0.0, 0, 538.0, '西安');
INSERT INTO `travel_system`.`diaries` VALUES (271, 'aa', '2025-05-12 19:36:21', 0, 'aa', 0, 1, 0, 0.0, 0, 0.0, NULL);
INSERT INTO `travel_system`.`diaries` VALUES (272, 'aa', '2025-05-12 19:39:18', 0, 'aa', 0, 1, 0, 0.0, 0, 0.0, NULL);
INSERT INTO `travel_system`.`diaries` VALUES (273, 'aa', '2025-05-12 19:39:39', 0, 'a', 0, 1, 0, 0.0, 0, 0.0, NULL);
INSERT INTO `travel_system`.`diaries` VALUES (274, 'aa', '2025-05-12 19:39:54', 0, 'a', 0, 1, 0, 0.0, 0, 0.0, NULL);
INSERT INTO `travel_system`.`diaries` VALUES (275, 'aa', '2025-05-12 19:43:16', 0, 'a', 0, 1, 0, 0.0, 0, 0.0, NULL);
INSERT INTO `travel_system`.`diaries` VALUES (276, '1111', '2025-05-12 19:44:47', 0, 'Test1', 0, 1, 0, 0.0, 0, 0.0, NULL);
INSERT INTO `travel_system`.`diaries` VALUES (277, '2222', '2025-05-12 20:04:21', 0, 'Test2', 0, 1, 0, 0.0, 0, 0.0, '西安');
INSERT INTO `travel_system`.`diaries` VALUES (278, '吃火锅好吗？好的', '2025-05-12 20:07:55', 0, '成都', 0, 1, 1, 0.0, 0, 0.0, '四川');

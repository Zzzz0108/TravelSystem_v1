CREATE TABLE IF NOT EXISTS `travel_system`.`diary_images`(
    `diary_id` bigint NOT NULL,
    `image_url` varchar(255) NULL,
    KEY `FK5ld4mw5kij2rxjaat714ekr3f` (`diary_id` ),
    CONSTRAINT `FK5ld4mw5kij2rxjaat714ekr3f` FOREIGN KEY (`diary_id`) REFERENCES `travel_system`.`diaries` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

-- 为每个日记添加一张默认图片
INSERT INTO `travel_system`.`diary_images` VALUES (1, '/images/diaries/default.jpg');
INSERT INTO `travel_system`.`diary_images` VALUES (2, '/images/diaries/default.jpg');
INSERT INTO `travel_system`.`diary_images` VALUES (3, '/images/diaries/default.jpg');
INSERT INTO `travel_system`.`diary_images` VALUES (4, '/images/diaries/default.jpg');
INSERT INTO `travel_system`.`diary_images` VALUES (5, '/images/diaries/default.jpg');
INSERT INTO `travel_system`.`diary_images` VALUES (6, '/images/diaries/default.jpg');
INSERT INTO `travel_system`.`diary_images` VALUES (7, '/images/diaries/default.jpg');
INSERT INTO `travel_system`.`diary_images` VALUES (8, '/images/diaries/default.jpg');
INSERT INTO `travel_system`.`diary_images` VALUES (9, '/images/diaries/default.jpg');
INSERT INTO `travel_system`.`diary_images` VALUES (10, '/images/diaries/default.jpg');

-- 为部分日记添加额外的随机图片
INSERT INTO `travel_system`.`diary_images` VALUES (1, 'https://picsum.photos/800/600?random=1');
INSERT INTO `travel_system`.`diary_images` VALUES (1, 'https://picsum.photos/800/600?random=2');
INSERT INTO `travel_system`.`diary_images` VALUES (2, 'https://picsum.photos/800/600?random=3');
INSERT INTO `travel_system`.`diary_images` VALUES (2, 'https://picsum.photos/800/600?random=4');
INSERT INTO `travel_system`.`diary_images` VALUES (3, 'https://picsum.photos/800/600?random=5');
INSERT INTO `travel_system`.`diary_images` VALUES (3, 'https://picsum.photos/800/600?random=6');
INSERT INTO `travel_system`.`diary_images` VALUES (4, 'https://picsum.photos/800/600?random=7');
INSERT INTO `travel_system`.`diary_images` VALUES (4, 'https://picsum.photos/800/600?random=8');
INSERT INTO `travel_system`.`diary_images` VALUES (5, 'https://picsum.photos/800/600?random=9');
INSERT INTO `travel_system`.`diary_images` VALUES (5, 'https://picsum.photos/800/600?random=10');

-- 添加用户上传的图片
INSERT INTO `travel_system`.`diary_images` VALUES (272, '/uploads/diaries/9ec45e8c-21ea-4eeb-9537-2c497ac0cb8f_96ffaa839642611ebd30d3c01a1ea074.jpeg');
INSERT INTO `travel_system`.`diary_images` VALUES (275, '/uploads/diaries/d7d98f0e-c72f-4d65-9166-e45a18592fe6_96ffaa839642611ebd30d3c01a1ea074.jpeg');
INSERT INTO `travel_system`.`diary_images` VALUES (276, '/uploads/diaries/a145ec19-7e07-478a-90a1-9f11c5b70467_96ffaa839642611ebd30d3c01a1ea074.jpeg');
INSERT INTO `travel_system`.`diary_images` VALUES (277, '/uploads/diaries/c8b15d63-be92-4ac8-9d9e-08e8c0034071_841e9c6e25528335d735c16825e7919c.jpeg');
INSERT INTO `travel_system`.`diary_images` VALUES (278, '/uploads/diaries/f27aa571-73a3-4fe6-a69b-a840693e1636_174673008600987.WEBP');

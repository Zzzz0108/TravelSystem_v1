CREATE TABLE IF NOT EXISTS `travel_system`.`user_favorite_spots`(
    `user_id` bigint NOT NULL,
    `spot_id` bigint NOT NULL,
    `created_at` timestamp NULL DEFAULT (CURRENT_TIMESTAMP),
    PRIMARY KEY  (`user_id` ,`spot_id` ),
    KEY `spot_id` (`spot_id` ),
    CONSTRAINT `user_favorite_spots_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `travel_system`.`users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `user_favorite_spots_ibfk_2` FOREIGN KEY (`spot_id`) REFERENCES `travel_system`.`spots` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;

INSERT INTO `travel_system`.`user_favorite_spots` VALUES (2, 3, '2025-04-22 11:00:22');
INSERT INTO `travel_system`.`user_favorite_spots` VALUES (2, 7, '2025-04-22 11:00:35');


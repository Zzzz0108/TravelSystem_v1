CREATE TABLE IF NOT EXISTS `travel_system`.`users`(
    `id` bigint NOT NULL AUTO_INCREMENT,
    `username` varchar(255) NOT NULL,
    `password` varchar(255) NOT NULL,
    `avatar` varchar(255) NULL,
    `created_at` timestamp NULL DEFAULT (CURRENT_TIMESTAMP),
    `updated_at` timestamp NULL DEFAULT (CURRENT_TIMESTAMP) ON UPDATE CURRENT_TIMESTAMP,
    `role` varchar(255) NULL,
    PRIMARY KEY  (`id` ),
    UNIQUE KEY `username` (`username` )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ;
INSERT INTO `travel_system`.`users` VALUES (1, 'demo1', '$2a$10$WYMGZJDpM.YOK8VE4WLZ8eaY3yc3HieZiBOIDTU2mUb3HslLQRTxu', NULL, '2025-04-19 16:30:18', '2025-04-19 16:30:18', NULL);
INSERT INTO `travel_system`.`users` VALUES (2, 'demo2', '$2a$10$MWVlmLGTlr1HJxWW2MKbVuRhf3re097jFJbFikDLSvP8X.wVMQV4C', NULL, '2025-04-19 16:59:54', '2025-04-19 16:59:54', 'USER');
INSERT INTO `travel_system`.`users` VALUES (3, 'demo3', '$2a$10$CJsfkyHjfHmiWL1XgtPOVu45nJ8Fr3qaiuC0T47eFm3xpJtJVWn1i', 'https://api.dicebear.com/7.x/initials/svg?seed=demo3', '2025-04-21 22:52:20', '2025-04-21 22:52:20', NULL);
INSERT INTO `travel_system`.`users` VALUES (4, 'demo4', '$2a$10$psQAShpzH0MeMTkX0DqACeEZdq5a6vJWV9xLI3q9F91e9005cfQCi', 'https://api.dicebear.com/7.x/initials/svg?seed=demo4', '2025-04-29 01:59:01', '2025-04-29 01:59:01', NULL);
INSERT INTO `travel_system`.`users` VALUES (5, 'demo5', '$2a$10$xbwicHDO8TPicOU0g2/7Auz9S5hwM.fDGgst5V5o.UW/AEj3VI5My', 'https://api.dicebear.com/7.x/initials/svg?seed=demo5', '2025-04-29 02:00:58', '2025-04-29 02:00:58', NULL);
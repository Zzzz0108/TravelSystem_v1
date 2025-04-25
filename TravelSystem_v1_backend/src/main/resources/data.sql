-- 插入日记数据
INSERT INTO diaries (title, content, created_at, likes, views, user_id) VALUES
('云南七日游攻略', '探索丽江古城与玉龙雪山的绝美风光，感受纳西族文化的独特魅力。古城的小巷中，每一块石板都诉说着历史的故事。', NOW(), 245, 1234, 1),
('成都美食探店', '从火锅到串串，从龙抄手到钟水饺，带你吃遍成都的街头巷尾。特别推荐春熙路附近的老字号，味道正宗，价格实惠。', NOW(), 189, 987, 2),
('西藏自驾之旅', '沿着318国道，穿越雪山草原，感受最纯净的高原风光。布达拉宫的庄严，纳木错的宁静，都是此生难忘的风景。', NOW(), 312, 1567, 3),
('厦门鼓浪屿游记', '漫步在鼓浪屿的小巷中，感受中西文化的交融。每一栋老建筑都有它的故事，每一处风景都值得细细品味。', NOW(), 178, 876, 1),
('西安古城文化之旅', '从兵马俑到华清池，从大雁塔到钟鼓楼，感受千年古都的历史沉淀。特别推荐夜晚的城墙漫步，别有一番风味。', NOW(), 267, 1345, 2);

-- 插入日记图片
INSERT INTO diary_images (diary_id, image_url) VALUES
(1, 'https://picsum.photos/800/600?random=1'),
(1, 'https://picsum.photos/800/600?random=2'),
(2, 'https://picsum.photos/800/600?random=3'),
(2, 'https://picsum.photos/800/600?random=4'),
(3, 'https://picsum.photos/800/600?random=5'),
(3, 'https://picsum.photos/800/600?random=6'),
(4, 'https://picsum.photos/800/600?random=7'),
(4, 'https://picsum.photos/800/600?random=8'),
(5, 'https://picsum.photos/800/600?random=9'),
(5, 'https://picsum.photos/800/600?random=10');

-- 插入日记标签
INSERT INTO diary_tags (diary_id, tag) VALUES
(1, '旅行攻略'),
(1, '摄影圣地'),
(1, '自驾游'),
(2, '美食探店'),
(2, '城市探索'),
(3, '自驾游'),
(3, '摄影圣地'),
(3, '高原旅行'),
(4, '海岛游'),
(4, '文化探索'),
(5, '文化探索'),
(5, '历史遗迹'); 
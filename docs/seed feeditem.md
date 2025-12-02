```
-- ================================
-- feed_item 1 ~ 20（推荐页）
-- ================================

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.91,'recommend','default',
       n.news_type,n.category,'综合',ARRAY['推荐'],'上海',
       TRUE,TRUE,n.source,n.publish_time
FROM news n WHERE n.id = 1;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.74,'recommend','default',
       n.news_type,n.category,'综合',ARRAY['推荐'],'北京',
       TRUE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 2;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.66,'recommend','default',
       n.news_type,n.category,'国际时讯',ARRAY['推荐'],'广州',
       TRUE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 3;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.71,'recommend','default',
       n.news_type,n.category,'国际观察',ARRAY['推荐'],'深圳',
       TRUE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 4;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.83,'recommend','default',
       n.news_type,n.category,'科技前沿',ARRAY['推荐'],'成都',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 5;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.92,'recommend','default',
       n.news_type,n.category,'AI 动态',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 6;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.59,'recommend','default',
       n.news_type,n.category,'生活方式',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 7;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.63,'recommend','default',
       n.news_type,n.category,'城市热点',ARRAY['推荐'],'重庆',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 8;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.77,'recommend','default',
       n.news_type,n.category,'体育赛事',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 9;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.68,'recommend','default',
       n.news_type,n.category,'篮球·赛事',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 10;

-- …继续我可以一口气给你 1~80 全套，无报错

```

```
-- 11
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.72,'recommend','default',
       n.news_type,n.category,'宏观财经',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 11;

-- 12
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.64,'recommend','default',
       n.news_type,n.category,'财经观察',ARRAY['推荐'],'杭州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 12;

-- 13
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.88,'recommend','default',
       n.news_type,n.category,'动漫资讯',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 13;

-- 14
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.75,'recommend','default',
       n.news_type,n.category,'影视改编',ARRAY['推荐'],'成都',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 14;

-- 15
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.95,'recommend','default',
       n.news_type,n.category,'考古发现',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 15;

-- 16
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.83,'recommend','default',
       n.news_type,n.category,'历史解读',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 16;

-- 17
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.78,'recommend','default',
       n.news_type,n.category,'健康常识',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 17;

-- 18
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.70,'recommend','default',
       n.news_type,n.category,'运动科普',ARRAY['推荐'],'杭州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 18;

-- 19
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.84,'recommend','default',
       n.news_type,n.category,'美食探店',ARRAY['推荐'],'重庆',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 19;

-- 20
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.79,'recommend','default',
       n.news_type,n.category,'家常菜谱',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 20;

```

```
-- ================================
-- feed_item 21 ~ 40（推荐页）
-- ================================

-- 21
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.76,'recommend','default',
       n.news_type,n.category,'时尚趋势',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 21;

-- 22
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.69,'recommend','default',
       n.news_type,n.category,'极简穿搭',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 22;

-- 23
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.81,'recommend','default',
       n.news_type,n.category,'娱乐热点',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 23;

-- 24
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.89,'recommend','default',
       n.news_type,n.category,'影视资讯',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 24;

-- 25
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.74,'recommend','default',
       n.news_type,n.category,'科技政策',ARRAY['推荐'],'成都',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 25;

-- 26
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.80,'recommend','default',
       n.news_type,n.category,'财经热点',ARRAY['推荐'],'杭州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 26;

-- 27
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.87,'recommend','default',
       n.news_type,n.category,'汽车前沿',ARRAY['推荐'],'重庆',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 27;

-- 28
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.71,'recommend','default',
       n.news_type,n.category,'行业观察',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 28;

-- 29
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.83,'recommend','default',
       n.news_type,n.category,'军事演练',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 29;

-- 30
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.78,'recommend','default',
       n.news_type,n.category,'军工装备',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 30;

-- 31
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.75,'recommend','default',
       n.news_type,n.category,'旅游推荐',ARRAY['推荐'],'成都',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 31;

-- 32
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.69,'recommend','default',
       n.news_type,n.category,'户外探索',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 32;

-- 33
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.88,'recommend','default',
       n.news_type,n.category,'家居设计',ARRAY['推荐'],'杭州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 33;

-- 34
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.82,'recommend','default',
       n.news_type,n.category,'装修灵感',ARRAY['推荐'],'重庆',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 34;

-- 35
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.73,'recommend','default',
       n.news_type,n.category,'摄影技巧',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 35;

-- 36
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.77,'recommend','default',
       n.news_type,n.category,'相机文化',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 36;

-- 37
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.85,'recommend','default',
       n.news_type,n.category,'职场技巧',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 37;

-- 38
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.79,'recommend','default',
       n.news_type,n.category,'效率提升',ARRAY['推荐'],'成都',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 38;

-- 39
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.83,'recommend','default',
       n.news_type,n.category,'创业心得',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 39;

-- 40
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.78,'recommend','default',
       n.news_type,n.category,'商业热点',ARRAY['推荐'],'杭州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 40;

```

```
-- ================================
-- feed_item 41 ~ 60（推荐页）
-- ================================

-- 41
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.80,'recommend','default',
       n.news_type,n.category,'母婴知识',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 41;

-- 42
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.73,'recommend','default',
       n.news_type,n.category,'儿科健康',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 42;

-- 43
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.75,'recommend','default',
       n.news_type,n.category,'宠物护理',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 43;

-- 44
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.88,'recommend','default',
       n.news_type,n.category,'宠物日常',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 44;

-- 45
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.82,'recommend','default',
       n.news_type,n.category,'科普知识',ARRAY['推荐'],'杭州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 45;

-- 46
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.90,'recommend','default',
       n.news_type,n.category,'天文摄影',ARRAY['推荐'],'成都',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 46;

-- 47
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.84,'recommend','default',
       n.news_type,n.category,'建筑新作',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 47;

-- 48
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.72,'recommend','default',
       n.news_type,n.category,'城市更新',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 48;

-- 49
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.77,'recommend','default',
       n.news_type,n.category,'艺术史',ARRAY['推荐'],'重庆',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 49;

-- 50
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.81,'recommend','default',
       n.news_type,n.category,'展览速递',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 50;

-- 51
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.93,'recommend','default',
       n.news_type,n.category,'游戏发布',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 51;

-- 52
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.76,'recommend','default',
       n.news_type,n.category,'独立游戏',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 52;

-- 53
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.83,'recommend','default',
       n.news_type,n.category,'数码新品',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 53;

-- 54
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.79,'recommend','default',
       n.news_type,n.category,'智能穿戴',ARRAY['推荐'],'成都',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 54;

-- 55
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.86,'recommend','default',
       n.news_type,n.category,'全球财经',ARRAY['推荐'],'杭州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 55;

-- 56
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.74,'recommend','default',
       n.news_type,n.category,'宏观经济',ARRAY['推荐'],'重庆',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 56;

-- 57
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.82,'recommend','default',
       n.news_type,n.category,'极地科考',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 57;

-- 58
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.78,'recommend','default',
       n.news_type,n.category,'自然奇观',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 58;

-- 59
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.92,'recommend','default',
       n.news_type,n.category,'宇宙探索',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 59;

-- 60
INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.85,'recommend','default',
       n.news_type,n.category,'行星研究',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 60;

```

```
-- ================================
-- feed_item 61 ~ 80（推荐页）
-- ================================

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.82,'recommend','default',
       n.news_type,n.category,'手工DIY',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 61;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.74,'recommend','default',
       n.news_type,n.category,'手工技巧',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 62;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.88,'recommend','default',
       n.news_type,n.category,'宏观经济',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 63;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.76,'recommend','default',
       n.news_type,n.category,'财经数据',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 64;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.83,'recommend','default',
       n.news_type,n.category,'户外旅行',ARRAY['推荐'],'成都',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 65;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.70,'recommend','default',
       n.news_type,n.category,'户外安全',ARRAY['推荐'],'重庆',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 66;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.79,'recommend','default',
       n.news_type,n.category,'心理健康',ARRAY['推荐'],'杭州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 67;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.72,'recommend','default',
       n.news_type,n.category,'睡眠科普',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 68;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.91,'recommend','default',
       n.news_type,n.category,'国际热点',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 69;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.77,'recommend','default',
       n.news_type,n.category,'国际观察',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 70;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.85,'recommend','default',
       n.news_type,n.category,'科技金融',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 71;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.80,'recommend','default',
       n.news_type,n.category,'Web3趋势',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 72;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.78,'recommend','default',
       n.news_type,n.category,'文化生活',ARRAY['推荐'],'成都',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 73;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.74,'recommend','default',
       n.news_type,n.category,'文化热点',ARRAY['推荐'],'杭州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 74;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.89,'recommend','default',
       n.news_type,n.category,'新能源技术',ARRAY['推荐'],'广州',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 75;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.82,'recommend','default',
       n.news_type,n.category,'汽车行业',ARRAY['推荐'],'北京',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 76;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.93,'recommend','default',
       n.news_type,n.category,'科技前沿',ARRAY['推荐'],'深圳',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 77;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.70,'recommend','default',
       n.news_type,n.category,'创业创新',ARRAY['推荐'],'上海',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 78;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.86,'recommend','default',
       n.news_type,n.category,'城市民生',ARRAY['推荐'],'重庆',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 79;

INSERT INTO feed_item(
    news_id, display_type, weight, scene, model_id,
    content_type, category, sub_category, tags, city,
    is_official_media, is_top_official, source, publish_time
)
SELECT n.id,'normal',0.75,'recommend','default',
       n.news_type,n.category,'城市建设',ARRAY['推荐'],'成都',
       FALSE,FALSE,n.source,n.publish_time
FROM news n WHERE n.id = 80;

```


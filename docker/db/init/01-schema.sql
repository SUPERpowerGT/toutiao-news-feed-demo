-- ================================
-- 作者表
-- ================================
CREATE TABLE IF NOT EXISTS author (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    avatar_url    VARCHAR(255),
    description   VARCHAR(255),
    certification VARCHAR(50),
    created_at    TIMESTAMP DEFAULT NOW()
);

-- ================================
-- 新闻主表
-- ================================
CREATE TABLE IF NOT EXISTS news (
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    summary      VARCHAR(255),
    news_type    VARCHAR(50) NOT NULL,   -- 👍 ENUM → string
    author_id    BIGINT REFERENCES author(id),
    source       VARCHAR(100),
    category     VARCHAR(50),
    publish_time TIMESTAMP,
    status       SMALLINT DEFAULT 1,
    created_at   TIMESTAMP DEFAULT NOW(),
    updated_at   TIMESTAMP DEFAULT NOW()
);

-- ================================
-- 新闻内容
-- ================================
CREATE TABLE IF NOT EXISTS news_content (
    news_id      BIGINT PRIMARY KEY REFERENCES news(id) ON DELETE CASCADE,
    content_html TEXT,
    content_json JSONB,
    word_count   INT
);

-- ================================
-- 多媒体表
-- ================================
CREATE TABLE IF NOT EXISTS media (
    id          BIGSERIAL PRIMARY KEY,
    news_id     BIGINT REFERENCES news(id) ON DELETE CASCADE,
    group_id    BIGINT,
    media_type  VARCHAR(50) NOT NULL,  -- 👍 ENUM → string
    url         VARCHAR(255),
    cover_url   VARCHAR(255),
    duration    INT,
    width       INT,
    height      INT,
    order_index INT,
    created_at  TIMESTAMP DEFAULT NOW()
);

-- ================================
-- 数据统计表
-- ================================
CREATE TABLE IF NOT EXISTS stats (
    news_id        BIGINT PRIMARY KEY REFERENCES news(id) ON DELETE CASCADE,
    like_count     INT DEFAULT 0,
    comment_count  INT DEFAULT 0,
    favorite_count INT DEFAULT 0,
    share_count    INT DEFAULT 0,
    play_count     INT DEFAULT 0,
    version        INT DEFAULT 0,
    updated_at     TIMESTAMP DEFAULT NOW()
);

-- ================================
-- 推荐流 feed_item
-- ================================
CREATE TABLE IF NOT EXISTS feed_item (
    id              BIGSERIAL PRIMARY KEY,
    news_id         BIGINT REFERENCES news(id) ON DELETE CASCADE,

    -- display 信息
    display_type    VARCHAR(50) NOT NULL,
    weight          FLOAT,
    scene           VARCHAR(30),
    model_id        VARCHAR(50),

    -- Android FeedItemDto 对应字段
    content_type       VARCHAR(50),      -- 对应 contentType
    category           VARCHAR(50),
    sub_category       VARCHAR(50),
    tags               TEXT[],           -- 多字符串数组
    city               VARCHAR(50),
    is_official_media  BOOLEAN DEFAULT false,
    is_top_official    BOOLEAN DEFAULT false,
    source             VARCHAR(100),

    publish_time    TIMESTAMP,
    seq_id          BIGSERIAL,
    created_at      TIMESTAMP DEFAULT NOW()
);

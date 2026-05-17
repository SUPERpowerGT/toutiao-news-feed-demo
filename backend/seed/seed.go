package seed

import (
	"database/sql"
	"encoding/json"
	"fmt"
	"strings"
	"time"
)

type authorSeed struct {
	ID            int
	Name          string
	AvatarURL     string
	Description   string
	Certification string
}

type articleSeed struct {
	Title           string
	Summary         string
	ContentType     string
	AuthorID        int
	Source          string
	Category        string
	SubCategory     string
	City            string
	Tags            []string
	IsOfficialMedia bool
	IsTopOfficial   bool
}

type contentJSON struct {
	Title      string   `json:"title"`
	Paragraphs []string `json:"paragraphs"`
	Images     []string `json:"images"`
}

type insertHelpers struct {
	newsStmt    *sql.Stmt
	feedStmt    *sql.Stmt
	mediaStmt   *sql.Stmt
	statsStmt   *sql.Stmt
	contentStmt *sql.Stmt
}

func InsertSeedData(db *sql.DB) error {
	tx, err := db.Begin()
	if err != nil {
		return fmt.Errorf("begin seed transaction error: %w", err)
	}
	defer tx.Rollback()

	resetSQL := `
        TRUNCATE TABLE
            stats,
            media,
            news_content,
            feed_item,
            news,
            author
        RESTART IDENTITY CASCADE;
    `
	if _, err := tx.Exec(resetSQL); err != nil {
		return fmt.Errorf("reset tables error: %w", err)
	}

	authors := buildAuthors()
	articles := buildArticles()

	authorStmt, err := tx.Prepare(`
        INSERT INTO author (id, name, avatar_url, description, certification)
        VALUES ($1, $2, $3, $4, NULLIF($5, ''))
    `)
	if err != nil {
		return fmt.Errorf("prepare author insert error: %w", err)
	}
	defer authorStmt.Close()

	for _, author := range authors {
		if _, err := authorStmt.Exec(author.ID, author.Name, author.AvatarURL, author.Description, author.Certification); err != nil {
			return fmt.Errorf("insert author %d error: %w", author.ID, err)
		}
	}

	helpers, err := prepareInsertHelpers(tx)
	if err != nil {
		return err
	}
	defer helpers.close()

	baseTime := time.Now().Add(-10 * time.Minute)

	for i, article := range articles {
		newsID := i + 1
		publishTime := baseTime.Add(-time.Duration(i) * 14 * time.Minute)
		weight := 0.56 + float64((i%8)+1)*0.05

		if err := insertArticle(helpers, newsID, article, publishTime, weight, i); err != nil {
			return err
		}
	}

	if err := tx.Commit(); err != nil {
		return fmt.Errorf("commit seed transaction error: %w", err)
	}

	return nil
}

func AppendRefreshBatch(db *sql.DB, count int) error {
	if count <= 0 {
		count = 5
	}
	if count > 20 {
		count = 20
	}

	tx, err := db.Begin()
	if err != nil {
		return fmt.Errorf("begin append transaction error: %w", err)
	}
	defer tx.Rollback()

	helpers, err := prepareInsertHelpers(tx)
	if err != nil {
		return err
	}
	defer helpers.close()

	var maxNewsID int
	if err := tx.QueryRow(`SELECT COALESCE(MAX(id), 0) FROM news`).Scan(&maxNewsID); err != nil {
		return fmt.Errorf("query max news id error: %w", err)
	}

	var maxPublishUnix int64
	if err := tx.QueryRow(`
        SELECT COALESCE(MAX(FLOOR(EXTRACT(EPOCH FROM publish_time))::bigint), 0)
        FROM news
    `).Scan(&maxPublishUnix); err != nil {
		return fmt.Errorf("query max publish time error: %w", err)
	}

	refreshTemplates := buildRefreshTemplates()

	baseUnix := time.Now().Unix()
	if maxPublishUnix >= baseUnix {
		baseUnix = maxPublishUnix + 1
	} else {
		baseUnix = baseUnix + 1
	}

	for i := 0; i < count; i++ {
		newsID := maxNewsID + i + 1
		publishTime := time.Unix(baseUnix+int64(i), 0)
		template := refreshTemplates[i%len(refreshTemplates)]

		article := articleSeed{
			Title:           template.Title,
			Summary:         template.Summary,
			ContentType:     template.ContentType,
			AuthorID:        template.AuthorID,
			Source:          template.Source,
			Category:        template.Category,
			SubCategory:     template.SubCategory,
			City:            template.City,
			Tags:            append([]string{}, template.Tags...),
			IsOfficialMedia: template.IsOfficialMedia,
			IsTopOfficial:   i < 5 && template.IsOfficialMedia,
		}

		weight := 0.95 + float64(i)*0.01
		if err := insertArticle(helpers, newsID, article, publishTime, weight, newsID); err != nil {
			return err
		}
	}

	if err := tx.Commit(); err != nil {
		return fmt.Errorf("commit append transaction error: %w", err)
	}

	return nil
}

func buildRefreshTemplates() []articleSeed {
	return []articleSeed{
		{"多地密集出台产业支持举措，人工智能应用落地提速", "近期多项政策与项目同步推进，企业端与场景端的合作进度明显加快。", "image", 1, "新华社快讯", "科技", "产业观察", "深圳", []string{"人工智能", "产业"}, true, true},
		{"权威发布：重点城市消费数据持续回暖，服务业景气度回升", "最新监测数据显示，线下客流与餐饮、文旅等消费场景热度继续修复。", "text", 1, "新华社快讯", "民生", "权威发布", "上海", []string{"消费", "服务业"}, true, true},
		{"国际航运价格波动趋缓，港口运转效率出现改善迹象", "多家机构表示，近期港口拥堵缓解后，跨境物流时效有所提升。", "image", 2, "环球热点榜", "国际", "航运", "新加坡", []string{"航运", "物流"}, false, false},
		{"焦点战鏖战至最后时刻，主教练赛后回应关键换人调整", "比赛末节节奏一度反转，现场观众对关键回合讨论热度持续攀升。", "video", 5, "体育风云", "体育", "焦点赛事", "北京", []string{"比赛", "赛后"}, false, false},
		{"新能源产业链再迎新进展，上游设备与终端需求同步升温", "业内普遍认为，近期订单与项目开工节奏的变化值得继续跟踪。", "image", 38, "新能源观察员", "科技", "新能源", "合肥", []string{"新能源", "制造"}, false, false},
		{"本地交通组织优化方案落地，早晚高峰通行效率有望提升", "围绕学校、商圈与地铁站周边的微循环改造已经进入实施阶段。", "image", 40, "本地事件通", "民生", "城市交通", "杭州", []string{"交通", "出行"}, false, false},
		{"科技公司密集发布新产品路线图，多模态能力成为竞争焦点", "从终端形态到模型能力，企业正围绕实际场景展开新一轮布局。", "image", 3, "科技每日说", "科技", "产品动态", "北京", []string{"多模态", "产品"}, false, false},
		{"多家机构更新市场判断，资金风格切换引发板块轮动加快", "短线情绪有所修复，但业内提醒仍需关注后续量能与政策变化。", "text", 6, "金融新观察", "财经", "市场观察", "上海", []string{"板块", "资金"}, false, false},
		{"城市更新项目施工提速，周边商业与社区配套同步升级", "多地旧改项目进入关键节点，居民对环境改善与生活便利性关注升温。", "image", 4, "城市生活圈", "民生", "城市更新", "广州", []string{"旧改", "社区"}, false, false},
		{"热门新剧释出幕后花絮，平台预约人数继续攀升", "制作团队近期频繁释放新物料，带动相关话题保持较高讨论度。", "video", 12, "影视热搜榜", "娱乐", "剧集动态", "横店", []string{"新剧", "花絮"}, false, false},
		{"考古发掘进入新阶段，专家称核心遗迹信息仍在持续整理", "多项现场资料与文物保护工作同步推进，后续成果值得期待。", "image", 8, "历史故事局", "历史", "考古进展", "西安", []string{"考古", "遗迹"}, false, false},
		{"多个商圈客流延续增长，夜间消费场景成为近期亮点", "餐饮、演出与即时零售场景联动增强，带动周末消费热度上升。", "image", 10, "美食大玩家", "生活方式", "城市消费", "长沙", []string{"夜经济", "商圈"}, false, false},
	}
}

func prepareInsertHelpers(tx *sql.Tx) (*insertHelpers, error) {
	newsStmt, err := tx.Prepare(`
        INSERT INTO news (id, title, summary, news_type, author_id, source, category, publish_time)
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
    `)
	if err != nil {
		return nil, fmt.Errorf("prepare news insert error: %w", err)
	}

	feedStmt, err := tx.Prepare(`
        INSERT INTO feed_item (
            news_id, display_type, weight, scene, model_id,
            content_type, category, sub_category, tags, city,
            is_official_media, is_top_official, source, publish_time
        )
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9::text[], $10, $11, $12, $13, $14)
    `)
	if err != nil {
		newsStmt.Close()
		return nil, fmt.Errorf("prepare feed_item insert error: %w", err)
	}

	mediaStmt, err := tx.Prepare(`
        INSERT INTO media (news_id, media_type, url, cover_url, duration, width, height, order_index)
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
    `)
	if err != nil {
		newsStmt.Close()
		feedStmt.Close()
		return nil, fmt.Errorf("prepare media insert error: %w", err)
	}

	statsStmt, err := tx.Prepare(`
        INSERT INTO stats (news_id, like_count, comment_count, favorite_count, share_count, play_count, version)
        VALUES ($1, $2, $3, $4, $5, $6, $7)
    `)
	if err != nil {
		newsStmt.Close()
		feedStmt.Close()
		mediaStmt.Close()
		return nil, fmt.Errorf("prepare stats insert error: %w", err)
	}

	contentStmt, err := tx.Prepare(`
        INSERT INTO news_content (news_id, content_html, content_json, word_count)
        VALUES ($1, $2, $3, $4)
    `)
	if err != nil {
		newsStmt.Close()
		feedStmt.Close()
		mediaStmt.Close()
		statsStmt.Close()
		return nil, fmt.Errorf("prepare news_content insert error: %w", err)
	}

	return &insertHelpers{
		newsStmt:    newsStmt,
		feedStmt:    feedStmt,
		mediaStmt:   mediaStmt,
		statsStmt:   statsStmt,
		contentStmt: contentStmt,
	}, nil
}

func (h *insertHelpers) close() {
	h.newsStmt.Close()
	h.feedStmt.Close()
	h.mediaStmt.Close()
	h.statsStmt.Close()
	h.contentStmt.Close()
}

func insertArticle(h *insertHelpers, newsID int, article articleSeed, publishTime time.Time, weight float64, statsIndex int) error {
	if _, err := h.newsStmt.Exec(
		newsID,
		article.Title,
		article.Summary,
		article.ContentType,
		article.AuthorID,
		article.Source,
		article.Category,
		publishTime,
	); err != nil {
		return fmt.Errorf("insert news %d error: %w", newsID, err)
	}

	if _, err := h.feedStmt.Exec(
		newsID,
		"recommend",
		weight,
		"home",
		"seed_v3",
		article.ContentType,
		article.Category,
		article.SubCategory,
		pgTextArray(article.Tags),
		article.City,
		article.IsOfficialMedia,
		article.IsTopOfficial,
		article.Source,
		publishTime,
	); err != nil {
		return fmt.Errorf("insert feed_item %d error: %w", newsID, err)
	}

	if err := insertMedia(h.mediaStmt, newsID, article.ContentType); err != nil {
		return fmt.Errorf("insert media for news %d error: %w", newsID, err)
	}

	if _, err := h.statsStmt.Exec(
		newsID,
		900+(statsIndex*137)%4200,
		28+(statsIndex*17)%320,
		16+(statsIndex*11)%220,
		8+(statsIndex*7)%150,
		7600+(statsIndex*2300)%125000,
		1,
	); err != nil {
		return fmt.Errorf("insert stats %d error: %w", newsID, err)
	}

	if err := insertNewsContent(h.contentStmt, newsID, article); err != nil {
		return fmt.Errorf("insert news_content %d error: %w", newsID, err)
	}

	return nil
}

func buildAuthors() []authorSeed {
	return []authorSeed{
		{1, "新华社快讯", "https://randomuser.me/api/portraits/men/11.jpg", "国家权威新闻源", "官方认证"},
		{2, "环球热点榜", "https://randomuser.me/api/portraits/women/12.jpg", "国际资讯第一线", ""},
		{3, "科技每日说", "https://randomuser.me/api/portraits/men/13.jpg", "专注科技深度报道", ""},
		{4, "城市生活圈", "https://randomuser.me/api/portraits/women/14.jpg", "本地热点新闻", ""},
		{5, "体育风云", "https://randomuser.me/api/portraits/men/15.jpg", "最全体育资讯", ""},
		{6, "金融新观察", "https://randomuser.me/api/portraits/men/21.jpg", "金融行业深度解读", ""},
		{7, "动漫次元社", "https://randomuser.me/api/portraits/women/22.jpg", "ACG 情报站", ""},
		{8, "历史故事局", "https://randomuser.me/api/portraits/men/23.jpg", "讲述历史背后的故事", ""},
		{9, "健康新生活", "https://randomuser.me/api/portraits/women/24.jpg", "健康饮食与保健资讯", ""},
		{10, "美食大玩家", "https://randomuser.me/api/portraits/men/25.jpg", "全国美食探店", ""},
		{11, "潮流时尚志", "https://randomuser.me/api/portraits/women/31.jpg", "流行趋势与时尚信息", ""},
		{12, "影视热搜榜", "https://randomuser.me/api/portraits/men/32.jpg", "影视动态与新片速递", ""},
		{13, "大数据研习社", "https://randomuser.me/api/portraits/men/33.jpg", "大数据领域深度分享", ""},
		{14, "汽车观察室", "https://randomuser.me/api/portraits/men/34.jpg", "国内外汽车行业资讯", ""},
		{15, "国际军事眼", "https://randomuser.me/api/portraits/men/35.jpg", "军事热点解读", ""},
		{16, "旅行环球派", "https://randomuser.me/api/portraits/women/41.jpg", "旅行目的地探秘", ""},
		{17, "家居搭配师", "https://randomuser.me/api/portraits/women/42.jpg", "家居美学与生活方式", ""},
		{18, "摄影新视界", "https://randomuser.me/api/portraits/men/43.jpg", "摄影技巧与作品欣赏", ""},
		{19, "职场生存指南", "https://randomuser.me/api/portraits/women/44.jpg", "职场成长秘籍", ""},
		{20, "创业者说", "https://randomuser.me/api/portraits/men/45.jpg", "创业故事与行业分析", ""},
		{21, "母婴成长日记", "https://randomuser.me/api/portraits/women/51.jpg", "母婴知识与育儿科普", ""},
		{22, "宠物乐园", "https://randomuser.me/api/portraits/men/52.jpg", "萌宠日常与养宠知识", ""},
		{23, "科学知识局", "https://randomuser.me/api/portraits/men/53.jpg", "科普类知识分享", ""},
		{24, "建筑观察台", "https://randomuser.me/api/portraits/men/54.jpg", "建筑行业洞察", ""},
		{25, "艺术拾遗录", "https://randomuser.me/api/portraits/women/55.jpg", "艺术与文化鉴赏", ""},
		{26, "游戏情报站", "https://randomuser.me/api/portraits/men/61.jpg", "游戏动态与攻略分享", ""},
		{27, "数码新玩意", "https://randomuser.me/api/portraits/men/62.jpg", "数码产品评测", ""},
		{28, "财经热搜榜", "https://randomuser.me/api/portraits/men/63.jpg", "最新财经动态", ""},
		{29, "地理观测者", "https://randomuser.me/api/portraits/men/64.jpg", "地理科普与世界观察", ""},
		{30, "宇宙探索号", "https://randomuser.me/api/portraits/men/65.jpg", "宇宙科学与天文资讯", ""},
		{31, "手工爱好者", "https://randomuser.me/api/portraits/women/71.jpg", "手工制作兴趣分享", ""},
		{32, "金融日报", "https://randomuser.me/api/portraits/men/72.jpg", "每日金融快讯", ""},
		{33, "户外大玩家", "https://randomuser.me/api/portraits/men/73.jpg", "户外探险与装备分享", ""},
		{34, "心理小课堂", "https://randomuser.me/api/portraits/women/74.jpg", "心理学知识与案例分析", ""},
		{35, "全球热点速递", "https://randomuser.me/api/portraits/men/75.jpg", "全球新闻一网打尽", ""},
		{36, "区块链研习者", "https://randomuser.me/api/portraits/men/81.jpg", "区块链技术资讯", ""},
		{37, "文化随笔集", "https://randomuser.me/api/portraits/women/82.jpg", "文化观点与随笔", ""},
		{38, "新能源观察员", "https://randomuser.me/api/portraits/men/83.jpg", "新能源发展趋势", ""},
		{39, "业界风向标", "https://randomuser.me/api/portraits/men/84.jpg", "行业趋势解读", ""},
		{40, "本地事件通", "https://randomuser.me/api/portraits/women/85.jpg", "本地资讯热点", ""},
	}
}

func buildArticles() []articleSeed {
	return []articleSeed{
		{"AI 峰会今日召开，全球科技巨头齐聚上海", "本次大会聚焦大模型竞争与通用智能趋势，引发全球技术圈关注。", "image", 1, "新华社快讯", "科技", "峰会", "上海", []string{"AI", "峰会"}, true, true},
		{"长三角铁路客流创新高 春节返程高峰提前到来", "铁路部门预计本周客流量将持续攀升，部分线路加开临时列车。", "image", 1, "新华社快讯", "社会", "交通", "杭州", []string{"返程", "铁路"}, true, true},
		{"权威发布：多地推进新型基础设施建设", "多项重点工程进入加速落地阶段，产业链有望持续受益。", "text", 1, "新华社快讯", "时政", "权威发布", "北京", []string{"基建", "政策"}, true, true},
		{"官方通报：重点民生项目建设进展顺利", "围绕教育、医疗和养老的多项民生项目正在稳步推进。", "text", 1, "新华社快讯", "民生", "政策通报", "深圳", []string{"民生", "发布"}, true, true},
		{"焦点观察：外贸新政落地带动港口吞吐量回升", "沿海地区多座港口吞吐量明显提升，外贸企业订单恢复。", "image", 1, "新华社快讯", "财经", "外贸", "广州", []string{"港口", "外贸"}, true, true},
		{"欧洲多国遭遇强风暴侵袭 交通大面积瘫痪", "罕见风暴影响多国，机场高速相继关闭，民众被要求减少出行。", "image", 2, "环球热点榜", "国际", "天气", "巴黎", []string{"风暴", "出行"}, false, false},
		{"中东局势再度紧张，多方呼吁尽快恢复谈判", "多国外交官正在推动新的会谈进程，试图降低地区紧张局势。", "image", 2, "环球热点榜", "国际", "局势", "迪拜", []string{"中东", "会谈"}, false, false},
		{"国产旗舰手机发布 搭载自研芯片性能大增", "新款旗舰发布后，相关话题迅速登上热搜榜前列。", "image", 3, "科技每日说", "科技", "手机", "北京", []string{"手机", "芯片"}, false, false},
		{"AI 绘图工具升级，多模态生成能力显著增强", "最新升级的模型可以同时生成图片和短视频，引发讨论。", "video", 3, "科技每日说", "科技", "AI 动态", "上海", []string{"AI", "多模态"}, false, false},
		{"杭州发布新的公共交通优惠政策", "符合条件的市民将享受公交地铁 8 折票价优惠，政策将于下月实施。", "image", 4, "城市生活圈", "民生", "交通优惠", "杭州", []string{"公交", "地铁"}, false, false},
		{"本地夜市火爆，年轻人成为主力消费群体", "多地夜市开设直播区，吸引大量游客参与互动。", "image", 4, "城市生活圈", "生活方式", "夜经济", "长沙", []string{"夜市", "消费"}, false, false},
		{"中国短道速滑队打破赛季最佳成绩", "在世界赛场上再获佳绩，队员状态正佳。", "video", 5, "体育风云", "体育", "冰雪", "哈尔滨", []string{"短道速滑", "比赛"}, false, false},
		{"CBA 强强对话上演，广东队惊险取胜", "最后两分钟双方交替领先，现场氛围火爆。", "image", 5, "体育风云", "体育", "篮球", "东莞", []string{"CBA", "广东"}, false, false},
		{"人民币汇率短期波动，专家：属正常市场表现", "分析人士表示短期波动不必过度解读，长期走势仍保持稳定。", "text", 6, "金融新观察", "财经", "汇率", "上海", []string{"汇率", "市场"}, false, false},
		{"A 股三连涨，科技和新能源板块领涨", "市场信心回暖，多家机构上调下半年预期。", "image", 6, "金融新观察", "财经", "股市", "深圳", []string{"A股", "新能源"}, false, false},
		{"超人气动画新 PV 发布，粉丝热情高涨", "制作组公布新一季更新计划，画面质量大幅提升。", "image", 7, "动漫次元社", "娱乐", "动画", "东京", []string{"动画", "PV"}, false, false},
		{"经典动画改编真人剧，定档暑期上映", "官方今日公开主演名单，引发大量讨论。", "video", 7, "动漫次元社", "娱乐", "影视改编", "东京", []string{"真人剧", "改编"}, false, false},
		{"考古新发现：千年古墓中的珍贵文物曝光", "专家表示部分文物具有极高历史研究价值。", "image", 8, "历史故事局", "历史", "考古", "西安", []string{"文物", "古墓"}, false, false},
		{"史学家解读：三国时期真正的经济实力构成", "通过最新论文数据复盘当时区域的经济格局。", "text", 8, "历史故事局", "历史", "史学", "洛阳", []string{"三国", "历史"}, false, false},
		{"专家提醒：冬季感冒高发，做好防护很关键", "建议补充维生素、多喝水、多通风。", "image", 9, "健康新生活", "健康", "冬季防护", "北京", []string{"感冒", "健康"}, false, false},
		{"坚持运动的好处：研究表明心血管健康显著提升", "每天坚持 30 分钟运动即可降低多项健康风险。", "text", 9, "健康新生活", "健康", "运动科普", "深圳", []string{"运动", "心血管"}, false, false},
		{"长沙夜市美食盘点：这 10 家店值得一试", "从烧烤到甜品，应有尽有，网友纷纷打卡。", "image", 10, "美食大玩家", "美食", "探店", "长沙", []string{"美食", "夜市"}, false, false},
		{"五分钟学会做正宗台湾卤肉饭", "简单食材即可做出地道风味，附详细步骤。", "text", 10, "美食大玩家", "美食", "菜谱", "台北", []string{"卤肉饭", "家常菜"}, false, false},
		{"2025 春季时装周开幕，设计师新秀大放异彩", "本届时装周聚焦环保材料和未来主义风格，引发关注。", "image", 11, "潮流时尚志", "时尚", "时装周", "米兰", []string{"时尚", "秀场"}, false, false},
		{"今年最火的穿搭趋势：极简风卷土重来", "时尚达人分享 2025 极简穿搭技巧，易学又高级。", "text", 11, "潮流时尚志", "时尚", "穿搭", "上海", []string{"穿搭", "极简"}, false, false},
		{"票房爆冷！话题大作上映首日表现不佳", "业内人士分析剧情质量或成主要原因。", "image", 12, "影视热搜榜", "娱乐", "电影", "北京", []string{"票房", "电影"}, false, false},
		{"明星新剧即将开机，剧组公布完整演员阵容", "官方发布花絮视频，粉丝期待值拉满。", "video", 12, "影视热搜榜", "娱乐", "新剧", "横店", []string{"电视剧", "演员"}, false, false},
		{"AI 监管框架发布，算法透明度成核心要求", "新的政策将影响多个行业的数据处理方式。", "text", 13, "大数据研习社", "科技", "监管", "北京", []string{"算法", "监管"}, false, false},
		{"大型数据公司市值飙升，背后原因是什么？", "分析师认为多行业数字化需求推动了增长。", "image", 13, "大数据研习社", "财经", "数据产业", "上海", []string{"数据", "市值"}, false, false},
		{"全新电动 SUV 发布，续航突破 900 公里", "业内认为这款车将重新定义电车市场天花板。", "image", 14, "汽车观察室", "汽车", "新能源车", "上海", []string{"电动车", "SUV"}, false, false},
		{"车企财报出炉，销量回暖但利润承压", "多家车企强调将持续投入智能驾驶研发。", "text", 14, "汽车观察室", "汽车", "财报", "广州", []string{"车企", "财报"}, false, false},
		{"多国举行联合军演，战机编队画面曝光", "本次军演规模空前，涉及多种作战力量。", "video", 15, "国际军事眼", "军事", "军演", "地中海", []string{"军演", "战机"}, false, false},
		{"国际防务展今日开幕，新型装备亮相", "多款无人机和防空系统成为展会焦点。", "image", 15, "国际军事眼", "军事", "防务展", "阿布扎比", []string{"装备", "无人机"}, false, false},
		{"盘点 2025 年最值得去的五大旅行目的地", "从极光之城到海岛秘境，总有一处让你心动。", "text", 16, "旅行环球派", "旅游", "目的地", "赫尔辛基", []string{"旅行", "目的地"}, false, false},
		{"徒步爱好者的天堂！新线路正式对外开放", "路线难度适中，沿途风景壮丽。", "image", 16, "旅行环球派", "旅游", "徒步", "香格里拉", []string{"徒步", "风景"}, false, false},
		{"北欧风装修指南：简单 3 步营造高级感", "软装搭配技巧让小户型也能轻松变美。", "text", 17, "家居搭配师", "家居", "装修", "成都", []string{"家居", "北欧风"}, false, false},
		{"2025 家居流行色公布，温柔色系大热", "设计师推荐将流行色与木质家具搭配使用。", "image", 17, "家居搭配师", "家居", "流行色", "杭州", []string{"配色", "家居"}, false, false},
		{"如何用手机拍出大片感？专业摄影师分享技巧", "掌握构图与光线，随手也能出氛围感大片。", "image", 18, "摄影新视界", "摄影", "拍摄技巧", "上海", []string{"摄影", "手机"}, false, false},
		{"百年历史的老相机拍卖，最终价格令人惊讶", "收藏者称其具备极高纪念价值。", "text", 18, "摄影新视界", "摄影", "拍卖", "伦敦", []string{"相机", "收藏"}, false, false},
		{"职场沟通中最容易踩坑的三件事，你中招了吗？", "专家指出沟通技巧是职场晋升的关键能力之一。", "text", 19, "职场生存指南", "职场", "沟通", "北京", []string{"职场", "沟通"}, false, false},
		{"如何提升效率？这 5 个时间管理方法值得一试", "简单有效的技巧帮助你更轻松完成工作任务。", "image", 19, "职场生存指南", "职场", "效率", "深圳", []string{"效率", "管理"}, false, false},
		{"创业如何从 0 到 1？资深创业者分享心得", "从市场选择到团队搭建，这些经验你一定用得上。", "text", 20, "创业者说", "商业", "创业", "上海", []string{"创业", "经验"}, false, false},
		{"融资环境变化，初创企业如何应对？", "业内人士建议企业保持现金流和核心竞争力。", "image", 20, "创业者说", "商业", "融资", "北京", []string{"融资", "初创"}, false, false},
		{"宝宝辅食怎么添加？营养师给出专业建议", "不同月龄段的宝宝辅食需求不同，家长需根据情况调整。", "text", 21, "母婴成长日记", "母婴", "辅食", "广州", []string{"育儿", "辅食"}, false, false},
		{"孩子发烧不要慌！儿科医生教你三步应对", "家长最关心的退烧问题，医生给出科学方法。", "image", 21, "母婴成长日记", "健康", "儿科", "南京", []string{"孩子", "发烧"}, false, false},
		{"狗狗一直掉毛怎么办？兽医：这 3 点最关键", "换季掉毛是正常现象，但也可能是营养问题导致。", "text", 22, "宠物乐园", "宠物", "养宠", "深圳", []string{"狗狗", "掉毛"}, false, false},
		{"萌猫日常合集爆火，网友：治愈系天花板", "短视频平台宠物内容持续走红，带动相关话题讨论度提升。", "video", 22, "宠物乐园", "宠物", "萌宠", "上海", []string{"猫咪", "治愈"}, false, false},
		{"科学家揭示：为何我们会做梦？", "最新研究表明，梦境与大脑记忆修复密切相关。", "text", 23, "科学知识局", "科学", "睡眠", "北京", []string{"做梦", "大脑"}, false, false},
		{"空间望远镜捕捉到罕见星云照片，震撼发布", "高清照片展示星云壮丽细节，吸引大量天文爱好者关注。", "image", 23, "科学知识局", "科学", "天文", "乌鲁木齐", []string{"望远镜", "星云"}, false, false},
		{"全球最高木结构建筑完工，引发建筑界热议", "这种新型结构实现环保与稳定性的平衡。", "image", 24, "建筑观察台", "建筑", "新材料", "苏黎世", []string{"建筑", "木结构"}, false, false},
		{"老城区改造工程启动，专家：保留历史肌理很重要", "改造方案将兼顾居住改善与文化保护。", "text", 24, "建筑观察台", "建筑", "旧改", "苏州", []string{"改造", "历史"}, false, false},
		{"被遗忘的画家：女性艺术家的复兴之路", "越来越多博物馆开始重新展出昔日被忽略的女性艺术家作品。", "text", 25, "艺术拾遗录", "艺术", "展览", "巴黎", []string{"艺术家", "女性"}, false, false},
		{"现代艺术展今日开幕，沉浸式体验成最大亮点", "大量新媒体艺术作品吸引年轻观众前来打卡。", "image", 25, "艺术拾遗录", "艺术", "现代艺术", "上海", []string{"展览", "沉浸式"}, false, false},
		{"年度最期待游戏定档，玩家：终于来了！", "制作组公布全新战斗系统细节，引发热烈讨论。", "video", 26, "游戏情报站", "游戏", "新游", "东京", []string{"游戏", "定档"}, false, false},
		{"独立游戏爆火，三人团队打造千万级口碑", "独立游戏产业再次证明创意才是核心驱动力。", "text", 26, "游戏情报站", "游戏", "独立游戏", "成都", []string{"独立游戏", "口碑"}, false, false},
		{"折叠屏手机销量激增，厂商集体加码新品布局", "折叠屏行业竞争进入新阶段，产品创新成为关键。", "image", 27, "数码新玩意", "科技", "折叠屏", "深圳", []string{"折叠屏", "数码"}, false, false},
		{"智能穿戴设备再升级，健康监测成最大亮点", "新发布的手表支持更多监测项目，引发关注。", "text", 27, "数码新玩意", "科技", "穿戴", "上海", []string{"手表", "健康监测"}, false, false},
		{"全球股市普涨，科技板块领跑市场", "多国股指创今年新高，投资者信心增强。", "image", 28, "财经热搜榜", "财经", "全球市场", "纽约", []string{"股市", "科技板块"}, false, false},
		{"通胀压力仍存，专家建议保持审慎投资策略", "多家机构预计下季度通胀将逐步回落。", "text", 28, "财经热搜榜", "财经", "通胀", "伦敦", []string{"通胀", "投资"}, false, false},
		{"南极冰川发生断裂，科学家紧急评估影响", "该断裂或将影响未来海平面变化趋势。", "image", 29, "地理观测者", "科学", "气候", "南极", []string{"冰川", "海平面"}, false, false},
		{"世界上最神秘的三处地理奇观，你去过几个？", "这些自然奇观长期吸引探索者深入研究。", "text", 29, "地理观测者", "探索", "奇观", "冰岛", []string{"地理", "奇观"}, false, false},
		{"火星车传回最新高清地表照片，细节令人震惊", "科学团队称照片中疑似存在沉积结构。", "image", 30, "宇宙探索号", "宇宙", "火星", "休斯敦", []string{"火星", "探测"}, false, false},
		{"天文学家发现类地行星大气组成重要线索", "这项发现可能会改变我们对生命存在形式的理解。", "text", 30, "宇宙探索号", "宇宙", "行星", "昆明", []string{"天文", "行星"}, false, false},
		{"简单 5 步做出精美香薰蜡烛，零基础也能完成", "所需材料都很容易买到，是入门最友好的手工项目之一。", "text", 31, "手工爱好者", "手工", "DIY", "厦门", []string{"手工", "蜡烛"}, false, false},
		{"手工达人分享绝美编织包教程，春季新宠", "编织包因其清新风格深受年轻人喜爱。", "image", 31, "手工爱好者", "手工", "编织", "杭州", []string{"手作", "编织包"}, false, false},
		{"美联储政策会议召开，未来利率走向成焦点", "市场预计短期内不会大幅调整利率，但仍存不确定性。", "text", 32, "金融日报", "财经", "利率", "华盛顿", []string{"美联储", "利率"}, false, false},
		{"人民币兑美元小幅回升，投资者信心增强", "多项经济数据表现稳定，汇率走势趋于平稳。", "image", 32, "金融日报", "财经", "汇市", "香港", []string{"人民币", "美元"}, false, false},
		{"新晋徒步路线曝光：绝美雪山路线适合轻装备", "路线风光壮丽，非常适合周末短途徒步。", "image", 33, "户外大玩家", "户外", "徒步", "丽江", []string{"雪山", "徒步"}, false, false},
		{"野营安全指南：新手最容易忽略的五件事", "专业玩家提醒，安全意识始终是第一位的。", "text", 33, "户外大玩家", "户外", "露营", "阿勒泰", []string{"露营", "安全"}, false, false},
		{"如何缓解焦虑？心理学家推荐三种有效方法", "通过调整呼吸节奏与专注注意力可以显著降低焦虑感。", "text", 34, "心理小课堂", "心理", "情绪管理", "北京", []string{"焦虑", "心理"}, false, false},
		{"长期熬夜对心理健康影响被证实，年轻人需警惕", "研究显示睡眠不足会增加情绪波动风险。", "image", 34, "心理小课堂", "心理", "睡眠", "上海", []string{"熬夜", "睡眠"}, false, false},
		{"南亚多地暴雨引发洪灾，救援正在进行", "大量房屋受损，当地政府已启动紧急应对机制。", "image", 35, "全球热点速递", "国际", "灾害", "加德满都", []string{"暴雨", "洪灾"}, false, false},
		{"全球粮食供应链面临挑战，专家呼吁加强合作", "国际组织表示，跨国协同是缓解危机的重要手段。", "text", 35, "全球热点速递", "国际", "粮食安全", "内罗毕", []string{"粮食", "供应链"}, false, false},
		{"加密货币市场再度波动，投资者态度趋于谨慎", "多国监管政策调整引发市场短期震荡。", "text", 36, "区块链研习者", "科技", "加密货币", "新加坡", []string{"加密货币", "Web3"}, false, false},
		{"Web3 游戏生态增长迅速，用户规模翻倍", "去中心化游戏为玩家带来全新体验，市场关注度提升。", "image", 36, "区块链研习者", "科技", "Web3 游戏", "首尔", []string{"Web3", "游戏"}, false, false},
		{"关于慢生活的文化解读：为何越来越多人选择放慢脚步", "快节奏带来的压力促使人们重新审视日常生活方式。", "text", 37, "文化随笔集", "文化", "慢生活", "大理", []string{"文化", "生活方式"}, false, false},
		{"古典文学阅读热潮来袭，线下书店销量上涨", "读者对经典作品的兴趣明显提升，相关活动热度不减。", "image", 37, "文化随笔集", "文化", "阅读", "南京", []string{"文学", "书店"}, false, false},
		{"太阳能储能系统迎重大突破，成本有望再降低", "新技术的应用将推动新能源产业进一步发展。", "image", 38, "新能源观察员", "科技", "储能", "合肥", []string{"太阳能", "储能"}, false, false},
		{"新能源汽车销量连涨六个月，行业保持高景气度", "专家称政策与市场需求共同推动行业增长。", "text", 38, "新能源观察员", "汽车", "新能源车", "深圳", []string{"新能源车", "销量"}, false, false},
		{"国内互联网巨头公布新战略，AI 仍是核心方向", "新战略强调智能化转型，加大云计算投入力度。", "text", 39, "业界风向标", "科技", "公司战略", "杭州", []string{"互联网", "AI"}, false, false},
		{"多家初创公司融资成功，行业信心回暖", "本季度融资规模呈现明显上升趋势。", "image", 39, "业界风向标", "商业", "融资", "上海", []string{"初创", "融资"}, false, false},
		{"地铁新线路开通，市民出行更加便捷", "新开通线路将极大缓解城市交通压力。", "image", 40, "本地事件通", "民生", "地铁", "成都", []string{"地铁", "出行"}, false, false},
		{"旧小区电梯更新工程启动，居民拍手称赞", "多栋楼将陆续完成电梯升级施工。", "text", 40, "本地事件通", "民生", "社区改造", "重庆", []string{"旧改", "电梯"}, false, false},
	}
}

func insertMedia(stmt *sql.Stmt, newsID int, contentType string) error {
	image1 := fmt.Sprintf("https://picsum.photos/seed/news-%d-a/800/600", newsID)
	image2 := fmt.Sprintf("https://picsum.photos/seed/news-%d-b/800/600", newsID)
	videoURL := "https://sample-videos.com/video321/mp4/720/big_buck_bunny_720p_1mb.mp4"

	switch contentType {
	case "video":
		if _, err := stmt.Exec(newsID, "video", videoURL, image1, 64, nil, nil, 1); err != nil {
			return err
		}
		if _, err := stmt.Exec(newsID, "image", image2, nil, nil, 800, 600, 2); err != nil {
			return err
		}
	case "image":
		if _, err := stmt.Exec(newsID, "image", image1, nil, nil, 800, 600, 1); err != nil {
			return err
		}
		if newsID%3 != 0 {
			if _, err := stmt.Exec(newsID, "image", image2, nil, nil, 800, 600, 2); err != nil {
				return err
			}
		}
	default:
		if newsID%2 == 0 {
			if _, err := stmt.Exec(newsID, "image", image1, nil, nil, 800, 600, 1); err != nil {
				return err
			}
		}
	}

	return nil
}

func insertNewsContent(stmt *sql.Stmt, newsID int, article articleSeed) error {
	imageURL := fmt.Sprintf("https://picsum.photos/seed/content-%d/800/500", newsID)
	html := fmt.Sprintf(
		"<h2>%s</h2><p>%s</p><p>%s 已成为当日平台高热度话题，相关讨论持续升温。</p><img src=\"%s\"/><p>编辑部将持续跟进该事件的后续进展与深度解读。</p>",
		article.Title,
		article.Summary,
		article.Source,
		imageURL,
	)

	payload := contentJSON{
		Title: article.Title,
		Paragraphs: []string{
			article.Summary,
			fmt.Sprintf("%s 围绕 %s 持续带来更新内容，话题讨论度明显提升。", article.Source, article.SubCategory),
			fmt.Sprintf("围绕 %s 与 %s 的相关信息，平台将继续补充更多进展。", article.Category, article.City),
		},
		Images: []string{imageURL},
	}

	jsonBytes, err := json.Marshal(payload)
	if err != nil {
		return err
	}

	wordCount := 150 + len([]rune(article.Title)) + len([]rune(article.Summary))
	_, err = stmt.Exec(newsID, html, string(jsonBytes), wordCount)
	return err
}

func pgTextArray(tags []string) string {
	if len(tags) == 0 {
		return "{}"
	}

	escaped := make([]string, 0, len(tags))
	for _, tag := range tags {
		escaped = append(escaped, strings.ReplaceAll(tag, `"`, `\"`))
	}

	return "{" + strings.Join(escaped, ",") + "}"
}

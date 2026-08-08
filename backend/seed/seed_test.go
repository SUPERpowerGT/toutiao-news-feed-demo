package seed

import "testing"

func TestEnsureChannelMinimums(t *testing.T) {
	const minimum = 20
	articles := ensureChannelMinimums(buildArticles(), minimum)

	channels := map[string]func(articleSeed) bool{
		"recommend": func(articleSeed) bool { return true },
		"following": func(article articleSeed) bool { return article.Category == "关注" },
		"hot":       func(article articleSeed) bool { return article.Category == "热榜" },
		"video":     func(article articleSeed) bool { return article.ContentType == "video" },
		"shenzhen":  func(article articleSeed) bool { return article.City == "深圳" },
		"featured":  func(article articleSeed) bool { return article.Category == "精选" },
		"image":     func(article articleSeed) bool { return article.ContentType == "image" },
		"war":       func(article articleSeed) bool { return article.Category == "抗战" },
		"tech":      func(article articleSeed) bool { return article.Category == "科技" },
		"sports":    func(article articleSeed) bool { return article.Category == "体育" },
		"finance":   func(article articleSeed) bool { return article.Category == "财经" },
	}

	for channel, matches := range channels {
		count := 0
		for _, article := range articles {
			if matches(article) {
				count++
			}
		}
		if count < minimum {
			t.Errorf("channel %s has %d articles, want at least %d", channel, count, minimum)
		}
	}
}

func TestEnsureChannelMinimumsIsIdempotent(t *testing.T) {
	once := ensureChannelMinimums(buildArticles(), 20)
	twice := ensureChannelMinimums(once, 20)

	if len(twice) != len(once) {
		t.Fatalf("second pass added %d unexpected articles", len(twice)-len(once))
	}
}

func TestGeneratedArticlesAreNotOfficialTopStories(t *testing.T) {
	original := buildArticles()
	articles := ensureChannelMinimums(original, 20)

	for index, article := range articles[len(original):] {
		if article.IsTopOfficial || article.IsOfficialMedia {
			t.Errorf("generated article %d must not be an official top story", index)
		}
	}
}

package domain

type NewsContent struct {
	NewsID      int64       `json:"news_id"`
	ContentHTML string      `json:"content_html"`
	ContentJSON interface{} `json:"content_json"`
	WordCount   int         `json:"word_count"`
}

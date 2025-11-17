# **06. API Interface Design（demo）**

本章节定义后端与 Android 客户端之间的接口协议，包括请求方式、参数说明、返回格式、错误码规范。
 本系统采用 **RESTful API**，支持 JSON 数据交互。

------

# **6.1 接口总览**

| 模块     | API                    | 方法 | 说明                          |
| -------- | ---------------------- | ---- | ----------------------------- |
| 推荐流   | `/api/feed`            | GET  | 获取推荐流内容（cursor 分页） |
| 内容详情 | `/api/news/{id}`       | GET  | 获取单条新闻详情              |
| 多媒体   | `/api/media?news_id=`  | GET  | 获取新闻相关的媒体资源        |
| 作者信息 | `/api/author/{id}`     | GET  | 获取作者信息                  |
| 统计数据 | `/api/stats/{id}`      | GET  | 获取新闻统计数据              |
| 点赞操作 | `/api/stats/{id}/like` | POST | 点赞（可选）                  |

------

# **6.2 统一返回结构**

所有 API 采用统一格式：

```
{
  "code": 0,
  "message": "ok",
  "data": {}
}
```

| 字段    | 说明                     |
| ------- | ------------------------ |
| code    | 0 表示成功，其他表示错误 |
| message | 错误或成功信息           |
| data    | 实际返回内容             |

------

# **6.3 推荐流接口**

## 📌 `GET /api/feed`

### **用途：**

客户端首页拉取推荐流（支持 cursor 分页）。

------

### **请求参数（Query）**

| 字段   | 类型   | 必填 | 说明                          |
| ------ | ------ | ---- | ----------------------------- |
| scene  | string | 否   | recommend / shenzhen / local  |
| cursor | string | 否   | 下一页分页游标（base64 编码） |
| limit  | int    | 否   | 每页数量（默认 10）           |

------

### **返回结构**

```
{
  "code": 0,
  "message": "ok",
  "data": {
    "items": [
      {
        "feed_id": 10001,
        "news_id": 20001,
        "display_type": "multi_image",
        "weight": 0.89,
        "publish_time": "2025-11-17T11:22:00",
        "seq_id": 981181,
        "title": "深圳天气回暖，市民纷纷出门踏青",
        "summary": "今日天气晴朗...",
        "media": [
          {
            "url": "https://cdn.xx.com/img1.jpg",
            "width": 1080,
            "height": 720
          }
        ],
        "author": {
          "id": 12,
          "name": "新华社"
        },
        "stats": {
          "liked": 132,
          "comment_count": 23
        }
      }
    ],
    "next_cursor": "eyJwd...==" 
  }
}
```

------

# **6.4 新闻详情接口**

## 📌 `GET /api/news/{id}`

### 用途：

获取新闻正文内容（HTML 或 JSON 格式）

------

### 返回结构

```
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 20001,
    "title": "深圳天气回暖",
    "author_id": 12,
    "publish_time": "2025-11-17T12:00:00",
    "content_html": "<p>今日...</p>",
    "content_json": [
      { "type": "text", "content": "今日天气回暖..." },
      { "type": "image", "url": "https://cdn.xx.com/img1.jpg", "width": 1080, "height": 720 }
    ]
  }
}
```

------

# **6.5 获取媒体接口**

## 📌 `GET /api/media?news_id=20001`

返回卡片媒体（用于首页）：

```
{
  "data": [
    {
      "id": 1,
      "media_type": "image",
      "url": "https://cdn.xx.com/img1.jpg",
      "width": 1080,
      "height": 720,
      "order_index": 1
    }
  ]
}
```

------

# **6.6 获取作者信息**

## 📌 `GET /api/author/{id}`

```
{
  "data": {
    "id": 12,
    "name": "新华社",
    "avatar_url": "...",
    "description": "官方媒体"
  }
}
```

------

# **6.7 获取统计数据（stats）**

## 📌 `GET /api/stats/{news_id}`

```
{
  "data": {
    "news_id": 20001,
    "like_count": 120,
    "comment_count": 20,
    "favorite_count": 18,
    "share_count": 12,
    "play_count": 211
  }
}
```

------

# **6.8 点赞接口（可选功能）**

## 📌 `POST /api/stats/{id}/like`

```
{
  "code": 0,
  "message": "success"
}
```
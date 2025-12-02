package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.xuziyi.toutiaoandroid.R
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import com.xuziyi.toutiaoandroid.ui.feed.cards.components.*

@Composable
fun VideoCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    val media = item.media.firstOrNull()
    val coverUrl = media?.coverUrl ?: media?.url
    val duration = media?.duration ?: 0
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {

        // ====== 标题（统一组件） ======
        CardTitle(
            title = item.title,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ====== 视频封面图区域 ======
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {

            // ⭐ 完全优化版封面图 —— 强制 decode 限制
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(coverUrl)
                    .size(400)               // ⭐ 核心：限制解码尺寸，避免原图解码
                    .crossfade(true)
                    .allowHardware(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_video_placeholder),
                error = painterResource(R.drawable.ic_video_placeholder)
            )

            // 右下角视频时长
            VideoDurationLabel(
                sec = duration,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
            )

            // ▶️ 播放按钮（中间）
            Image(
                painter = painterResource(R.drawable.ic_play_circle),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ====== 作者信息 + 点赞 + 更多 ======
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CardAuthorInfo(item)
            Spacer(modifier = Modifier.weight(1f))
            CardActionRow(item)
        }

        Spacer(modifier = Modifier.height(12.dp))

        CardDivider()
    }
}

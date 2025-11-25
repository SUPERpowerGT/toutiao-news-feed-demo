package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.xuziyi.toutiaoandroid.R
import com.xuziyi.toutiaoandroid.common.util.TimeAgoFormatter
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

@Composable
fun VideoCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    val coverUrl = item.media.firstOrNull()?.coverUrl ?: item.media.firstOrNull()?.url
    val duration = item.media.firstOrNull()?.duration ?: 0

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {

        // ====== 标题（最多两行） ======
        Text(
            text = item.title,
            fontSize = 19.sp,
            color = Color(0xFF222222),
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ====== 封面图区域 ======
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {

            // 封面图
            AsyncImage(
                model = coverUrl,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_video_placeholder),
                error = painterResource(R.drawable.ic_video_placeholder)
            )

            // 视频时长（右下角）
            Text(
                text = formatDuration(duration),
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
                    .background(Color(0x80000000), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )

            // 播放按钮
            Image(
                painter = painterResource(R.drawable.ic_play_circle),
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ====== 作者 + 时间 + 点赞 + 更多 ======
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 作者头像
            AsyncImage(
                model = item.author.avatarUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_default_avatar),
                error = painterResource(R.drawable.ic_default_avatar)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 作者名
            Text(
                text = item.author.name,
                fontSize = 12.sp,
                color = Color(0xFF777777)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 发布时间
            Text(
                text = TimeAgoFormatter.format(item.publishTime),
                fontSize = 12.sp,
                color = Color(0xFFAAAAAA)
            )

            Spacer(modifier = Modifier.weight(1f))

            // 点赞 ❤️ + 数量
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_like),
                    contentDescription = null,
                    tint = Color(0xFF777777),
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = item.stats.likeCount.toString(),
                    fontSize = 12.sp,
                    color = Color(0xFF777777)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 更多 ···
            Icon(
                painter = painterResource(R.drawable.ic_more_vert),
                contentDescription = null,
                tint = Color(0xFF777777),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ====== 底部分割线 ======
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.6.dp)
                .background(Color(0xFFEFEFEF))
        )
    }
}

// 视频时长格式化，例如 "05:36"
private fun formatDuration(sec: Int): String {
    val m = sec / 60
    val s = sec % 60
    return "%02d:%02d".format(m, s)
}

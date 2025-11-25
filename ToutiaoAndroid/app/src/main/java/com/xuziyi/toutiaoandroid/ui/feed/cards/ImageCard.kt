package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.xuziyi.toutiaoandroid.R
import com.xuziyi.toutiaoandroid.common.util.TimeAgoFormatter
import com.xuziyi.toutiaoandroid.domain.model.FeedItem

@Composable
fun ImageCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    val coverUrl = item.media.firstOrNull()?.url
    val authorAvatar = item.author.avatarUrl

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // ========== 第一行：标题 + 图片 ==========
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            // ---- 左边：标题 ----
            Text(
                text = item.title,
                fontSize = 19.sp,
                lineHeight = 20.sp,
                color = Color(0xFF222222),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 12.dp)
            )

            // ---- 右边：封面图（固定尺寸） ----
            AsyncImage(
                model = coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .height(80.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEAEAEA)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ========== 第二行：作者信息 + 点赞数 + 右侧更多按钮 ==========
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 作者头像
            AsyncImage(
                model = authorAvatar,
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

            // 发布时间（你后面可换成相对时间，如 "6天前"）
            Text(
                text = TimeAgoFormatter.format(item.publishTime),
                fontSize = 12.sp,
                color = Color(0xFFAAAAAA)
            )

            Spacer(modifier = Modifier.weight(1f))

            //点赞数量
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_like),
                    contentDescription = null,
                    tint = Color(0xFF777777),
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${item.stats.likeCount}",
                    fontSize = 12.sp,
                    color = Color(0xFF777777)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // 更多 ···
            Icon(
                painter = painterResource(id = R.drawable.ic_more_vert),
                contentDescription = null,
                tint = Color(0xFF777777),
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ========== 底部分割线 ==========
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.6.dp)
                .background(Color(0xFFEFEFEF))
        )
    }
}

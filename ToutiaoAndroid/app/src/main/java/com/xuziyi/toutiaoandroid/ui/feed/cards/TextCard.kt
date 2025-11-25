package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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


/**
 * 普通混合流的文字新闻（非前 5 条官方区）
 */
@Composable
fun TextCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        // ===== 标题 =====
        Text(
            text = item.title,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF222222),
            lineHeight = 22.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ===== 作者 + 时间 + 点赞 + 更多 =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 作者头像
            AsyncImage(
                model = item.author.avatarUrl,
                contentDescription = null,
                placeholder = painterResource(R.drawable.ic_default_avatar),
                error = painterResource(R.drawable.ic_default_avatar),
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 作者名称
            Text(
                text = item.author.name,
                fontSize = 12.sp,
                color = Color(0xFF777777)
            )

            Spacer(modifier = Modifier.width(6.dp))

            // 发布时间（相对时间）
            Text(
                text = TimeAgoFormatter.format(item.publishTime),
                fontSize = 12.sp,
                color = Color(0xFFAAAAAA)
            )

            Spacer(modifier = Modifier.weight(1f))

            // 点赞 ❤️
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

            Spacer(modifier = Modifier.width(10.dp))

            // 更多 ···
            Icon(
                painter = painterResource(R.drawable.ic_more_vert),
                contentDescription = null,
                tint = Color(0xFF777777),
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ===== 底部分割线 =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.6.dp)
                .background(Color(0xFFEFEFEF))
        )
    }
}

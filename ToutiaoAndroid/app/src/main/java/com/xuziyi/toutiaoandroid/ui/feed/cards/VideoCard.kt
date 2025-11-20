package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import android.R

@Composable
fun VideoCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    val media = item.media.firstOrNull()

    Card(modifier = modifier.padding(8.dp)) {

        Column(modifier = Modifier.padding(12.dp)) {

            // 标题
            Text(text = item.title)

            Spacer(modifier = Modifier.height(8.dp))

            // 视频封面区域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {

                // 封面图片（先用 placeholder）
                Image(
                    painter = painterResource(R.drawable.ic_menu_gallery),
                    contentDescription = item.title
                )

                // 播放按钮
                Image(
                    painter = painterResource(R.drawable.ic_media_play),
                    contentDescription = "Play",
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 作者
            Text(text = "作者：${item.author.name}")

            // 发布时间
            Text(text = "发布时间：${item.publishTime}")
        }
    }
}

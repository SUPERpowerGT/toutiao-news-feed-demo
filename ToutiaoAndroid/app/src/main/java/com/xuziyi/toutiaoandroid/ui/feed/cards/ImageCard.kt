package com.xuziyi.toutiaoandroid.ui.feed.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xuziyi.toutiaoandroid.domain.model.FeedItem
import android.R

@Composable
fun ImageCard(
    item: FeedItem,
    modifier: Modifier = Modifier
) {
    val cover = item.media.firstOrNull()

    Card(modifier = modifier.padding(8.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {

            // 标题
            Text(text = item.title)

            // 图片（临时 placeholder）
            Image(
                painter = painterResource(R.drawable.ic_menu_gallery),
                contentDescription = item.title
            )

            // 作者
            Text(text = "作者：${item.author.name}")

            // 时间
            Text(text = "发布时间：${item.publishTime}")
        }
    }
}
